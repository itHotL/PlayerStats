package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.api.StatCalculator;
import com.gmail.artemis.the.gr8.playerstats.enums.DebugLevel;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.StatRequest;
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

    public StatManager(OfflinePlayerHandler offlinePlayerHandler) {
        this.offlinePlayerHandler = offlinePlayerHandler;
    }

    /** Gets the statistic data for an individual player. If somehow the player
     cannot be found, this returns 0.*/
    @Override
    public int getPlayerStat(StatRequest statRequest) {
        OfflinePlayer player = offlinePlayerHandler.getOfflinePlayer(statRequest.getPlayerName());
        if (player != null) {
            return switch (statRequest.getStatistic().getType()) {
                case UNTYPED -> player.getStatistic(statRequest.getStatistic());
                case ENTITY -> player.getStatistic(statRequest.getStatistic(), statRequest.getEntity());
                case BLOCK -> player.getStatistic(statRequest.getStatistic(), statRequest.getBlock());
                case ITEM -> player.getStatistic(statRequest.getStatistic(), statRequest.getItem());
            };
        }
        return 0;
    }

    @Override
    public LinkedHashMap<String, Integer> getTopStats(StatRequest statRequest) {
        return getAllStatsAsync(statRequest).entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(statRequest.getTopListSize())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public long getServerStat(StatRequest statRequest) {
        List<Integer> numbers = getAllStatsAsync(statRequest)
                .values()
                .parallelStream()
                .toList();
        return numbers.parallelStream().mapToLong(Integer::longValue).sum();
    }

    /** Invokes a bunch of worker pool threads to divide and conquer (get the statistics for all players
     that are stored in the {@link OfflinePlayerHandler}) */
    private @NotNull ConcurrentHashMap<String, Integer> getAllStatsAsync(StatRequest statRequest) {
        long time = System.currentTimeMillis();

        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        ConcurrentHashMap<String, Integer> allStats;

        try {
            allStats = commonPool.invoke(getStatTask(statRequest));
        } catch (ConcurrentModificationException e) {
            MyLogger.logMsg("The statRequest could not be executed due to a ConcurrentModificationException. " +
                    "This likely happened because Bukkit hasn't fully initialized all player-data yet. " +
                    "Try again and it should be fine!", true);
            throw new ConcurrentModificationException(e.toString());
        }

        MyLogger.actionFinished(2);
        ThreadManager.recordCalcTime(System.currentTimeMillis() - time);
        MyLogger.logTimeTaken("StatThread", "calculated all stats", time, DebugLevel.MEDIUM);

        return allStats;
    }

    private StatAction getStatTask(StatRequest statRequest) {
        int size = offlinePlayerHandler.getOfflinePlayerCount() != 0 ? offlinePlayerHandler.getOfflinePlayerCount() : 16;
        ConcurrentHashMap<String, Integer> allStats = new ConcurrentHashMap<>(size);
        ImmutableList<String> playerNames = ImmutableList.copyOf(offlinePlayerHandler.getOfflinePlayerNames());

        StatAction task = new StatAction(offlinePlayerHandler, playerNames, statRequest, allStats);
        MyLogger.actionCreated(playerNames.size());

        return task;
    }
}