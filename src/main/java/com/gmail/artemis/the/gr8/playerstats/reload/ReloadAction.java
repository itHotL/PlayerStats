package com.gmail.artemis.the.gr8.playerstats.reload;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import com.gmail.artemis.the.gr8.playerstats.utils.UnixTimeHandler;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;

/** The action that is executed when a reload-command is triggered. */
final class ReloadAction extends RecursiveAction {

    private static int threshold;

    private final OfflinePlayer[] players;
    private final int start;
    private final int end;

    private final int lastPlayedLimit;
    private final ConcurrentHashMap<String, UUID> offlinePlayerUUIDs;

    /** Fills a ConcurrentHashMap with PlayerNames and UUIDs for all OfflinePlayers that should be included in statistic calculations.
     * @param players array of all OfflinePlayers (straight from Bukkit)
     * @param lastPlayedLimit whether to set a limit based on last-played-date
     * @param offlinePlayerUUIDs the ConcurrentHashMap to put resulting playerNames and UUIDs on
     */
    public ReloadAction(OfflinePlayer[] players,
                        int lastPlayedLimit, ConcurrentHashMap<String, UUID> offlinePlayerUUIDs) {

       this(players, 0, players.length, lastPlayedLimit, offlinePlayerUUIDs);
    }

    private ReloadAction(OfflinePlayer[] players, int start, int end,
                           int lastPlayedLimit, ConcurrentHashMap<String, UUID> offlinePlayerUUIDs) {
        threshold = ThreadManager.getTaskThreshold();

        this.players = players;
        this.start = start;
        this.end = end;

        this.lastPlayedLimit = lastPlayedLimit;
        this.offlinePlayerUUIDs = offlinePlayerUUIDs;

        MyLogger.subActionCreated(Thread.currentThread().getName());
    }

    @Override
    protected void compute() {
        final int length = end - start;
        if (length < threshold) {
            process();
        }
        else {
            final int split = length / 2;
            final ReloadAction subTask1 = new ReloadAction(players, start, (start + split),
                    lastPlayedLimit, offlinePlayerUUIDs);
            final ReloadAction subTask2 = new ReloadAction(players, (start + split), end,
                    lastPlayedLimit, offlinePlayerUUIDs);

            //queue and compute all subtasks in the right order
            invokeAll(subTask1, subTask2);
        }
    }

    private void process() {
        for (int i = start; i < end; i++) {
            OfflinePlayer player = players[i];
            String playerName = player.getName();
            MyLogger.actionRunning(Thread.currentThread().getName());
            if (playerName != null &&
                    (lastPlayedLimit == 0 || UnixTimeHandler.hasPlayedSince(lastPlayedLimit, player.getLastPlayed()))) {
                offlinePlayerUUIDs.put(playerName, player.getUniqueId());
            }
        }
    }
}