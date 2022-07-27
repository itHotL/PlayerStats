package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;

import java.util.LinkedHashMap;

/** The {@link StatCalculator} represents the actual statistic-getting magic that happens once a valid
 {@link StatRequest} is passed to it. It takes a valid StatRequest, and returns (a map of) numbers.
 For more information on how to create a valid StatRequest, see the class description for {@link StatRequest}.*/
public interface StatCalculator {

    /** Returns the requested Statistic*/
    int getPlayerStat(StatRequest request);

    /** Don't call from main Thread!*/
    long getServerStat(StatRequest request);

    /** Don't call from main Thread!*/
    LinkedHashMap<String, Integer> getTopStats(StatRequest request);
}
