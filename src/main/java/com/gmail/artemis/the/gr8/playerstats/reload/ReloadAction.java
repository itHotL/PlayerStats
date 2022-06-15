package com.gmail.artemis.the.gr8.playerstats.reload;

import com.gmail.artemis.the.gr8.playerstats.utils.UnixTimeHandler;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;

public class ReloadAction extends RecursiveAction {

    private final int threshold;

    private final OfflinePlayer[] players;
    private final int start;
    private final int end;

    private final boolean whitelistOnly;
    private final boolean excludeBanned;
    private final int lastPlayedLimit;
    private final ConcurrentHashMap<String, UUID> offlinePlayerUUIDs;

    /** Fills a ConcurrentHashMap with PlayerNames and UUIDs for all OfflinePlayers that should be included in statistic calculations.
     * @param threshold the maximum length of OfflinePlayers to process in one task
     * @param players array of all OfflinePlayers (straight from Bukkit)
     * @param whitelistOnly whether to limit players based on the whitelist
     * @param excludeBanned whether to exclude banned players
     * @param lastPlayedLimit whether to set a limit based on last-played-date
     * @param offlinePlayerUUIDs the ConcurrentHashMap to put resulting playerNames and UUIDs on
     */
    public ReloadAction(int threshold, OfflinePlayer[] players,
                        boolean whitelistOnly, boolean excludeBanned, int lastPlayedLimit,
                        ConcurrentHashMap<String, UUID> offlinePlayerUUIDs) {

       this(threshold, players, 0, players.length,
               whitelistOnly, excludeBanned, lastPlayedLimit, offlinePlayerUUIDs);
    }

    protected ReloadAction(int threshold, OfflinePlayer[] players, int start, int end,
                           boolean whitelistOnly, boolean excludeBanned, int lastPlayedLimit,
                           ConcurrentHashMap<String, UUID> offlinePlayerUUIDs) {

        this.threshold = threshold;
        this.players = players;
        this.start = start;
        this.end = end;

        this.whitelistOnly = whitelistOnly;
        this.excludeBanned = excludeBanned;
        this.lastPlayedLimit = lastPlayedLimit;
        this.offlinePlayerUUIDs = offlinePlayerUUIDs;
    }

    @Override
    protected void compute() {
        final int length = end - start;
        if (length < threshold) {
            process();
        }
        else {
            final int split = length / 2;
            final ReloadAction subTask1 = new ReloadAction(threshold, players, start, (start + split),
                    whitelistOnly, excludeBanned, lastPlayedLimit, offlinePlayerUUIDs);
            final ReloadAction subTask2 = new ReloadAction(threshold, players, (start + split), end,
                    whitelistOnly, excludeBanned, lastPlayedLimit, offlinePlayerUUIDs);

            //queue and compute all subtasks in the right order
            invokeAll(subTask1, subTask2);
        }
    }

    private void process() {
        for (int i = start; i < end; i++) {
            OfflinePlayer player = players[i];
            if (player.getName() != null &&
                    (!whitelistOnly || player.isWhitelisted()) &&
                    (!excludeBanned || !player.isBanned()) &&
                    (lastPlayedLimit == 0 || UnixTimeHandler.hasPlayedSince(lastPlayedLimit, player.getLastPlayed()))) {
                offlinePlayerUUIDs.put(player.getName(), player.getUniqueId());
            }
        }
    }
}
