package com.gmail.artemis.the.gr8.playerstats.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** A utility class that deals with OfflinePlayers. It stores a list of all OfflinePlayer-names
 that need to be included in statistic calculations, and can retrieve the corresponding OfflinePlayer
 object for a given player-name.*/
public final class OfflinePlayerHandler {

    private static ConcurrentHashMap<String, UUID> offlinePlayerUUIDs;
    private static ArrayList<String> playerNames;

    public OfflinePlayerHandler() {
        offlinePlayerUUIDs = new ConcurrentHashMap<>();
        playerNames = new ArrayList<>();
    }

    /**
     * Get a new HashMap that stores the players to include in stat calculations.
     * This HashMap is stored as a private variable in OfflinePlayerHandler.
     * @param playerList ConcurrentHashMap with keys: playerNames and values: UUIDs
     */
    public static void updateOfflinePlayerList(ConcurrentHashMap<String, UUID> playerList) {
        offlinePlayerUUIDs = playerList;
        playerNames = Collections.list(offlinePlayerUUIDs.keys());
    }

    /** Checks if a given playerName is on the private HashMap of players that should be included in statistic calculations
     @param playerName String, case-sensitive */
    public boolean isRelevantPlayer(String playerName) {
        return offlinePlayerUUIDs.containsKey(playerName);
    }

    /** Returns the number of OfflinePlayers that are included in statistic calculations */
    public int getOfflinePlayerCount() {
        return offlinePlayerUUIDs.size();
    }

    /** Get an ArrayList of names from all OfflinePlayers that should be included in statistic calculations */
    public ArrayList<String> getOfflinePlayerNames() {
        return playerNames;
    }

    /**
     * Uses the playerName to get the player's UUID from a private HashMap, and uses the UUID to get the corresponding OfflinePlayer Object.
     * @param playerName name of the target player (case-sensitive)
     * @return OfflinePlayer
     * @throws IllegalArgumentException if this player is not on the list of players that should be included in statistic calculations
     */
    public OfflinePlayer getOfflinePlayer(String playerName) throws IllegalArgumentException {
        if (offlinePlayerUUIDs.get(playerName) != null) {
            return Bukkit.getOfflinePlayer(offlinePlayerUUIDs.get(playerName));
        }
        else {
            MyLogger.logMsg("Cannot calculate statistics for player-name: " + playerName +
                    "! Double-check if the name is spelled correctly (including capital letters), " +
                    "or if any of your config settings exclude them", true);
            throw new IllegalArgumentException("Cannot convert this player-name into a valid Player to calculate statistics for");
        }
    }
}