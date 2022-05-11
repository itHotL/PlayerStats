package com.gmail.artemis.the.gr8.playerstats.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class OfflinePlayerHandler {

    private static OfflinePlayerHandler instance;
    private HashMap<String, OfflinePlayer> offlinePlayerMap;
    private List<String> offlinePlayerNames;
    private int totalOfflinePlayers;

    private OfflinePlayerHandler() {
        updateOfflinePlayers();
    }

    public static OfflinePlayerHandler getInstance() {
        if (instance == null) {
            instance = new OfflinePlayerHandler();
        }
        return instance;
    }

    public boolean isOfflinePlayerName(String playerName) {
        return offlinePlayerNames.contains(playerName);
    }

    public OfflinePlayer getOfflinePlayer(String playerName) {
        long time = System.currentTimeMillis();
        return offlinePlayerMap.get(playerName);
    }

    public int getOfflinePlayerCount() {
        return totalOfflinePlayers > 0 ? totalOfflinePlayers : 1;
    }

    public List<String> getAllOfflinePlayerNames() {
        return offlinePlayerNames;
    }

    //stores a private HashMap with keys:playerName and values:OfflinePlayer, and a private list of the names for easy access
    public void updateOfflinePlayers() {
        long totalTime = System.currentTimeMillis();
        long time = System.currentTimeMillis();
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
        System.out.println("OfflinePlayerHandler, making the HashMap and ArrayList: " + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();

        totalOfflinePlayers = offlinePlayerMap.size();
        System.out.println("OfflinePlayerHandler, counting the HashMap: " + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();

        totalOfflinePlayers = offlinePlayerNames.size();
        System.out.println("OfflinePlayerHandler, counting the ArrayList: " + (System.currentTimeMillis() - time));
        System.out.println("updateOfflinePlayers total time: " + (System.currentTimeMillis() - totalTime));
    }
}
