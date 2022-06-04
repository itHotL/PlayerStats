package com.gmail.artemis.the.gr8.playerstats.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OfflinePlayerHandler {

    private static ConcurrentHashMap<String, UUID> offlinePlayerUUIDs;

    private OfflinePlayerHandler() {
    }

    public static boolean isOfflinePlayerName(String playerName) {
        return offlinePlayerUUIDs.containsKey(playerName);
    }

    public static int getOfflinePlayerCount() throws NullPointerException {
        if (offlinePlayerUUIDs != null && offlinePlayerUUIDs.size() > 0) return offlinePlayerUUIDs.size();
        else throw new NullPointerException("No players found!");
    }

    public static ArrayList<String> getOfflinePlayerNames() {
        return new ArrayList<>(offlinePlayerUUIDs.keySet());
    }

    /**
     * Get a new HashMap that stores the players to include in stat calculations.
     * This HashMap is stored as a private variable in OfflinePlayerHandler (keys: playerNames, values: UUIDs).
     */
    public static void updateOfflinePlayerList(ConcurrentHashMap<String, UUID> playerList) {
        offlinePlayerUUIDs = playerList;
    }

    /**
     * Uses the playerName to get the player's UUID from a private HashMap, and uses the UUID to get the corresponding OfflinePlayer Object.
     * @param playerName name of the target player
     * @return OfflinePlayer (if this player is on the list)
     */
    public static OfflinePlayer getOfflinePlayer(String playerName) {
        return Bukkit.getOfflinePlayer(offlinePlayerUUIDs.get(playerName));
    }
}
