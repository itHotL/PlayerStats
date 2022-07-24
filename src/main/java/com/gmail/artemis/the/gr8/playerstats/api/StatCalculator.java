package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;

import java.util.LinkedHashMap;

/** The {@link StatCalculator} represents the actual statistic-getting magic that happens once a valid
 {@link StatRequest} has been obtained. It takes a valid StatRequest, and returns (a map of) numbers. */
public interface StatCalculator {

    int getPlayerStat(StatRequest request);

    long getServerStat(StatRequest request);

    LinkedHashMap<String, Integer> getTopStats(StatRequest request);
}
