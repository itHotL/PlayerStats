package com.artemis.the.gr8.playerstats.api;

import com.artemis.the.gr8.playerstats.statistic.request.*;
import com.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;

import static org.jetbrains.annotations.ApiStatus.Internal;

/** The implementation of the API Interface */
public final class PlayerStatsAPI implements PlayerStats, StatManager {

    private final OfflinePlayerHandler offlinePlayerHandler;
    private static ApiFormatter apiFormatter;

    @Internal
    public PlayerStatsAPI(ApiFormatter formatter, OfflinePlayerHandler offlinePlayers) {
        apiFormatter = formatter;
        offlinePlayerHandler = offlinePlayers;
    }

    @Override
    public ApiFormatter getFormatter() {
        return apiFormatter;
    }

    @Override
    public StatManager getStatManager() {
        return this;
    }

    @Override
    public PlayerStatRequest playerStatRequest(String playerName) {
        RequestSettings request = RequestHandler.getBasicPlayerStatRequest(playerName);
        return new PlayerStatRequest(request);
    }

    @Override
    public ServerStatRequest serverStatRequest() {
        RequestSettings request = RequestHandler.getBasicServerStatRequest();
        return new ServerStatRequest(request);
    }

    @Override
    public TopStatRequest topStatRequest(int topListSize) {
        RequestSettings request = RequestHandler.getBasicTopStatRequest(topListSize);
        return new TopStatRequest(request);
    }

    @Override
    public TopStatRequest totalTopStatRequest() {
        int playerCount = offlinePlayerHandler.getOfflinePlayerCount();
        return topStatRequest(playerCount);
    }
}