package com.cricket.ui;

import com.cricket.model.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main JavaFX application for the Cricket Match Scorer and Stats Tracker.
 *
 * The UI is organised into four tabs:
 *   1. Setup   – configure teams, player names, and match options
 *   2. Scoring – record deliveries ball-by-ball
 *   3. Scorecard – full batting and bowling scorecard
 *   4. Save/Load – persist and restore match state
 */
public class CricketApp extends Application {

    // ── State ────────────────────────────────────────────────────────────────
    private Match match;

    // ── Setup Tab fields ─────────────────────────────────────────────────────
    private TextField matchTitleField;
    private TextField team1NameField;
    private TextField team2NameField;
    private TextField oversField;
    private ListView<String> team1PlayerList;
    private ListView<String> team2PlayerList;
    private TextField addPlayerTeam1Field;
    private TextField addPlayerTeam2Field;
    private List<String> team1Players = new ArrayList<>();
    private List<String> team2Players = new ArrayList<>();

    // ── Scoring Tab fields ───────────────────────────────────────────────────
    private Label inningsLabel;
    private Label scoreLabel;
    private Label currentBatsmenLabel;
    private Label currentBowlerLabel;
    private Label ballByBallLabel;
    private ComboBox<String> bowlerSelector;
    private ToggleGroup deliveryTypeGroup;
    private RadioButton rbNormal, rbWide, rbNoBall;
    private ToggleGroup wicketGroup;
    private RadioButton rbNoWicket, rbWicket;
    private ComboBox<String> runsSelector;
    private Button recordButton;
    private Button startInnings2Button;
    private Button endMatchButton;
    private Label targetLabel;

    // ── Scorecard Tab ────────────────────────────────────────────────────────
    private TextArea scorecardArea;

    // ── Save/Load Tab ────────────────────────────────────────────────────────
    private Label saveStatusLabel;

    // ── Root ─────────────────────────────────────────────────────────────────
    private TabPane tabPane;

    // ========================================================================
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cricket Match Scorer & Stats Tracker");

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(
                buildSetupTab(),
                buildScoringTab(),
                buildScorecardTab(),
                buildSaveLoadTab()
        );

        Scene scene = new Scene(tabPane, 850, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ========================================================================
    // TAB 1 – SETUP
    // ========================================================================

    /**
     * Builds the Setup tab where the user enters teams, players, and match settings.
     */
    private Tab buildSetupTab() {
        Tab tab = new Tab("Setup");

        VBox root = new VBox(12);
        root.setPadding(new Insets(16));

        // ── Match settings ────────────────────────────────────────────────
        Label matchHeader = header("Match Settings");
        matchTitleField = new TextField("Test Match");
        matchTitleField.setPromptText("Match title");
        oversField = new TextField("20");
        oversField.setPromptText("Overs per innings (0 = unlimited)");

        GridPane matchGrid = new GridPane();
        matchGrid.setHgap(10);
        matchGrid.setVgap(8);
        matchGrid.addRow(0, new Label("Match Title:"), matchTitleField);
        matchGrid.addRow(1, new Label("Overs per innings:"), oversField);

        // ── Team 1 ────────────────────────────────────────────────────────
        Label team1Header = header("Team 1 (bats first)");
        team1NameField = new TextField("Team A");
        team1NameField.setPromptText("Team 1 name");

        team1PlayerList = new ListView<>(FXCollections.observableArrayList(team1Players));
        team1PlayerList.setPrefHeight(120);

        addPlayerTeam1Field = new TextField();
        addPlayerTeam1Field.setPromptText("Player name");
        Button addT1 = new Button("Add Player");
        addT1.setOnAction(e -> addPlayer(team1Players, team1PlayerList, addPlayerTeam1Field));

        Button removeT1 = new Button("Remove Selected");
        removeT1.setOnAction(e -> {
            int idx = team1PlayerList.getSelectionModel().getSelectedIndex();
            if (idx >= 0) {
                team1Players.remove(idx);
                team1PlayerList.setItems(FXCollections.observableArrayList(team1Players));
            }
        });

        HBox t1Controls = new HBox(6, addPlayerTeam1Field, addT1, removeT1);

        // ── Team 2 ────────────────────────────────────────────────────────
        Label team2Header = header("Team 2");
        team2NameField = new TextField("Team B");
        team2NameField.setPromptText("Team 2 name");

        team2PlayerList = new ListView<>(FXCollections.observableArrayList(team2Players));
        team2PlayerList.setPrefHeight(120);

        addPlayerTeam2Field = new TextField();
        addPlayerTeam2Field.setPromptText("Player name");
        Button addT2 = new Button("Add Player");
        addT2.setOnAction(e -> addPlayer(team2Players, team2PlayerList, addPlayerTeam2Field));

        Button removeT2 = new Button("Remove Selected");
        removeT2.setOnAction(e -> {
            int idx = team2PlayerList.getSelectionModel().getSelectedIndex();
            if (idx >= 0) {
                team2Players.remove(idx);
                team2PlayerList.setItems(FXCollections.observableArrayList(team2Players));
            }
        });

        HBox t2Controls = new HBox(6, addPlayerTeam2Field, addT2, removeT2);

        // ── Start match ───────────────────────────────────────────────────
        Button startBtn = new Button("Start Match");
        startBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size:14;");
        startBtn.setPrefWidth(200);
        startBtn.setOnAction(e -> startMatch());

        Label hint = new Label("Minimum 2 players per team required.");
        hint.setTextFill(Color.GRAY);

        root.getChildren().addAll(
                matchHeader, matchGrid,
                team1Header, new Label("Team name:"), team1NameField,
                new Label("Players:"), team1PlayerList, t1Controls,
                team2Header, new Label("Team name:"), team2NameField,
                new Label("Players:"), team2PlayerList, t2Controls,
                new Separator(),
                hint, startBtn
        );

        tab.setContent(new ScrollPane(root));
        return tab;
    }

    /**
     * Adds a player to the given list and refreshes the ListView.
     */
    private void addPlayer(List<String> players, ListView<String> view, TextField field) {
        String name = field.getText().trim();
        if (!name.isEmpty()) {
            players.add(name);
            view.setItems(FXCollections.observableArrayList(players));
            field.clear();
        }
    }

    /**
     * Validates input and initialises the Match object, then switches to the Scoring tab.
     */
    private void startMatch() {
        if (team1Players.size() < 2 || team2Players.size() < 2) {
            alert("Each team must have at least 2 players.");
            return;
        }
        int overs;
        try {
            overs = Integer.parseInt(oversField.getText().trim());
            if (overs < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            alert("Overs must be a non-negative integer (0 = unlimited).");
            return;
        }

        Team t1 = new Team(team1NameField.getText().trim());
        Team t2 = new Team(team2NameField.getText().trim());
        team1Players.forEach(t1::addPlayer);
        team2Players.forEach(t2::addPlayer);

        match = new Match(matchTitleField.getText().trim(), t1, t2, overs);
        refreshScoringTab();
        tabPane.getSelectionModel().select(1); // switch to Scoring tab
    }

    // ========================================================================
    // TAB 2 – SCORING
    // ========================================================================

    /**
     * Builds the Scoring tab for recording deliveries ball-by-ball.
     */
    private Tab buildScoringTab() {
        Tab tab = new Tab("Scoring");

        VBox root = new VBox(12);
        root.setPadding(new Insets(16));

        // ── Scoreboard display ────────────────────────────────────────────
        inningsLabel = new Label("Innings 1");
        inningsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        scoreLabel = new Label("Score: 0/0 (0.0 ov)");
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        scoreLabel.setTextFill(Color.DARKBLUE);

        targetLabel = new Label("");
        targetLabel.setTextFill(Color.DARKRED);
        targetLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        currentBatsmenLabel = new Label("Batsmen: –");
        currentBowlerLabel  = new Label("Bowler:  –");

        ballByBallLabel = new Label("This over: ");
        ballByBallLabel.setWrapText(true);

        // ── Bowler selector ───────────────────────────────────────────────
        Label bowlerLbl = new Label("Select Bowler:");
        bowlerSelector = new ComboBox<>();
        bowlerSelector.setPrefWidth(220);
        bowlerSelector.setOnAction(e -> {
            if (match != null) {
                int idx = bowlerSelector.getSelectionModel().getSelectedIndex();
                if (idx >= 0) match.getCurrentInnings().setCurrentBowlerIdx(idx);
                refreshScoringDisplay();
            }
        });

        // ── Delivery type ─────────────────────────────────────────────────
        deliveryTypeGroup = new ToggleGroup();
        rbNormal = new RadioButton("Normal");  rbNormal.setToggleGroup(deliveryTypeGroup); rbNormal.setSelected(true);
        rbWide   = new RadioButton("Wide");    rbWide.setToggleGroup(deliveryTypeGroup);
        rbNoBall = new RadioButton("No Ball"); rbNoBall.setToggleGroup(deliveryTypeGroup);
        HBox deliveryBox = new HBox(12, new Label("Delivery:"), rbNormal, rbWide, rbNoBall);

        // ── Wicket ────────────────────────────────────────────────────────
        wicketGroup = new ToggleGroup();
        rbNoWicket = new RadioButton("No Wicket"); rbNoWicket.setToggleGroup(wicketGroup); rbNoWicket.setSelected(true);
        rbWicket   = new RadioButton("Wicket!");   rbWicket.setToggleGroup(wicketGroup);
        rbWide.selectedProperty().addListener((obs, o, n) -> { if (n) rbNoWicket.setSelected(true); });
        HBox wicketBox = new HBox(12, new Label("Wicket:"), rbNoWicket, rbWicket);

        // ── Runs ──────────────────────────────────────────────────────────
        runsSelector = new ComboBox<>(FXCollections.observableArrayList(
                "0", "1", "2", "3", "4", "5", "6"));
        runsSelector.setValue("0");
        HBox runsBox = new HBox(10, new Label("Runs scored:"), runsSelector);
        runsBox.setAlignment(Pos.CENTER_LEFT);

        // ── Record button ─────────────────────────────────────────────────
        recordButton = new Button("Record Delivery");
        recordButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size:13;");
        recordButton.setPrefWidth(180);
        recordButton.setOnAction(e -> recordDelivery());

        // ── Innings controls ──────────────────────────────────────────────
        startInnings2Button = new Button("End Innings 1 / Start Innings 2");
        startInnings2Button.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white;");
        startInnings2Button.setDisable(true);
        startInnings2Button.setOnAction(e -> beginSecondInnings());

        endMatchButton = new Button("End Match");
        endMatchButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
        endMatchButton.setDisable(true);
        endMatchButton.setOnAction(e -> endMatch());

        HBox inningsButtons = new HBox(12, startInnings2Button, endMatchButton);

        root.getChildren().addAll(
                inningsLabel, scoreLabel, targetLabel,
                currentBatsmenLabel, currentBowlerLabel, ballByBallLabel,
                new Separator(),
                new HBox(8, bowlerLbl, bowlerSelector),
                deliveryBox, wicketBox, runsBox,
                recordButton,
                new Separator(),
                inningsButtons
        );

        tab.setContent(new ScrollPane(root));
        return tab;
    }

    /**
     * Refreshes all Scoring tab widgets to reflect the current match state.
     * Called after starting a match or loading a saved match.
     */
    private void refreshScoringTab() {
        if (match == null) return;
        Innings inn = match.getCurrentInnings();

        inningsLabel.setText("Innings " + match.getCurrentInningsNumber()
                + "  –  " + inn.getBattingTeam().getName() + " batting");

        // Populate bowler selector
        List<String> bowlerNames = new ArrayList<>();
        for (int i = 0; i < inn.getBowlingTeam().getPlayerCount(); i++) {
            bowlerNames.add(inn.getBowlingTeam().getBowler(i).getName());
        }
        bowlerSelector.setItems(FXCollections.observableArrayList(bowlerNames));
        if (inn.getCurrentBowlerIdx() >= 0) {
            bowlerSelector.getSelectionModel().select(inn.getCurrentBowlerIdx());
        }

        startInnings2Button.setDisable(match.getCurrentInningsNumber() != 1);
        endMatchButton.setDisable(match.getCurrentInningsNumber() != 2);

        if (match.getCurrentInningsNumber() == 2 && match.getFirstInnings() != null) {
            int target = match.getFirstInnings().getTotalRuns() + 1;
            targetLabel.setText("Target: " + target + "  |  Need: "
                    + (target - inn.getTotalRuns()) + " from "
                    + ((inn.getMaxOvers() > 0
                    ? (inn.getMaxOvers() * 6 - inn.getBallsBowled()) + " balls"
                    : "unlimited overs")));
        } else {
            targetLabel.setText("");
        }

        refreshScoringDisplay();
    }

    /**
     * Updates the live scoreboard labels (score, batsmen, bowler, this-over).
     */
    private void refreshScoringDisplay() {
        if (match == null) return;
        Innings inn = match.getCurrentInnings();

        scoreLabel.setText("Score: " + inn.getScoreString());
        currentBatsmenLabel.setText(
                "* " + inn.getBattingTeam().getBatsman(inn.getStrikerIdx()).getName()
                + "  |  " + inn.getBattingTeam().getBatsman(inn.getNonStrikerIdx()).getName());

        if (inn.getCurrentBowlerIdx() >= 0) {
            Bowler b = inn.getBowlingTeam().getBowler(inn.getCurrentBowlerIdx());
            currentBowlerLabel.setText("Bowler: " + b.getName()
                    + "  " + b.getWickets() + "-" + b.getRunsConceded()
                    + "  (" + b.getOvers() + "." + b.getRemainderBalls() + " ov)");
        }

        // Show last 6–12 deliveries for the ongoing over
        List<String> log = inn.getBallByBall();
        int overStart = (inn.getBallsBowled() / 6) * 6;
        // approximate: last entries since over began
        StringBuilder sb = new StringBuilder("This over: ");
        for (int i = Math.max(0, log.size() - (log.size() - overStart)); i < log.size(); i++) {
            sb.append(log.get(i)).append(" ");
        }
        ballByBallLabel.setText(sb.toString());

        // Update target label for 2nd innings
        if (match.getCurrentInningsNumber() == 2 && match.getFirstInnings() != null) {
            int target = match.getFirstInnings().getTotalRuns() + 1;
            int needed = target - inn.getTotalRuns();
            if (needed <= 0) {
                targetLabel.setText("Target reached!");
            } else {
                targetLabel.setText("Target: " + target + "  |  Need: " + needed
                        + (inn.getMaxOvers() > 0
                        ? "  from " + (inn.getMaxOvers() * 6 - inn.getBallsBowled()) + " balls"
                        : ""));
            }
        }
    }

    /**
     * Reads the UI controls and records a delivery against the current innings.
     */
    private void recordDelivery() {
        if (match == null) { alert("No match in progress. Go to Setup first."); return; }
        Innings inn = match.getCurrentInnings();
        if (inn.isComplete()) { alert("This innings is already complete."); return; }
        if (inn.getCurrentBowlerIdx() < 0) { alert("Please select a bowler first."); return; }

        boolean isWide   = rbWide.isSelected();
        boolean isNoBall = rbNoBall.isSelected();
        boolean isWicket = rbWicket.isSelected() && !isWide; // no wicket on wide
        int runs = Integer.parseInt(runsSelector.getValue());

        inn.recordDelivery(runs, isWide, isNoBall, isWicket);

        // Reset controls for next delivery
        rbNormal.setSelected(true);
        rbNoWicket.setSelected(true);
        runsSelector.setValue("0");

        // Check auto-completion conditions
        if (inn.isComplete()) {
            if (match.getCurrentInningsNumber() == 1) {
                alert("First innings complete!\n" + inn.getBattingTeam().getName()
                        + " scored " + inn.getScoreString()
                        + "\nClick 'End Innings 1 / Start Innings 2' to continue.");
                startInnings2Button.setDisable(false);
            } else {
                // Check if target reached during second innings
                int score2 = inn.getTotalRuns();
                int score1 = match.getFirstInnings().getTotalRuns();
                if (score2 > score1) {
                    match.setComplete(true);
                    alert("Match over!\n" + match.getResult());
                    refreshScorecardTab();
                } else {
                    match.setComplete(true);
                    alert("Match over!\n" + match.getResult());
                    refreshScorecardTab();
                }
                endMatchButton.setDisable(false);
            }
        }

        // Also check if target exceeded mid-innings (2nd innings won)
        if (match.getCurrentInningsNumber() == 2 && match.getSecondInnings() != null) {
            int score2 = match.getSecondInnings().getTotalRuns();
            int score1 = match.getFirstInnings().getTotalRuns();
            if (score2 > score1) {
                match.setComplete(true);
                alert("Match over!\n" + match.getResult());
                refreshScorecardTab();
            }
        }

        refreshScoringDisplay();
        refreshScorecardTab();
    }

    /**
     * Ends the first innings and initialises the second innings.
     */
    private void beginSecondInnings() {
        if (match == null) return;
        match.startSecondInnings();
        startInnings2Button.setDisable(true);
        endMatchButton.setDisable(false);
        refreshScoringTab();
        alert("Second innings started!\n"
                + match.getTeam2().getName() + " needs "
                + (match.getFirstInnings().getTotalRuns() + 1) + " to win.");
    }

    /**
     * Manually ends the match and shows the result.
     */
    private void endMatch() {
        if (match == null) return;
        match.setComplete(true);
        refreshScorecardTab();
        alert("Match result:\n" + match.getResult());
        tabPane.getSelectionModel().select(2);
    }

    // ========================================================================
    // TAB 3 – SCORECARD
    // ========================================================================

    /**
     * Builds the Scorecard tab that shows the full batting and bowling figures.
     */
    private Tab buildScorecardTab() {
        Tab tab = new Tab("Scorecard");

        VBox root = new VBox(8);
        root.setPadding(new Insets(12));

        scorecardArea = new TextArea();
        scorecardArea.setEditable(false);
        scorecardArea.setFont(Font.font("Courier New", 12));
        scorecardArea.setPrefRowCount(30);

        Button refreshBtn = new Button("Refresh Scorecard");
        refreshBtn.setOnAction(e -> refreshScorecardTab());

        root.getChildren().addAll(refreshBtn, scorecardArea);
        tab.setContent(root);
        return tab;
    }

    /**
     * Regenerates the full scorecard text from the current match state.
     */
    private void refreshScorecardTab() {
        if (match == null) { scorecardArea.setText("No match in progress."); return; }

        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(match.getMatchTitle()).append(" ===\n\n");

        appendInningsCard(sb, match.getFirstInnings(), "1st Innings");
        if (match.getSecondInnings() != null) {
            sb.append("\n");
            appendInningsCard(sb, match.getSecondInnings(), "2nd Innings");
        }

        if (match.isComplete()) {
            sb.append("\n─────────────────────────────────────────\n");
            sb.append("RESULT: ").append(match.getResult()).append("\n");
        }

        scorecardArea.setText(sb.toString());
    }

    /**
     * Appends one innings' batting and bowling scorecard to the StringBuilder.
     */
    private void appendInningsCard(StringBuilder sb, Innings inn, String label) {
        sb.append("── ").append(label).append("  –  ")
          .append(inn.getBattingTeam().getName())
          .append("  ──────────────────────────────────────\n");
        sb.append(String.format("%-18s %6s  %4s  %3s  %3s  %6s  %s\n",
                "Batsman", "Runs", "Balls", "4s", "6s", "SR", "Dismissal"));
        sb.append("─".repeat(75)).append("\n");

        int dismissed = 0;
        for (Batsman b : inn.getBattingTeam().getBatsmen()) {
            if (b.getBallsFaced() > 0 || b.isOut()) {
                sb.append(b.getStats()).append("\n");
                if (b.isOut()) dismissed++;
            }
        }
        sb.append("─".repeat(75)).append("\n");
        sb.append(String.format("Extras: %d   Total: %s\n", inn.getExtras(), inn.getScoreString()));

        sb.append("\nBowling:\n");
        sb.append(String.format("%-18s %5s %5s %5s %5s %7s %4s %4s\n",
                "Bowler", "W", "R", "O", "M", "Econ", "Wd", "Nb"));
        sb.append("─".repeat(75)).append("\n");
        for (Bowler bw : inn.getBowlingTeam().getBowlers()) {
            if (bw.getOvers() > 0 || bw.getRemainderBalls() > 0) {
                sb.append(bw.getStats()).append("\n");
            }
        }
    }

    // ========================================================================
    // TAB 4 – SAVE / LOAD
    // ========================================================================

    /**
     * Builds the Save/Load tab for persisting and restoring match state.
     */
    private Tab buildSaveLoadTab() {
        Tab tab = new Tab("Save / Load");

        VBox root = new VBox(14);
        root.setPadding(new Insets(16));

        saveStatusLabel = new Label("No match saved yet.");
        saveStatusLabel.setTextFill(Color.GRAY);

        Button saveBtn = new Button("Save Match to File…");
        saveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size:13;");
        saveBtn.setPrefWidth(200);
        saveBtn.setOnAction(e -> saveMatch());

        Button loadBtn = new Button("Load Match from File…");
        loadBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size:13;");
        loadBtn.setPrefWidth(200);
        loadBtn.setOnAction(e -> loadMatch());

        Label infoLabel = new Label(
                "Saving serialises the entire match to a .cricket file.\n"
                + "You can load it later to resume exactly where you left off.");
        infoLabel.setTextFill(Color.DIMGRAY);
        infoLabel.setWrapText(true);

        root.getChildren().addAll(
                header("Save / Load Match"),
                infoLabel,
                new Separator(),
                saveBtn, loadBtn,
                saveStatusLabel
        );

        tab.setContent(root);
        return tab;
    }

    /**
     * Opens a file chooser and saves the current match to the chosen file.
     */
    private void saveMatch() {
        if (match == null) { alert("No match to save. Start a match first."); return; }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Match");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Cricket Match Files", "*.cricket"));
        File file = chooser.showSaveDialog(null);

        if (file != null) {
            try {
                match.saveToFile(file.getAbsolutePath());
                saveStatusLabel.setText("Saved to: " + file.getName());
                saveStatusLabel.setTextFill(Color.GREEN);
            } catch (Exception ex) {
                saveStatusLabel.setText("Save failed: " + ex.getMessage());
                saveStatusLabel.setTextFill(Color.RED);
            }
        }
    }

    /**
     * Opens a file chooser and loads a previously saved match.
     */
    private void loadMatch() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load Match");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Cricket Match Files", "*.cricket"));
        File file = chooser.showOpenDialog(null);

        if (file != null) {
            try {
                match = Match.loadFromFile(file.getAbsolutePath());
                refreshScoringTab();
                refreshScorecardTab();
                saveStatusLabel.setText("Loaded: " + file.getName());
                saveStatusLabel.setTextFill(Color.GREEN);
                tabPane.getSelectionModel().select(1);
            } catch (Exception ex) {
                saveStatusLabel.setText("Load failed: " + ex.getMessage());
                saveStatusLabel.setTextFill(Color.RED);
            }
        }
    }

    // ========================================================================
    // HELPERS
    // ========================================================================

    /** Creates a bold header label. */
    private Label header(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        return lbl;
    }

    /** Shows a simple information alert dialog. */
    private void alert(String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    // ========================================================================
    public static void main(String[] args) {
        launch(args);
    }
}
