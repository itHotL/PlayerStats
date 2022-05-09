package com.gmail.artemis.the.gr8.playerstats.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.stream.Collectors;

public class OfflinePlayerHandler {

    private static OfflinePlayerHandler instance;
    private List<OfflinePlayer> offlinePlayers;
    private List<String> offlinePlayerNames;
    private HashMap<String, UUID> offlinePlayerUUIDs;

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
        System.out.println(("OfflinePlayerHandler 34: " + (System.currentTimeMillis() - time)));
        time = System.currentTimeMillis();

        Optional<OfflinePlayer> player = offlinePlayers.stream().filter(offlinePlayer ->
                offlinePlayer.getName() != null && offlinePlayer.getName().equalsIgnoreCase(playerName)).findAny();

        System.out.println(("OfflinePlayerHandler 40: " + (System.currentTimeMillis() - time)));
        time = System.currentTimeMillis();
        return player.orElse(null);
    }

    public List<OfflinePlayer> getAllOfflinePlayers() {
        return offlinePlayers;
    }

    public List<String> getAllOfflinePlayerNames() {
        return offlinePlayerNames;
    }

    public void updateOfflinePlayers() {
        offlinePlayers = Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer ->
                offlinePlayer.getName() != null && offlinePlayer.hasPlayedBefore()).collect(Collectors.toList());
        offlinePlayerNames = offlinePlayers.stream().map(OfflinePlayer::getName).collect(Collectors.toList());
        offlinePlayers.forEach(offlinePlayer -> offlinePlayerUUIDs.put(offlinePlayer.getName(), offlinePlayer.getUniqueId()));
    }
}
