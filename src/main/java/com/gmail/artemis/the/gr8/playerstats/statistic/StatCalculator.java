package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.RequestSettings;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.google.common.collect.ImmutableList;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public final class StatCalculator {

    private final OfflinePlayerHandler offlinePlayerHandler;

    public StatCalculator(OfflinePlayerHandler offlinePlayerHandler) {
        this.offlinePlayerHandler = offlinePlayerHandler;
    }

    public int getPlayerStat(RequestSettings requestSettings) {
        OfflinePlayer player = offlinePlayerHandler.getOfflinePlayer(requestSettings.getPlayerName());
        return switch (requestSettings.getStatistic().getType()) {
            case UNTYPED -> player.getStatistic(requestSettings.getStatistic());
            case ENTITY -> player.getStatistic(requestSettings.getStatistic(), requestSettings.getEntity());
            case BLOCK -> player.getStatistic(requestSettings.getStatistic(), requestSettings.getBlock());
            case ITEM -> player.getStatistic(requestSettings.getStatistic(), requestSettings.getItem());
        };
    }

    public LinkedHashMap<String, Integer> getTopStats(RequestSettings requestSettings) {
        return getAllStatsAsync(requestSettings).entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(requestSettings.getTopListSize())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public long getServerStat(RequestSettings requestSettings) {
        List<Integer> numbers = getAllStatsAsync(requestSettings)
                .values()
                .parallelStream()
                .toList();
        return numbers.parallelStream().mapToLong(Integer::longValue).sum();
    }

    /** Invokes a bunch of worker pool threads to divide and conquer (get the statistics for all players
     that are stored in the {@link OfflinePlayerHandler}) */
    private @NotNull ConcurrentHashMap<String, Integer> getAllStatsAsync(RequestSettings requestSettings) {
        long time = System.currentTimeMillis();

        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        ConcurrentHashMap<String, Integer> allStats;

        try {
            allStats = commonPool.invoke(getStatTask(requestSettings));
        } catch (ConcurrentModificationException e) {
            MyLogger.logWarning("The requestSettings could not be executed due to a ConcurrentModificationException. " +
                    "This likely happened because Bukkit hasn't fully initialized all player-data yet. " +
                    "Try again and it should be fine!");
            throw new ConcurrentModificationException(e.toString());
        }

        MyLogger.actionFinished();
        ThreadManager.recordCalcTime(System.currentTimeMillis() - time);
        MyLogger.logMediumLevelTask("StatThread", "calculated all stats", time);

        return allStats;
    }

    private StatAction getStatTask(RequestSettings requestSettings) {
        int size = offlinePlayerHandler.getOfflinePlayerCount() != 0 ? offlinePlayerHandler.getOfflinePlayerCount() : 16;
        ConcurrentHashMap<String, Integer> allStats = new ConcurrentHashMap<>(size);
        ImmutableList<String> playerNames = ImmutableList.copyOf(offlinePlayerHandler.getOfflinePlayerNames());

        StatAction task = new StatAction(offlinePlayerHandler, playerNames, requestSettings, allStats);
        MyLogger.actionCreated(playerNames.size());

        return task;
    }
}