package com.gmail.artemis.the.gr8.playerstats.api;


import com.gmail.artemis.the.gr8.playerstats.statistic.StatManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.*;

import static org.jetbrains.annotations.ApiStatus.Internal;

/** The implementation of the API Interface */
public final class PlayerStatsAPI implements PlayerStats {

    private static StatCalculator statCalculator;
    private static StatFormatter statFormatter;

    @Internal
    public PlayerStatsAPI(StatManager stat, StatFormatter format) {
        statCalculator = stat;
        statFormatter = format;
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
    public Formatter getFormatter() {
        return null;
    }

    static StatCalculator statCalculator() {
        return statCalculator;
    }

    static StatFormatter statFormatter() {
        return statFormatter;
    }
}