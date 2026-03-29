package com.cricket.model;

import java.io.Serializable;

/**
 * Abstract base class representing a cricket player.
 * Subclasses Batsman and Bowler extend this with role-specific statistics.
 */
public abstract class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final int playerNumber;

    /**
     * Constructs a Player with a name and jersey/order number.
     *
     * @param name         player's full name
     * @param playerNumber batting/bowling order number
     */
    public Player(String name, int playerNumber) {
        this.name = name;
        this.playerNumber = playerNumber;
    }

    /** Returns the player's name. */
    public String getName() {
        return name;
    }

    /** Returns the player's order number. */
    public int getPlayerNumber() {
        return playerNumber;
    }

    /**
     * Returns a formatted stats string for this player.
     * Implemented differently by Batsman and Bowler.
     */
    public abstract String getStats();

    @Override
    public String toString() {
        return playerNumber + ". " + name;
    }
}
