package com.cricket.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents one innings in a cricket match.
 * Tracks the score, wickets, overs, current batsmen, current bowler,
 * and a ball-by-ball commentary log.
 */
public class Innings implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Team battingTeam;
    private final Team bowlingTeam;
    private final int maxOvers;

    private int totalRuns;
    private int wickets;
    private int ballsBowled;      // legal deliveries only
    private int extras;           // total wides + no-balls

    private int strikerIdx;       // index into battingTeam.getBatsmen()
    private int nonStrikerIdx;
    private int nextBatsmanIdx;   // index of the next batsman to come in
    private int currentBowlerIdx; // -1 means "not yet selected"

    private final List<String> ballByBall; // commentary for each delivery
    private boolean complete;

    /**
     * Constructs a new Innings.
     *
     * @param battingTeam  the team batting in this innings
     * @param bowlingTeam  the team bowling in this innings
     * @param maxOvers     maximum overs allowed (0 = unlimited)
     */
    public Innings(Team battingTeam, Team bowlingTeam, int maxOvers) {
        this.battingTeam = battingTeam;
        this.bowlingTeam = bowlingTeam;
        this.maxOvers = maxOvers;
        this.totalRuns = 0;
        this.wickets = 0;
        this.ballsBowled = 0;
        this.extras = 0;
        this.strikerIdx = 0;
        this.nonStrikerIdx = 1;
        this.nextBatsmanIdx = 2;
        this.currentBowlerIdx = -1;
        this.ballByBall = new ArrayList<>();
        this.complete = false;
    }

    /**
     * Records a single delivery and updates all statistics accordingly.
     *
     * Scoring rules applied:
     * - Wide or no-ball: +1 penalty run, delivery not counted as legal
     * - Wicket on a legal delivery: batsman out, next batsman comes in
     * - Odd runs off the bat: batsmen rotate strike
     * - End of an over (every 6 legal balls): batsmen rotate strike
     *
     * @param runs     runs scored off the bat (not including wide/no-ball penalties)
     * @param isWide   true if the delivery is a wide
     * @param isNoBall true if the delivery is a no-ball
     * @param isWicket true if a wicket fell (only meaningful for legal deliveries)
     * @return true if the innings is still in progress after this delivery
     */
    public boolean recordDelivery(int runs, boolean isWide, boolean isNoBall, boolean isWicket) {
        Bowler bowler = bowlingTeam.getBowler(currentBowlerIdx);
        Batsman batter = battingTeam.getBatsman(strikerIdx);

        StringBuilder desc = new StringBuilder();

        if (isWide) {
            // Wide: penalty run + any runs scored; NOT a legal delivery
            totalRuns += runs + 1;
            extras++;
            bowler.addDelivery(runs, true, false, false);
            desc.append("Wd").append(runs > 0 ? "+" + runs : "");

        } else if (isNoBall) {
            // No-ball: penalty run + runs to batsman's credit; NOT a legal delivery
            totalRuns += runs + 1;
            extras++;
            batter.addRuns(runs);
            bowler.addDelivery(runs, false, true, false);
            desc.append("Nb").append(runs > 0 ? "+" + runs : "");

        } else {
            // Legal delivery
            ballsBowled++;

            if (isWicket) {
                totalRuns += runs; // any runs before the wicket still count
                batter.addBallFaced();
                batter.setOut("b " + bowler.getName());
                bowler.addDelivery(runs, false, false, true);
                wickets++;
                desc.append("W");

                // Bring in next batsman to take the striker's place
                if (nextBatsmanIdx < battingTeam.getPlayerCount()) {
                    strikerIdx = nextBatsmanIdx;
                    nextBatsmanIdx++;
                }
            } else {
                totalRuns += runs;
                batter.addRuns(runs);
                bowler.addDelivery(runs, false, false, false);
                desc.append(runs);

                // Odd runs: strike rotates immediately
                if (runs % 2 != 0) {
                    swapStrike();
                }
            }

            // End of over: strike rotates
            if (ballsBowled % 6 == 0) {
                swapStrike();
            }
        }

        ballByBall.add(desc.toString());

        // Check innings completion
        boolean allOut = (wickets >= battingTeam.getPlayerCount() - 1);
        boolean oversUp = (maxOvers > 0 && ballsBowled >= maxOvers * 6);
        if (allOut || oversUp) {
            complete = true;
        }

        return !complete;
    }

    /** Swaps the striker and non-striker indices. */
    private void swapStrike() {
        int tmp = strikerIdx;
        strikerIdx = nonStrikerIdx;
        nonStrikerIdx = tmp;
    }

    // ── Getters ─────────────────────────────────────────────────────────────

    /** Returns the total runs scored in this innings. */
    public int getTotalRuns() { return totalRuns; }

    /** Returns the number of wickets fallen. */
    public int getWickets() { return wickets; }

    /** Returns the number of legal deliveries bowled. */
    public int getBallsBowled() { return ballsBowled; }

    /** Returns completed overs. */
    public int getOvers() { return ballsBowled / 6; }

    /** Returns balls bowled in the current incomplete over. */
    public int getRemainderBalls() { return ballsBowled % 6; }

    /** Returns the maximum overs allowed (0 = unlimited). */
    public int getMaxOvers() { return maxOvers; }

    /** Returns the batting team. */
    public Team getBattingTeam() { return battingTeam; }

    /** Returns the bowling team. */
    public Team getBowlingTeam() { return bowlingTeam; }

    /** Returns the index of the current striker in the batting team. */
    public int getStrikerIdx() { return strikerIdx; }

    /** Returns the index of the current non-striker in the batting team. */
    public int getNonStrikerIdx() { return nonStrikerIdx; }

    /** Returns the index of the current bowler in the bowling team. */
    public int getCurrentBowlerIdx() { return currentBowlerIdx; }

    /** Sets the current bowler by their index in the bowling team. */
    public void setCurrentBowlerIdx(int idx) { this.currentBowlerIdx = idx; }

    /** Returns the ball-by-ball commentary list. */
    public List<String> getBallByBall() { return ballByBall; }

    /** Returns true if this innings has ended. */
    public boolean isComplete() { return complete; }

    /** Returns total extras (wides + no-balls) in this innings. */
    public int getExtras() { return extras; }

    /**
     * Returns a concise score string, e.g. "143/4 (18.3 ov)".
     */
    public String getScoreString() {
        return totalRuns + "/" + wickets + " (" + getOvers() + "." + getRemainderBalls() + " ov)";
    }
}
