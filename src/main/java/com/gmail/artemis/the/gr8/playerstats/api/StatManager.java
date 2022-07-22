package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;

import java.util.LinkedHashMap;

public interface StatManager extends RequestManager {

    //use ThreadManager.startStatThread
    LinkedHashMap<String, Integer> getTopStats(StatRequest request);

    long getServerStat(StatRequest request);

    int getPlayerStat(StatRequest request);

}
