package com.artemis.the.gr8.playerstats.api;

import com.artemis.the.gr8.playerstats.msg.OutputManager;

import static org.jetbrains.annotations.ApiStatus.Internal;

/** The implementation of the API Interface */
public final class PlayerStatsAPI implements PlayerStats {

    private static OutputManager outputManager;
    private final StatManager statManager;

    @Internal
    public PlayerStatsAPI(StatManager statManager, OutputManager outputManager) {
        PlayerStatsAPI.outputManager = outputManager;
        this.statManager = statManager;
    }

    @Override
    public StatFormatter getFormatter() {
        return outputManager.getMainMessageBuilder();
    }

    @Override
    public StatManager getStatManager() {
        return statManager;
    }
}