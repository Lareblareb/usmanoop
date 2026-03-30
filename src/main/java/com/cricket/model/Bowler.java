package com.cricket.model;

/**
 * Represents a cricket player in their bowling role.
 * Tracks overs bowled, runs conceded, wickets, extras, and maidens.
 */
public class Bowler extends Player {
    private static final long serialVersionUID = 1L;

    private int ballsBowled;       // legal deliveries only
    private int runsConceded;      // includes extras charged to bowler
    private int wickets;
    private int wides;
    private int noBalls;
    private int maidens;
    private int currentOverBalls;  // legal balls in the ongoing over
    private int currentOverRuns;   // runs (incl extras) in the ongoing over

    /**
     * Creates a Bowler with all statistics at zero.
     *
     * @param name         player name
     * @param playerNumber bowling order position
     */
    public Bowler(String name, int playerNumber) {
        super(name, playerNumber);
    }

    /**
     * Records a single delivery bowled by this bowler.
     *
     * @param runs      runs scored off the delivery (excluding wide/no-ball penalty)
     * @param isWide    true if the delivery is a wide
     * @param isNoBall  true if the delivery is a no-ball
     * @param isWicket  true if a wicket was taken on this delivery
     */
    public void addDelivery(int runs, boolean isWide, boolean isNoBall, boolean isWicket) {
        // Legal deliveries advance the over counter
        if (!isWide && !isNoBall) {
            ballsBowled++;
            currentOverBalls++;
        }

        // Add runs to bowler's figures; extras carry a +1 penalty run
        runsConceded += runs;
        currentOverRuns += runs;

        if (isWide) {
            wides++;
            runsConceded++;       // penalty run
            currentOverRuns++;
        }
        if (isNoBall) {
            noBalls++;
            runsConceded++;       // penalty run
            currentOverRuns++;
        }
        if (isWicket) {
            wickets++;
        }

        // Check if over is complete (6 legal deliveries)
        if (currentOverBalls == 6) {
            if (currentOverRuns == 0) maidens++;
            currentOverBalls = 0;
            currentOverRuns = 0;
        }
    }

    /** Returns completed overs bowled. */
    public int getOvers() { return ballsBowled / 6; }

    /** Returns the ball count within the current incomplete over. */
    public int getRemainderBalls() { return ballsBowled % 6; }

    /** Returns total runs conceded (including extras charged to the bowler). */
    public int getRunsConceded() { return runsConceded; }

    /** Returns total wickets taken. */
    public int getWickets() { return wickets; }

    /** Returns number of wides bowled. */
    public int getWides() { return wides; }

    /** Returns number of no-balls bowled. */
    public int getNoBalls() { return noBalls; }

    /** Returns number of maiden overs. */
    public int getMaidens() { return maidens; }

    /**
     * Calculates the economy rate (runs per over).
     *
     * @return economy rate, or 0.0 if no balls have been bowled
     */
    public double getEconomy() {
        if (ballsBowled == 0) return 0.0;
        return runsConceded / (ballsBowled / 6.0);
    }

    /**
     * Returns a one-line bowling scorecard entry.
     * Format: Name: W-R O:X.X M:X Econ:X.XX
     */
    @Override
    public String getStats() {
        return String.format("%-18s  %d-%d  O:%d.%d  M:%d  Econ:%5.2f  Wd:%-2d  Nb:%-2d",
                getName(), wickets, runsConceded,
                getOvers(), getRemainderBalls(),
                maidens, getEconomy(), wides, noBalls);
    }
}
