package com.gmail.artemis.the.gr8.playerstats.utils;

import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class OfflinePlayerHandler {

    private final ConfigHandler config;
    private static HashMap<String, UUID> offlinePlayerUUIDs;

    public OfflinePlayerHandler(ConfigHandler c) {
        config = c;
        updateOfflinePlayerList();
    }

    public static boolean isOfflinePlayerName(String playerName) {
        return offlinePlayerUUIDs.containsKey(playerName);
    }

    public static int getOfflinePlayerCount() throws NullPointerException {
        if (offlinePlayerUUIDs != null && offlinePlayerUUIDs.size() > 0) return offlinePlayerUUIDs.size();
        else throw new NullPointerException("No players found!");
    }

    public static Set<String> getOfflinePlayerNames() {
        return offlinePlayerUUIDs.keySet();
    }

    public void updateOfflinePlayerList() {
        updateOfflinePlayerList(config.whitelistOnly(), config.excludeBanned(), config.lastPlayedLimit());
    }

    //stores a private HashMap with keys:playerName and values:UUID, and a private list of the names for easy access
    private void updateOfflinePlayerList(boolean whitelistOnly, boolean excludeBanned, int lastPlayedLimit) {
        if (offlinePlayerUUIDs == null) offlinePlayerUUIDs = new HashMap<>();
        else if (!offlinePlayerUUIDs.isEmpty()) {
            offlinePlayerUUIDs.clear();
        }

        Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer ->
                offlinePlayer.getName() != null && offlinePlayer.hasPlayedBefore()).forEach(offlinePlayer -> {
            offlinePlayerUUIDs.put(offlinePlayer.getName(), offlinePlayer.getUniqueId());
        });
    }

    public static OfflinePlayer getOfflinePlayer(String playerName) {
        return Bukkit.getOfflinePlayer(offlinePlayerUUIDs.get(playerName));
    }
}
