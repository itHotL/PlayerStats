package com.artemis.the.gr8.playerstats.api;

import com.artemis.the.gr8.playerstats.msg.OutputManager;
import com.artemis.the.gr8.playerstats.statistic.PlayerStatRequest;
import com.artemis.the.gr8.playerstats.statistic.ServerStatRequest;
import com.artemis.the.gr8.playerstats.statistic.TopStatRequest;
import com.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;

import java.util.LinkedHashMap;

import static org.jetbrains.annotations.ApiStatus.Internal;

/** The implementation of the API Interface */
public final class PlayerStatsImpl implements PlayerStats, StatManager {

    private static OutputManager outputManager;
    private final OfflinePlayerHandler offlinePlayerHandler;

    @Internal
    public PlayerStatsImpl(OfflinePlayerHandler offlinePlayerHandler, OutputManager outputManager) {
        PlayerStatsImpl.outputManager = outputManager;
        this.offlinePlayerHandler = offlinePlayerHandler;
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