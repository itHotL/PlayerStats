package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.api.StatGetter;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.msg.OutputManager;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.google.common.collect.ImmutableList;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public final class StatManager implements StatGetter {

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
    public int getPlayerStat(StatRequest request) {
        OfflinePlayer player = offlinePlayerHandler.getOfflinePlayer(request.getPlayerName());
        if (player != null) {
            return switch (request.getStatistic().getType()) {
                case UNTYPED -> player.getStatistic(request.getStatistic());
                case ENTITY -> player.getStatistic(request.getStatistic(), request.getEntity());
                case BLOCK -> player.getStatistic(request.getStatistic(), request.getBlock());
                case ITEM -> player.getStatistic(request.getStatistic(), request.getItem());
            };
        }
        return 0;
    }

    public LinkedHashMap<String, Integer> getTopStats(StatRequest request) {
        return getAllStatsAsync(request).entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(topListMaxSize)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public long getServerStat(StatRequest request) {
        List<Integer> numbers = getAllStatsAsync(request)
                .values()
                .parallelStream()
                .toList();
        return numbers.parallelStream().mapToLong(Integer::longValue).sum();
    }

    /** Invokes a bunch of worker pool threads to divide and conquer (get the statistics for all players
     that are stored in the {@link OfflinePlayerHandler}) */
    public @NotNull ConcurrentHashMap<String, Integer> getAllStatsAsync(StatRequest request) {
        long time = System.currentTimeMillis();

        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        ConcurrentHashMap<String, Integer> allStats;

        try {
            allStats = commonPool.invoke(getStatTask(request));
        } catch (ConcurrentModificationException e) {
            MyLogger.logMsg("The request could not be executed due to a ConcurrentModificationException. " +
                    "This likely happened because Bukkit hasn't fully initialized all player-data yet. " +
                    "Try again and it should be fine!", true);
            throw new ConcurrentModificationException(e.toString());
        }

        MyLogger.actionFinished(2);
        ThreadManager.recordCalcTime(System.currentTimeMillis() - time);
        MyLogger.logTimeTaken("StatThread", "calculated all stats", time);

        return allStats;
    }

    private StatAction getStatTask(StatRequest request) {
        int size = offlinePlayerHandler.getOfflinePlayerCount() != 0 ? offlinePlayerHandler.getOfflinePlayerCount() : 16;
        ConcurrentHashMap<String, Integer> allStats = new ConcurrentHashMap<>(size);
        ImmutableList<String> playerNames = ImmutableList.copyOf(offlinePlayerHandler.getOfflinePlayerNames());

        StatAction task = new StatAction(offlinePlayerHandler, playerNames, request, allStats);
        MyLogger.actionCreated(playerNames.size());

        return task;
    }
}