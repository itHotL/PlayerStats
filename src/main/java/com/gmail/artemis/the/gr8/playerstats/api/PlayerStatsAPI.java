package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.statistic.RequestManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatManager;

import static org.jetbrains.annotations.ApiStatus.Internal;

/** The implementation of the API Interface */
public final class PlayerStatsAPI implements PlayerStats {

    private static RequestManager requestManager;
    private static StatManager statManager;
    private static StatFormatter statFormatter;

    @Internal
    public PlayerStatsAPI(RequestManager request, StatManager stat, StatFormatter format) {
        requestManager = request;
        statManager = stat;
        statFormatter = format;
    }

    @Override
    public StatCalculator statCalculator() {
        return statManager;
    }

    @Override
    public RequestGenerator requestGenerator() {
        return requestManager;
    }

    @Override
    public StatFormatter statFormatter() {
        return statFormatter;
    }
}