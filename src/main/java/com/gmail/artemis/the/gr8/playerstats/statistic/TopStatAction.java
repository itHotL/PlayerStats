package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;


public class TopStatAction extends RecursiveTask<ConcurrentHashMap<String, Integer>> {

    private static final int threshold = 10;

    private final List<String> playerNames;
    private final StatRequest request;
    private final ConcurrentHashMap<String, Integer> playerStats;

    /**
     * Gets the statistic numbers for all players whose name is on the list, puts them in a ConcurrentHashMap
     * using the default ForkJoinPool, and returns the top x as a sorted LinkedHashMap
     */

    public TopStatAction(List<String> playerNames, StatRequest statRequest, ConcurrentHashMap<String, Integer> playerStats) {
        this.playerNames = playerNames;
        request = statRequest;
        this.playerStats = playerStats;
    }

    @Override
    protected ConcurrentHashMap<String, Integer> compute() {
        if (playerNames.size() < threshold) {
            return getStatsDirectly();
        }
        else {
            TopStatAction subTask1 = new TopStatAction(playerNames.subList(0, (playerNames.size()/2)),
                    request, playerStats);
            TopStatAction subTask2 = new TopStatAction(playerNames.subList((playerNames.size()/2), playerNames.size()),
                    request, playerStats);

            //queue and compute all subtasks in the right order
            subTask1.fork();
            subTask2.compute();
            subTask1.join();

            return playerStats;
        }
    }

    private ConcurrentHashMap<String, Integer> getStatsDirectly() throws IllegalArgumentException {
        playerNames.forEach(playerName -> {
            try {
                OfflinePlayer player = OfflinePlayerHandler.getOfflinePlayer(playerName);
                int statistic = 0;
                switch (request.getStatType()) {
                    case UNTYPED ->
                        statistic = player.getStatistic(request.getStatEnum());
                    case ENTITY ->
                        statistic = player.getStatistic(request.getStatEnum(), request.getEntity());
                    case BLOCK ->
                        statistic = player.getStatistic(request.getStatEnum(), request.getBlock());
                    case ITEM ->
                        statistic = player.getStatistic(request.getStatEnum(), request.getItem());
                    }

                if (statistic > 0) {
                    playerStats.put(playerName, statistic);
                }
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning(e.toString());
            }
        });
        return playerStats;
    }
}
