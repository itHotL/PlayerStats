package com.gmail.artemis.the.gr8.playerstats.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class OfflinePlayerHandler {

    private static HashMap<String, OfflinePlayer> offlinePlayerMap;
    private static List<String> offlinePlayerNames;
    private static int totalOfflinePlayers;

    private OfflinePlayerHandler() {
    }

    public static boolean isOfflinePlayerName(String playerName) {
        return offlinePlayerNames.contains(playerName);
    }

    public static OfflinePlayer getOfflinePlayer(String playerName) {
        return offlinePlayerMap.get(playerName);
    }

    public static int getOfflinePlayerCount() throws NullPointerException {
        if (totalOfflinePlayers > 0) return totalOfflinePlayers;
        else throw new NullPointerException("No players found!");
    }

    public static List<String> getAllOfflinePlayerNames() {
        return offlinePlayerNames;
    }

    //stores a private HashMap with keys:playerName and values:OfflinePlayer, and a private list of the names for easy access
    public static void updateOfflinePlayers() {
        if (offlinePlayerMap == null) offlinePlayerMap = new HashMap<>();
        else if (!offlinePlayerMap.isEmpty()) {
            offlinePlayerMap.clear();
        }

        if (offlinePlayerNames == null) offlinePlayerNames = new ArrayList<>();
        else if (!offlinePlayerNames.isEmpty()) {
            offlinePlayerNames.clear();
        }

        Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer ->
                offlinePlayer.getName() != null && offlinePlayer.hasPlayedBefore()).forEach(offlinePlayer -> {
                    offlinePlayerNames.add(offlinePlayer.getName());
                    offlinePlayerMap.put(offlinePlayer.getName(), offlinePlayer);
        });

        totalOfflinePlayers = offlinePlayerMap.size();
    }
}
