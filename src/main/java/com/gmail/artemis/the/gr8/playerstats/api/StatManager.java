package com.gmail.artemis.the.gr8.playerstats.api;

import java.util.LinkedHashMap;

public interface StatManager {

    /** Gets a StatRequest object that can be used to look up a player-statistic.
     This StatRequest will have all default settings already configured,
     and will be processed as soon as you call one of its methods.

     @return a PlayerStatRequest that can be used to look up a statistic for the
     Player whose name is provided*/
    RequestGenerator<Integer> getPlayerStat(String playerName);

    /** Gets a StatRequest object that can be used to look up a server-statistic.
     This StatRequest will have all default settings already configured,
     and will be processed as soon as you call one of its methods.
     <br>
     <br> Don't call this from the main Thread! (see class description)

     @return a ServerStatRequest that can be used to look up a server total*/
    RequestGenerator<Long> calculateServerStat();

    /** Gets a StatRequest object that can be used to look up a top-x-statistic.
     This StatRequest will have all default settings already configured, and will be
     processed as soon as you call one of its methods.
     <br>
     <br> Don't call this from the main Thread! (see class description)

     @param topListSize how big the top-x should be (10 by default)
     @return a TopStatRequest that can be used to look up a top statistic*/
    RequestGenerator<LinkedHashMap<String, Integer>> calculateTopStat(int topListSize);

    RequestGenerator<LinkedHashMap<String, Integer>> calculateTotalTopStatList();
}
