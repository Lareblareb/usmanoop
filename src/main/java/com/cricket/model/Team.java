package com.cricket.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a cricket team.
 * Holds parallel lists of Batsman and Bowler objects for the same players,
 * allowing each player's batting and bowling statistics to be tracked separately.
 */
public class Team implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final List<Batsman> batsmen;
    private final List<Bowler> bowlers;

    /**
     * Creates a team with the given name and empty player lists.
     *
     * @param name team name (e.g. "Pakistan")
     */
    public Team(String name) {
        this.name = name;
        this.batsmen = new ArrayList<>();
        this.bowlers = new ArrayList<>();
    }

    /**
     * Adds a player to the team.
     * Creates both a Batsman and a Bowler record for the same player
     * so that both batting and bowling statistics can be tracked.
     *
     * @param playerName the player's full name
     */
    public void addPlayer(String playerName) {
        int number = batsmen.size() + 1;
        batsmen.add(new Batsman(playerName, number));
        bowlers.add(new Bowler(playerName, number));
    }

    /** Returns the team name. */
    public String getName() { return name; }

    /** Returns the list of Batsman objects for this team. */
    public List<Batsman> getBatsmen() { return batsmen; }

    /** Returns the list of Bowler objects for this team. */
    public List<Bowler> getBowlers() { return bowlers; }

    /**
     * Returns the Batsman object for the player at the given index.
     *
     * @param index zero-based batting order index
     * @return the Batsman at that position
     */
    public Batsman getBatsman(int index) {
        return batsmen.get(index);
    }

    /**
     * Returns the Bowler object for the player at the given index.
     *
     * @param index zero-based index in the player list
     * @return the Bowler at that position
     */
    public Bowler getBowler(int index) {
        return bowlers.get(index);
    }

    /** Returns the total number of players in the team. */
    public int getPlayerCount() {
        return batsmen.size();
    }

    @Override
    public String toString() {
        return name + " (" + batsmen.size() + " players)";
    }
}
