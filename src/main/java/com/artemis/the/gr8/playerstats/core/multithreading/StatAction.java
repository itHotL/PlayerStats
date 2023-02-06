package com.artemis.the.gr8.playerstats.core.multithreading;

import com.artemis.the.gr8.playerstats.api.StatRequest;
import com.artemis.the.gr8.playerstats.core.utils.OfflinePlayerHandler;
import com.artemis.the.gr8.playerstats.core.utils.MyLogger;
import com.google.common.collect.ImmutableList;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;

/**
 * The action that is executed when a stat-command is triggered.
 */
final class StatAction extends RecursiveTask<ConcurrentHashMap<String, Integer>> {

    private static int threshold;
    private final ImmutableList<String> playerNames;
    private final StatRequest.Settings requestSettings;
    private final ConcurrentHashMap<String, Integer> allStats;

    /**
     * Gets the statistic numbers for all players whose name is on
     * the list, puts them in a ConcurrentHashMap using the default
     * ForkJoinPool, and returns the ConcurrentHashMap when
     * everything is done.
     *
     * @param playerNames ImmutableList of playerNames for players that should be included in stat calculations
     * @param requestSettings a validated requestSettings object
     * @param allStats the ConcurrentHashMap to put the results on
     */
    public StatAction(ImmutableList<String> playerNames, StatRequest.Settings requestSettings, ConcurrentHashMap<String, Integer> allStats) {
        threshold = ThreadManager.getTaskThreshold();

        this.playerNames = playerNames;
        this.requestSettings = requestSettings;
        this.allStats = allStats;

        MyLogger.subActionCreated(Thread.currentThread().getName());
    }

    @Override
    protected ConcurrentHashMap<String, Integer> compute() {
        if (playerNames.size() < threshold) {
            return getStatsDirectly();
        }
        else {
            final StatAction subTask1 = new StatAction(playerNames.subList(0, playerNames.size()/2), requestSettings, allStats);
            final StatAction subTask2 = new StatAction(playerNames.subList(playerNames.size()/2, playerNames.size()), requestSettings, allStats);

            //queue and compute all subtasks in the right order
            subTask1.fork();
            subTask2.compute();
            return subTask1.join();
        }
    }

    private ConcurrentHashMap<String, Integer> getStatsDirectly() {
        OfflinePlayerHandler offlinePlayerHandler = OfflinePlayerHandler.getInstance();

        Iterator<String> iterator = playerNames.iterator();
        if (iterator.hasNext()) {
            do {
                String playerName = iterator.next();
                MyLogger.actionRunning(Thread.currentThread().getName());
                OfflinePlayer player = offlinePlayerHandler.getIncludedOfflinePlayer(playerName);
                int statistic = 0;
                switch (requestSettings.getStatistic().getType()) {
                    case UNTYPED -> statistic = player.getStatistic(requestSettings.getStatistic());
                    case ENTITY -> statistic = player.getStatistic(requestSettings.getStatistic(), requestSettings.getEntity());
                    case BLOCK -> statistic = player.getStatistic(requestSettings.getStatistic(), requestSettings.getBlock());
                    case ITEM -> statistic = player.getStatistic(requestSettings.getStatistic(), requestSettings.getItem());
                }
                if (statistic > 0) {
                    allStats.put(playerName, statistic);
                }
            } while (iterator.hasNext());
        }
        return allStats;
    }
}