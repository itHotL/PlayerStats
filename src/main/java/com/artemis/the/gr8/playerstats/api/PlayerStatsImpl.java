package com.artemis.the.gr8.playerstats.api;

import com.artemis.the.gr8.playerstats.msg.OutputManager;
import com.artemis.the.gr8.playerstats.statistic.request.*;
import com.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;

import java.util.LinkedHashMap;

import static org.jetbrains.annotations.ApiStatus.Internal;

/** The implementation of the API Interface */
public final class PlayerStatsImpl implements PlayerStats, StatManager {

    private final OfflinePlayerHandler offlinePlayerHandler;
    private static OutputManager outputManager;

    @Internal
    public PlayerStatsImpl(OutputManager outputManager, OfflinePlayerHandler offlinePlayers) {
        PlayerStatsImpl.outputManager = outputManager;
        offlinePlayerHandler = offlinePlayers;
    }

    @Override
    public StatFormatter getFormatter() {
        return outputManager.getCurrentMainMessageBuilder();
    }

    @Override
    public StatManager getStatManager() {
        return this;
    }

    @Override
    public RequestGenerator<Integer> playerStatRequest(String playerName) {
        return new PlayerStatRequest(playerName);
    }

    @Override
    public RequestGenerator<Long> serverStatRequest() {
        return new ServerStatRequest();
    }

    @Override
    public RequestGenerator<LinkedHashMap<String, Integer>> topStatRequest(int topListSize) {
        return new TopStatRequest(topListSize);
    }

    @Override
    public RequestGenerator<LinkedHashMap<String, Integer>> totalTopStatRequest() {
        int playerCount = offlinePlayerHandler.getOfflinePlayerCount();
        return topStatRequest(playerCount);
    }
}