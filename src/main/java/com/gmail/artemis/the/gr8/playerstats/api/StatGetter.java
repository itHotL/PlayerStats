package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;

import java.util.LinkedHashMap;

public interface StatGetter {

    int getPlayerStat(StatRequest request);

    long getServerStat(StatRequest request);

    LinkedHashMap<String, Integer> getTopStats(StatRequest request);
}
