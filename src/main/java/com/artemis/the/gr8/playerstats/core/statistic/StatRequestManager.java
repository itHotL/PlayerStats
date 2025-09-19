package com.artemis.the.gr8.playerstats.core.statistic;

import com.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.artemis.the.gr8.playerstats.api.StatManager;
import com.artemis.the.gr8.playerstats.api.StatRequest;
import com.artemis.the.gr8.playerstats.api.StatResult;
import com.artemis.the.gr8.playerstats.core.Main;
import com.artemis.the.gr8.playerstats.core.msg.OutputManager;
import com.artemis.the.gr8.playerstats.core.utils.MyLogger;
import com.artemis.the.gr8.playerstats.core.utils.OfflinePlayerHandler;
import com.artemis.the.gr8.playerstats.core.utils.Reloadable;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Turns user input into a {@link StatRequest} that can be
 * executed to get statistic data.
 */
public final class StatRequestManager implements StatManager, Reloadable {

    private static RequestProcessor processor;
    private final OfflinePlayerHandler offlinePlayerHandler;

    public StatRequestManager() {
        offlinePlayerHandler = OfflinePlayerHandler.getInstance();
        processor = getProcessor();
        Main.registerReloadable(this);
    }

    @Override
    public void reload() {
        processor = getProcessor();
    }

    private @NotNull RequestProcessor getProcessor() {
        OutputManager outputManager = OutputManager.getInstance();

        boolean huskSyncAvailable = Bukkit.getPluginManager().getPlugin("HuskSync") != null;

        if (huskSyncAvailable) {
            MyLogger.logLowLevelMsg("HuskSync detected, using HuskSync for player data retrieval.");
            return new HuskSyncProcessor(outputManager);
        }

        MyLogger.logLowLevelMsg("Using Bukkit API for player data retrieval.");
        return new BukkitProcessor(outputManager);
    }

    // TODO: This is not a good type declaration. Change it later.
    public static @NotNull CompletableFuture<?> execute(@NotNull StatRequest<?> request) {
        return switch (request.getSettings().getTarget()) {
            case PLAYER -> processor.processPlayerRequest(request);
            case SERVER -> processor.processServerRequest(request);
            case TOP -> processor.processTopRequest(request);
        };
    }

    @Override
    public boolean isExcludedPlayer(String playerName) {
        return offlinePlayerHandler.isExcludedPlayer(playerName);
    }

    @Contract("_ -> new")
    @Override
    public @NotNull RequestGenerator<Integer> createPlayerStatRequest(String playerName) {
        return new PlayerStatRequest(playerName);
    }

    @Override
    public @NotNull CompletableFuture<StatResult<Integer>> executePlayerStatRequest(@NotNull StatRequest<Integer> request) {
        return processor.processPlayerRequest(request);
    }

    @Contract(" -> new")
    @Override
    public @NotNull RequestGenerator<Long> createServerStatRequest() {
        return new ServerStatRequest();
    }

    @Override
    public @NotNull CompletableFuture<StatResult<Long>> executeServerStatRequest(@NotNull StatRequest<Long> request) {
        return processor.processServerRequest(request);
    }

    @Contract("_ -> new")
    @Override
    public @NotNull RequestGenerator<LinkedHashMap<String, Integer>> createTopStatRequest(int topListSize) {
        return new TopStatRequest(topListSize);
    }

    @Override
    public @NotNull RequestGenerator<LinkedHashMap<String, Integer>> createTotalTopStatRequest() {
        int playerCount = offlinePlayerHandler.getIncludedPlayerCount();
        return createTopStatRequest(playerCount);
    }

    @Override
    public @NotNull CompletableFuture<StatResult<LinkedHashMap<String, Integer>>> executeTopRequest(@NotNull StatRequest<LinkedHashMap<String, Integer>> request) {
        return processor.processTopRequest(request);
    }
}