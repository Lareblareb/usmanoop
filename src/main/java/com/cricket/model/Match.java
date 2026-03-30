package com.cricket.model;

import java.io.*;

/**
 * Represents a complete cricket match between two teams.
 * Manages both innings and provides save/load functionality via
 * Java object serialisation so a match can be paused and resumed.
 */
public class Match implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String matchTitle;
    private final Team team1;
    private final Team team2;
    private final int maxOvers;

    private Innings firstInnings;
    private Innings secondInnings;
    private int currentInningsNumber; // 1 or 2
    private boolean complete;

    /**
     * Creates a new match.
     *
     * @param matchTitle descriptive title (e.g. "Pakistan vs India – T20")
     * @param team1      the team batting first
     * @param team2      the team batting second
     * @param maxOvers   overs per innings (0 = unlimited / Test match)
     */
    public Match(String matchTitle, Team team1, Team team2, int maxOvers) {
        this.matchTitle = matchTitle;
        this.team1 = team1;
        this.team2 = team2;
        this.maxOvers = maxOvers;
        this.firstInnings = new Innings(team1, team2, maxOvers);
        this.secondInnings = null;
        this.currentInningsNumber = 1;
        this.complete = false;
    }

    /**
     * Starts the second innings (team2 bats, team1 bowls).
     * Should be called after the first innings is complete.
     */
    public void startSecondInnings() {
        secondInnings = new Innings(team2, team1, maxOvers);
        currentInningsNumber = 2;
    }

    /**
     * Returns the innings currently in progress.
     *
     * @return the active Innings object
     */
    public Innings getCurrentInnings() {
        return currentInningsNumber == 1 ? firstInnings : secondInnings;
    }

    /**
     * Computes and returns the match result string.
     * Returns "In Progress" if the match is not yet finished.
     */
    public String getResult() {
        if (!complete) return "In Progress";
        if (secondInnings == null) return "Match not completed";

        int score1 = firstInnings.getTotalRuns();
        int score2 = secondInnings.getTotalRuns();

        if (score1 > score2) {
            return team1.getName() + " won by " + (score1 - score2) + " runs";
        } else if (score2 > score1) {
            int wicketsLeft = (team2.getPlayerCount() - 1) - secondInnings.getWickets();
            return team2.getName() + " won by " + wicketsLeft + " wicket(s)";
        } else {
            return "Match Tied";
        }
    }

    // ── Persistence ──────────────────────────────────────────────────────────

    /**
     * Serialises this match to a file so it can be resumed later.
     *
     * @param filename path to the file to write
     * @throws IOException if writing fails
     */
    public void saveToFile(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            oos.writeObject(this);
        }
    }

    /**
     * Loads a previously saved match from a serialised file.
     *
     * @param filename path to the saved file
     * @return the deserialised Match object
     * @throws IOException            if reading fails
     * @throws ClassNotFoundException if the file is not a valid Match file
     */
    public static Match loadFromFile(String filename)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filename))) {
            return (Match) ois.readObject();
        }
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    /** Returns the match title. */
    public String getMatchTitle() { return matchTitle; }

    /** Returns team 1 (the team that bats first). */
    public Team getTeam1() { return team1; }

    /** Returns team 2. */
    public Team getTeam2() { return team2; }

    /** Returns the first innings. */
    public Innings getFirstInnings() { return firstInnings; }

    /** Returns the second innings (null if not yet started). */
    public Innings getSecondInnings() { return secondInnings; }

    /** Returns the current innings number (1 or 2). */
    public int getCurrentInningsNumber() { return currentInningsNumber; }

    /** Returns the maximum overs per innings. */
    public int getMaxOvers() { return maxOvers; }

    /** Returns true if the match has ended. */
    public boolean isComplete() { return complete; }

    /** Marks the match as complete or incomplete. */
    public void setComplete(boolean complete) { this.complete = complete; }
}
