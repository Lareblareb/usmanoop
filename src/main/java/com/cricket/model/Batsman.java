package com.cricket.model;

/**
 * Represents a cricket player in their batting role.
 * Tracks runs, balls faced, boundaries, and dismissal information.
 */
public class Batsman extends Player {
    private static final long serialVersionUID = 1L;

    private int runs;
    private int ballsFaced;
    private int fours;
    private int sixes;
    private boolean out;
    private String dismissalInfo;

    /**
     * Creates a Batsman with zero stats and "not out" status.
     *
     * @param name         player name
     * @param playerNumber batting order position
     */
    public Batsman(String name, int playerNumber) {
        super(name, playerNumber);
        this.runs = 0;
        this.ballsFaced = 0;
        this.fours = 0;
        this.sixes = 0;
        this.out = false;
        this.dismissalInfo = "not out";
    }

    /**
     * Records runs scored off a legal delivery.
     * Automatically tracks fours (4) and sixes (6).
     *
     * @param r runs scored (0-6)
     */
    public void addRuns(int r) {
        runs += r;
        ballsFaced++;
        if (r == 4) fours++;
        if (r == 6) sixes++;
    }

    /**
     * Increments balls faced without adding runs (dot ball or wicket delivery
     * where runs are not credited to the batsman).
     */
    public void addBallFaced() {
        ballsFaced++;
    }

    /**
     * Marks the batsman as dismissed.
     *
     * @param info dismissal description (e.g. "b Smith", "run out")
     */
    public void setOut(String info) {
        this.out = true;
        this.dismissalInfo = info;
    }

    /** Returns total runs scored. */
    public int getRuns() { return runs; }

    /** Returns total balls faced. */
    public int getBallsFaced() { return ballsFaced; }

    /** Returns number of fours hit. */
    public int getFours() { return fours; }

    /** Returns number of sixes hit. */
    public int getSixes() { return sixes; }

    /** Returns whether the batsman is dismissed. */
    public boolean isOut() { return out; }

    /** Returns the dismissal description string. */
    public String getDismissalInfo() { return dismissalInfo; }

    /**
     * Calculates and returns the batsman's strike rate.
     *
     * @return strike rate (runs per 100 balls), or 0.0 if no balls faced
     */
    public double getStrikeRate() {
        if (ballsFaced == 0) return 0.0;
        return (runs * 100.0) / ballsFaced;
    }

    /**
     * Returns a one-line batting scorecard entry.
     * Format: Name: runs(balls) 4s:X 6s:X SR:XX.X [dismissal]
     */
    @Override
    public String getStats() {
        return String.format("%-18s %3d(%3d)  4s:%-2d  6s:%-2d  SR:%5.1f  [%s]",
                getName(), runs, ballsFaced, fours, sixes, getStrikeRate(), dismissalInfo);
    }
}
