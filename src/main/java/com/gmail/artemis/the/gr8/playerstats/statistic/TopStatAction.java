package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;


public class TopStatAction extends RecursiveAction {

    private static final int threshold = 11;

    private final String[] playerNames;
    private final StatRequest request;
    private final ConcurrentHashMap<String, Integer> playerStats;

    /**
     * Gets the statistic numbers for all players whose name is on the list, puts them in a ConcurrentHashMap
     * using the default ForkJoinPool, and returns the ConcurrentHashMap when everything is done
     * @param playerNames List of playerNames of players that should be included in the stat calculations
     * @param statRequest a validated statRequest
     * @param playerStats the ConcurrentHashMap to put the results on
     */

    public TopStatAction(String[] playerNames, StatRequest statRequest, ConcurrentHashMap<String, Integer> playerStats) {
        this.playerNames = playerNames;
        request = statRequest;
        this.playerStats = playerStats;
    }

    @Override
    protected void compute() {
        if (playerNames.length < threshold) {
            getStatsDirectly();
        }
        else {
            Bukkit.getLogger().info("playerNames length: " + playerNames.length);
            TopStatAction subTask1 = new TopStatAction(Arrays.copyOfRange(playerNames, 0, playerNames.length/2),
                    request, playerStats);
            TopStatAction subTask2 = new TopStatAction(Arrays.copyOfRange(playerNames, playerNames.length/2, playerNames.length),
                    request, playerStats);

            //queue and compute all subtasks in the right order
            invokeAll(subTask1, subTask2);
        }
    }

    private void getStatsDirectly() throws IllegalArgumentException, ConcurrentModificationException {
        Bukkit.getLogger().info("ArrayCopy Length: " + playerNames.length);
        for (String playerName : playerNames) {
            try {
                OfflinePlayer player = OfflinePlayerHandler.getOfflinePlayer(playerName);
                int statistic = 0;
                switch (request.getStatType()) {
                    case UNTYPED -> statistic = player.getStatistic(request.getStatEnum());
                    case ENTITY -> statistic = player.getStatistic(request.getStatEnum(), request.getEntity());
                    case BLOCK -> statistic = player.getStatistic(request.getStatEnum(), request.getBlock());
                    case ITEM -> statistic = player.getStatistic(request.getStatEnum(), request.getItem());
                }

                if (statistic > 0) {
                    playerStats.put(playerName, statistic);
                }
            } catch (IllegalArgumentException ignored) {
            }
            catch (ConcurrentModificationException e) {
                Bukkit.getLogger().warning("A ConcurrentModificationException has occurred");
                throw new ConcurrentModificationException(e.toString());
            }
        }
    }
}
