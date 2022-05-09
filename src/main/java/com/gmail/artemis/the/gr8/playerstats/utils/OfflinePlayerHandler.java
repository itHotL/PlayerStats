package com.gmail.artemis.the.gr8.playerstats.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OfflinePlayerHandler {

    private List<OfflinePlayer> allOfflinePlayers;

    public OfflinePlayerHandler() {
        updateOfflinePlayers();
    }

    public static boolean isOfflinePlayer(String playerName) {
        return (getOfflinePlayer(playerName) != null);
    }

    public static OfflinePlayer getOfflinePlayer(String playerName) {
        OfflinePlayer[] playerList = Bukkit.getOfflinePlayers();
        OfflinePlayer offlinePlayer = null;

        for(OfflinePlayer player : playerList) {
            if(player.getName() != null && player.getName().equalsIgnoreCase(playerName)) {
                offlinePlayer = player;
                break;
            }
        }
        return offlinePlayer;
    }

    public List<OfflinePlayer> getAllOfflinePlayers() {
        return allOfflinePlayers;
    }

    public List<String> getAllOfflinePlayerNames() {
        return allOfflinePlayers.stream().map(OfflinePlayer::getName).collect(Collectors.toList());
    }

    public void updateOfflinePlayers() {
        allOfflinePlayers = Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer ->
                offlinePlayer.getName() != null && offlinePlayer.hasPlayedBefore()).collect(Collectors.toList());
    }
}
