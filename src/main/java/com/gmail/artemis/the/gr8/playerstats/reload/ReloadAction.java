package com.gmail.artemis.the.gr8.playerstats.reload;

import com.gmail.artemis.the.gr8.playerstats.utils.UnixTimeHandler;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;

public class ReloadAction extends RecursiveAction {

    private static final int threshold = 10;

    private final OfflinePlayer[] players;
    private final boolean whitelistOnly;
    private final boolean excludeBanned;
    private final int lastPlayedLimit;
    private final ConcurrentHashMap<String, UUID> offlinePlayerUUIDs;

    public ReloadAction(OfflinePlayer[] players,
                        boolean whitelistOnly, boolean excludeBanned, int lastPlayedLimit,
                        ConcurrentHashMap<String, UUID> offlinePlayerUUIDs) {
        this.players = players;
        this.whitelistOnly = whitelistOnly;
        this.excludeBanned = excludeBanned;
        this.lastPlayedLimit = lastPlayedLimit;
        this.offlinePlayerUUIDs = offlinePlayerUUIDs;
    }

    @Override
    protected void compute() {
        if (players.length < threshold) {
            process();
        }
        else {
            ReloadAction subTask1 = new ReloadAction(Arrays.copyOfRange(players, 0, players.length/2),
                    whitelistOnly, excludeBanned, lastPlayedLimit, offlinePlayerUUIDs);
            ReloadAction subTask2 = new ReloadAction(Arrays.copyOfRange(players, players.length/2, players.length),
                    whitelistOnly, excludeBanned, lastPlayedLimit, offlinePlayerUUIDs);

            try {
                //queue and compute all subtasks in the right order
                invokeAll(subTask1, subTask2);
            }
            catch (ConcurrentModificationException e) {
                e.printStackTrace();
            }
        }
    }

    private void process() {
        for (OfflinePlayer player : players) {
            if (player.getName() != null &&
                    (!whitelistOnly || player.isWhitelisted()) &&
                    (!excludeBanned || !player.isBanned()) &&
                    (lastPlayedLimit == 0 || UnixTimeHandler.hasPlayedSince(lastPlayedLimit, player.getLastPlayed()))) {
                offlinePlayerUUIDs.put(player.getName(), player.getUniqueId());
            }
        }
    }
}
