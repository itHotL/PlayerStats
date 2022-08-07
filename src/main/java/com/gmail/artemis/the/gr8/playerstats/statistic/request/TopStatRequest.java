package com.gmail.artemis.the.gr8.playerstats.statistic.request;

import com.gmail.artemis.the.gr8.playerstats.api.RequestExecutor;
import com.gmail.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.gmail.artemis.the.gr8.playerstats.statistic.result.StatResult;
import com.gmail.artemis.the.gr8.playerstats.statistic.result.TopStatResult;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

public final class TopStatRequest implements RequestGenerator<LinkedHashMap<String, Integer>>, RequestExecutor<LinkedHashMap<String, Integer>> {

    private final StatRequest statRequest;
    private final StatRequestHandler statRequestHandler;

    public TopStatRequest(StatRequest request) {
        statRequest = request;
        statRequestHandler = new StatRequestHandler(request);
    }

    @Override
    public TopStatRequest untyped(@NotNull Statistic statistic) {
        StatRequest completedRequest = statRequestHandler.untyped(statistic);
        return new TopStatRequest(completedRequest);
    }

    @Override
    public TopStatRequest blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) {
        StatRequest completedRequest = statRequestHandler.blockOrItemType(statistic, material);
        return new TopStatRequest(completedRequest);
    }

    @Override
    public TopStatRequest entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) {
        StatRequest completedRequest = statRequestHandler.entityType(statistic, entityType);
        return new TopStatRequest(completedRequest);
    }

    @Override
    public StatResult<LinkedHashMap<String, Integer>> execute() {
        return getStatResult(statRequest);
    }

    private TopStatResult getStatResult(StatRequest completedRequest) {
        LinkedHashMap<String, Integer> stat = RequestExecutor.getStatCalculator()
                .getTopStats(completedRequest);
        TextComponent prettyStat = RequestExecutor.getStatFormatter()
                .formatTopStat(completedRequest, stat);

        return new TopStatResult(stat, prettyStat);
    }
}