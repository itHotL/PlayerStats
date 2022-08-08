package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.RequestSettings;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.google.common.collect.ImmutableList;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;

/** The action that is executed when a stat-command is triggered. */
final class StatAction extends RecursiveTask<ConcurrentHashMap<String, Integer>> {

    private static int threshold;

    private final OfflinePlayerHandler offlinePlayerHandler;
    private final ImmutableList<String> playerNames;
    private final RequestSettings requestSettings;
    private final ConcurrentHashMap<String, Integer> allStats;

    /**
     * Gets the statistic numbers for all players whose name is on the list, puts them in a ConcurrentHashMap
     * using the default ForkJoinPool, and returns the ConcurrentHashMap when everything is done
     * @param offlinePlayerHandler the OfflinePlayerHandler to convert playerNames into Players
     * @param playerNames ImmutableList of playerNames for players that should be included in stat calculations
     * @param requestSettings a validated requestSettings
     * @param allStats the ConcurrentHashMap to put the results on
     */
    public StatAction(OfflinePlayerHandler offlinePlayerHandler, ImmutableList<String> playerNames, RequestSettings requestSettings, ConcurrentHashMap<String, Integer> allStats) {
        threshold = ThreadManager.getTaskThreshold();

        this.offlinePlayerHandler = offlinePlayerHandler;
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
            final StatAction subTask1 = new StatAction(offlinePlayerHandler, playerNames.subList(0, playerNames.size()/2), requestSettings, allStats);
            final StatAction subTask2 = new StatAction(offlinePlayerHandler, playerNames.subList(playerNames.size()/2, playerNames.size()), requestSettings, allStats);

            //queue and compute all subtasks in the right order
            subTask1.fork();
            subTask2.compute();
            return subTask1.join();
        }
    }

    private ConcurrentHashMap<String, Integer> getStatsDirectly() {
        Iterator<String> iterator = playerNames.iterator();
        if (iterator.hasNext()) {
            do {
                String playerName = iterator.next();
                MyLogger.actionRunning(Thread.currentThread().getName(), playerName, 2);
                OfflinePlayer player = offlinePlayerHandler.getOfflinePlayer(playerName);
                if (player != null) {
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
                }
            } while (iterator.hasNext());
        }
        return allStats;
    }
}