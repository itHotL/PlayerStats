package com.gmail.artemis.the.gr8.playerstats.api;


import com.gmail.artemis.the.gr8.playerstats.statistic.StatManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.*;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;

import static org.jetbrains.annotations.ApiStatus.Internal;

/** The implementation of the API Interface */
public final class PlayerStatsAPI implements PlayerStats {

    private final OfflinePlayerHandler offlinePlayerHandler;
    private static StatCalculator statCalculator;
    private static StatFormatter statFormatter;

    @Internal
    public PlayerStatsAPI(StatManager stat, StatFormatter format, OfflinePlayerHandler offlinePlayers) {
        statCalculator = stat;
        statFormatter = format;
        offlinePlayerHandler = offlinePlayers;
    }

    @Override
    public PlayerStatRequest playerStat(String playerName) {
        StatRequestHandler statRequestHandler = StatRequestHandler.playerRequestHandler(playerName);
        return new PlayerStatRequest(statRequestHandler);
    }

    @Override
    public ServerStatRequest serverStat() {
        StatRequestHandler statRequestHandler = StatRequestHandler.serverRequestHandler();
        return new ServerStatRequest(statRequestHandler);
    }

    @Override
    public TopStatRequest topStat(int topListSize) {
        StatRequestHandler statRequestHandler = StatRequestHandler.topRequestHandler(topListSize);
        return new TopStatRequest(statRequestHandler);
    }

    @Override
    public TopStatRequest totalTopStatList() {
        int playerCount = offlinePlayerHandler.getOfflinePlayerCount();
        return topStat(playerCount);
    }

    @Override
    public Formatter getFormatter() {
        return statFormatter;
    }

    static StatCalculator statCalculator() {
        return statCalculator;
    }

    static StatFormatter statFormatter() {
        return statFormatter;
    }
}