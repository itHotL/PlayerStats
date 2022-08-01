package com.gmail.artemis.the.gr8.playerstats.api;


import com.gmail.artemis.the.gr8.playerstats.statistic.StatManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.PlayerStatRequest;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.RequestManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.StatRequest;

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
        StatRequest request = RequestManager.generateBasicPlayerRequest(playerName);
        return new PlayerStatRequest(request);
    }

    @Override
    public RequestGenerator serverStat() {
        StatRequest request = RequestManager.generateBasicServerRequest();
        return new RequestManager(request);
    }

    @Override
    public RequestGenerator topStat(int topListSize) {
        StatRequest request = RequestManager.generateBasicTopRequest(topListSize);
        return new RequestManager(request);
    }
}