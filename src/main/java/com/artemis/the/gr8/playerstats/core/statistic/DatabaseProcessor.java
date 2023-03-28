package com.artemis.the.gr8.playerstats.core.statistic;

import com.artemis.the.gr8.playerstats.api.StatRequest;
import com.artemis.the.gr8.playerstats.api.StatResult;
import com.artemis.the.gr8.playerstats.core.database.Database;
import com.artemis.the.gr8.playerstats.core.msg.OutputManager;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

public class DatabaseProcessor extends RequestProcessor {

    private final OutputManager outputManager;
    private final Database database;
    private boolean isActive;

    public DatabaseProcessor(OutputManager outputManager, Database database) {
        this.outputManager = outputManager;
        this.database = database;
    }

    public boolean isActive() {
        return database.isRunning();
    }

    @Override
    public @NotNull StatResult<Integer> processPlayerRequest(StatRequest<?> playerStatRequest) {
        return null;
    }

    @Override
    public @NotNull StatResult<Long> processServerRequest(StatRequest<?> serverStatRequest) {
        return null;
    }

    @Override
    public @NotNull StatResult<LinkedHashMap<String, Integer>> processTopRequest(StatRequest<?> topStatRequest) {
        return null;
    }
}
