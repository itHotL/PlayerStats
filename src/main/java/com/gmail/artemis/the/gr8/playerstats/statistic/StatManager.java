package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.api.StatCalculator;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.StatRequestCore;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.google.common.collect.ImmutableList;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public final class StatManager implements StatCalculator {

    private final OfflinePlayerHandler offlinePlayerHandler;
    private static int topListMaxSize;

    public StatManager(OfflinePlayerHandler offlinePlayerHandler, int topListMaxSize) {
        this.offlinePlayerHandler = offlinePlayerHandler;
        StatManager.topListMaxSize = topListMaxSize;
    }

    public static void updateSettings(int topListMaxSize) {
        StatManager.topListMaxSize = topListMaxSize;
    }

    /** Gets the statistic data for an individual player. If somehow the player
     cannot be found, this returns 0.*/
    public int getPlayerStat(StatRequestCore statRequestCore) {
        OfflinePlayer player = offlinePlayerHandler.getOfflinePlayer(statRequestCore.getPlayerName());
        if (player != null) {
            return switch (statRequestCore.getStatistic().getType()) {
                case UNTYPED -> player.getStatistic(statRequestCore.getStatistic());
                case ENTITY -> player.getStatistic(statRequestCore.getStatistic(), statRequestCore.getEntity());
                case BLOCK -> player.getStatistic(statRequestCore.getStatistic(), statRequestCore.getBlock());
                case ITEM -> player.getStatistic(statRequestCore.getStatistic(), statRequestCore.getItem());
            };
        }
        return 0;
    }

    public LinkedHashMap<String, Integer> getTopStats(StatRequestCore statRequestCore) {
        return getAllStatsAsync(statRequestCore).entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(topListMaxSize)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public long getServerStat(StatRequestCore statRequestCore) {
        List<Integer> numbers = getAllStatsAsync(statRequestCore)
                .values()
                .parallelStream()
                .toList();
        return numbers.parallelStream().mapToLong(Integer::longValue).sum();
    }

    /** Invokes a bunch of worker pool threads to divide and conquer (get the statistics for all players
     that are stored in the {@link OfflinePlayerHandler}) */
    private @NotNull ConcurrentHashMap<String, Integer> getAllStatsAsync(StatRequestCore statRequestCore) {
        long time = System.currentTimeMillis();

        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        ConcurrentHashMap<String, Integer> allStats;

        try {
            allStats = commonPool.invoke(getStatTask(statRequestCore));
        } catch (ConcurrentModificationException e) {
            MyLogger.logMsg("The statRequest could not be executed due to a ConcurrentModificationException. " +
                    "This likely happened because Bukkit hasn't fully initialized all player-data yet. " +
                    "Try again and it should be fine!", true);
            throw new ConcurrentModificationException(e.toString());
        }

        MyLogger.actionFinished(2);
        ThreadManager.recordCalcTime(System.currentTimeMillis() - time);
        MyLogger.logTimeTaken("StatThread", "calculated all stats", time);

        return allStats;
    }

    private StatAction getStatTask(StatRequestCore statRequestCore) {
        int size = offlinePlayerHandler.getOfflinePlayerCount() != 0 ? offlinePlayerHandler.getOfflinePlayerCount() : 16;
        ConcurrentHashMap<String, Integer> allStats = new ConcurrentHashMap<>(size);
        ImmutableList<String> playerNames = ImmutableList.copyOf(offlinePlayerHandler.getOfflinePlayerNames());

        StatAction task = new StatAction(offlinePlayerHandler, playerNames, statRequestCore, allStats);
        MyLogger.actionCreated(playerNames.size());

        return task;
    }
}