package com.artemis.the.gr8.playerstats.core.statistic;

import com.artemis.the.gr8.playerstats.api.StatRequest;
import com.artemis.the.gr8.playerstats.api.StatResult;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

public abstract class RequestProcessor {

    abstract @NotNull StatResult<Integer> processPlayerRequest(StatRequest<?> playerStatRequest);

    abstract @NotNull StatResult<Long> processServerRequest(StatRequest<?> serverStatRequest);

    abstract @NotNull StatResult<LinkedHashMap<String, Integer>> processTopRequest(StatRequest<?> topStatRequest);
}
