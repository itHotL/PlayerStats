package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.Main;

public interface PlayerStats extends RequestManager, StatManager, StatFormatter {

    static PlayerStats getAPI() {
        return Main.getPlayerStatsAPI();
    }
}