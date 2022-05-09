package com.gmail.artemis.the.gr8.playerstats.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.stream.Collectors;

public class OfflinePlayerHandler {

    private static OfflinePlayerHandler instance;
    private List<OfflinePlayer> offlinePlayers;
    private List<String> offlinePlayerNames;
    private HashMap<String, OfflinePlayer> offlinePlayerMap;

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

        OfflinePlayer player = offlinePlayerMap.get(playerName);
        System.out.println(("OfflinePlayerHandler 35: " + (System.currentTimeMillis() - time)));
        return player;
    }

    public List<OfflinePlayer> getAllOfflinePlayers() {
        return offlinePlayers;
    }

    public List<String> getAllOfflinePlayerNames() {
        return offlinePlayerNames;
    }

    public void updateOfflinePlayers() {
        offlinePlayerMap = new HashMap<>();
        offlinePlayers = Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer ->
                offlinePlayer.getName() != null && offlinePlayer.hasPlayedBefore()).collect(Collectors.toList());
        offlinePlayerNames = offlinePlayers.stream().map(OfflinePlayer::getName).collect(Collectors.toList());
        offlinePlayers.forEach(offlinePlayer -> offlinePlayerMap.put(offlinePlayer.getName(), offlinePlayer));
    }
}
