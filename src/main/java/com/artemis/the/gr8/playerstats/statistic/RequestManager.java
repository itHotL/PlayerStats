package com.artemis.the.gr8.playerstats.statistic;

import com.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.artemis.the.gr8.playerstats.api.StatManager;
import com.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

/**
 * Turns user input into a {@link StatRequest} that can be
 * used to get statistic data.
 */
public final class RequestManager implements StatManager {

    private static OfflinePlayerHandler offlinePlayerHandler;
    private final RequestProcessor processor;

    public RequestManager(OfflinePlayerHandler offlinePlayerHandler, RequestProcessor processor) {
        RequestManager.offlinePlayerHandler = offlinePlayerHandler;
        this.processor = processor;
    }

    public StatResult<?> execute(@NotNull StatRequest<?> request) {
        return switch (request.getSettings().getTarget()) {
            case PLAYER -> processor.processPlayerRequest(request.getSettings());
            case SERVER -> processor.processServerRequest(request.getSettings());
            case TOP -> processor.processTopRequest(request.getSettings());
        };
    }

    @Override
    public RequestGenerator<Integer> createPlayerStatRequest(String playerName) {
        return new PlayerStatRequest(playerName);
    }

    @Override
    public StatResult<Integer> executePlayerStatRequest(StatRequest<Integer> request) {
        return processor.processPlayerRequest(request.getSettings());
    }

    @Override
    public RequestGenerator<Long> createServerStatRequest() {
        return new ServerStatRequest();
    }

    @Override
    public StatResult<Long> executeServerStatRequest(StatRequest<Long> request) {
        return processor.processServerRequest(request.getSettings());
    }

    @Override
    public RequestGenerator<LinkedHashMap<String, Integer>> createTopStatRequest(int topListSize) {
        return new TopStatRequest(topListSize);
    }

    @Override
    public RequestGenerator<LinkedHashMap<String, Integer>> createTotalTopStatRequest() {
        int playerCount = offlinePlayerHandler.getOfflinePlayerCount();
        return createTopStatRequest(playerCount);
    }

    @Override
    public StatResult<LinkedHashMap<String, Integer>> executeTopRequest(StatRequest<LinkedHashMap<String, Integer>> request) {
        return processor.processTopRequest(request.getSettings());
    }
}