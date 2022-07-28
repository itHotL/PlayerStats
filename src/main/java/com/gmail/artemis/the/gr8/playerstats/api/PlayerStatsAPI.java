package com.gmail.artemis.the.gr8.playerstats.api;


import com.gmail.artemis.the.gr8.playerstats.statistic.request.InternalStatFetcher;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.PlayerStatFetcher;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.ServerStatFetcher;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.TopStatFetcher;

import static org.jetbrains.annotations.ApiStatus.Internal;

/** The implementation of the API Interface */
public final class PlayerStatsAPI implements PlayerStats {

    private static InternalStatFetcher requestGenerator;
    private static StatCalculator statCalculator;
    private static StatFormatter statFormatter;

    @Internal
    public PlayerStatsAPI(InternalStatFetcher request, StatManager stat, StatFormatter format) {
        PlayerStatsAPI.requestGenerator = request;
        statCalculator = stat;
        statFormatter = format;
    }

    @Override
    public PlayerStatFetcher playerStat(String playerName) {
        return new PlayerStatFetcher(playerName);
    }

    @Override
    public ServerStatFetcher serverStat() {
        return new ServerStatFetcher();
    }

    @Override
    public TopStatFetcher topStat(int topListSize) {
        return new TopStatFetcher(topListSize);
    }
}