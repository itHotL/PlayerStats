package com.gmail.artemis.the.gr8.playerstats.api;


import com.gmail.artemis.the.gr8.playerstats.statistic.StatRetriever;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.*;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;

import static org.jetbrains.annotations.ApiStatus.Internal;

/** The implementation of the API Interface */
public final class PlayerStatsAPI implements PlayerStats, StatManager {

    private final OfflinePlayerHandler offlinePlayerHandler;
    private static StatRetriever statRetriever;
    private static StatFormatter statFormatter;

    @Internal
    public PlayerStatsAPI(StatRetriever stat, StatFormatter format, OfflinePlayerHandler offlinePlayers) {
        statRetriever = stat;
        statFormatter = format;
        offlinePlayerHandler = offlinePlayers;
    }

    static StatRetriever statCalculator() {
        return statRetriever;
    }

    static StatFormatter statFormatter() {
        return statFormatter;
    }

    @Override
    public Formatter getFormatter() {
        return statFormatter;
    }

    @Override
    public StatManager getStatManager() {
        return this;
    }

    @Override
    public RequestGenerator<Integer> getPlayerStat(String playerName) {
        StatRequest request = StatRequestHandler.getBasicPlayerStatRequest(playerName);
        return new PlayerStatRequest(request);
    }

    @Override
    public ServerStatRequest calculateServerStat() {
        StatRequest request = StatRequestHandler.getBasicServerStatRequest();
        return new ServerStatRequest(request);
    }

    @Override
    public TopStatRequest calculateTopStat(int topListSize) {
        StatRequest request = StatRequestHandler.getBasicTopStatRequest(topListSize);
        return new TopStatRequest(request);
    }

    @Override
    public TopStatRequest calculateTotalTopStatList() {
        int playerCount = offlinePlayerHandler.getOfflinePlayerCount();
        return calculateTopStat(playerCount);
    }
}