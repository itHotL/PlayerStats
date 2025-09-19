package com.artemis.the.gr8.playerstats.core.statistic;

import com.artemis.the.gr8.playerstats.api.StatRequest;
import com.artemis.the.gr8.playerstats.api.StatResult;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.concurrent.CompletableFuture;

public abstract class RequestProcessor {

    abstract @NotNull CompletableFuture<StatResult<Integer>> processPlayerRequest(StatRequest<?> playerStatRequest);

    abstract @NotNull CompletableFuture<StatResult<Long>> processServerRequest(StatRequest<?> serverStatRequest);

    abstract @NotNull CompletableFuture<StatResult<LinkedHashMap<String, Integer>>> processTopRequest(StatRequest<?> topStatRequest);
}
