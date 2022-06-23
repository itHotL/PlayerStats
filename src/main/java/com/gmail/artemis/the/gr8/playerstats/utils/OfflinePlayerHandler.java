package com.gmail.artemis.the.gr8.playerstats.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OfflinePlayerHandler {

    private static ConcurrentHashMap<String, UUID> offlinePlayerUUIDs;
    private static ArrayList<String> playerNames;

    static{
        offlinePlayerUUIDs = new ConcurrentHashMap<>();
        playerNames = new ArrayList<>();
    }

    private OfflinePlayerHandler() {
    }

    /** Checks if a given playerName is on the private HashMap of players that should be included in statistic calculations
     @param playerName String, case-sensitive */
    public static boolean isRelevantPlayer(String playerName) {
        return offlinePlayerUUIDs.containsKey(playerName);
    }

    /** Returns the number of OfflinePlayers that are included in statistic calculations */
    public static int getOfflinePlayerCount() {
        return offlinePlayerUUIDs.size();
    }

    /** Get an ArrayList of names from all OfflinePlayers that should be included in statistic calculations */
    public static ArrayList<String> getOfflinePlayerNames() {
        return playerNames;
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

    /**
     * Uses the playerName to get the player's UUID from a private HashMap, and uses the UUID to get the corresponding OfflinePlayer Object.
     * @param playerName name of the target player
     * @return OfflinePlayer (if this player is on the list, otherwise null)
     */
    public static @Nullable OfflinePlayer getOfflinePlayer(String playerName) {
        if (offlinePlayerUUIDs.get(playerName) != null) {
            return Bukkit.getOfflinePlayer(offlinePlayerUUIDs.get(playerName));
        }
        else {
            return null;
        }
    }
}
