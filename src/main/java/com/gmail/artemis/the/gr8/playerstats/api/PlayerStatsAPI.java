package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.statistic.request.*;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;

import static org.jetbrains.annotations.ApiStatus.Internal;

/** The implementation of the API Interface */
public final class PlayerStatsAPI implements PlayerStats, StatManager {

    private final OfflinePlayerHandler offlinePlayerHandler;
    private static StatFormatter statFormatter;

    @Internal
    public PlayerStatsAPI(StatFormatter format, OfflinePlayerHandler offlinePlayers) {
        statFormatter = format;
        offlinePlayerHandler = offlinePlayers;
    }

    @Override
    public Formatter getFormatter() {
        return statFormatter;
    }

    @Override
    public StatManager getStatManager() {
        return this;
    }

    @Override
    public RequestGenerator<Integer> playerStatRequest(String playerName) {
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
    public TopStatRequest totalTopStatListRequest() {
        int playerCount = offlinePlayerHandler.getOfflinePlayerCount();
        return topStatRequest(playerCount);
    }
}