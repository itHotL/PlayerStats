package com.gmail.artemis.the.gr8.playerstats.statistic.request;

import com.gmail.artemis.the.gr8.playerstats.api.RequestExecutor;
import com.gmail.artemis.the.gr8.playerstats.statistic.result.StatResult;
import com.gmail.artemis.the.gr8.playerstats.statistic.result.TopStatResult;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

import java.util.LinkedHashMap;

public final class TopStatRequest implements RequestExecutor<LinkedHashMap<String, Integer>> {

    private final StatRequestHandler statRequestHandler;

    public TopStatRequest(StatRequestHandler statRequestHandler) {
        this.statRequestHandler = statRequestHandler;
    }

    @Override
    public StatResult<LinkedHashMap<String, Integer>> untyped(Statistic statistic) {
        StatRequest completedRequest = statRequestHandler.untyped(statistic);
        return getStatResult(completedRequest);
    }

    @Override
    public StatResult<LinkedHashMap<String, Integer>> blockOrItemType(Statistic statistic, Material material) {
        StatRequest completedRequest = statRequestHandler.blockOrItemType(statistic, material);
        return getStatResult(completedRequest);
    }

    @Override
    public StatResult<LinkedHashMap<String, Integer>> entityType(Statistic statistic, EntityType entityType) {
        StatRequest completedRequest = statRequestHandler.entityType(statistic, entityType);
        return getStatResult(completedRequest);
    }

    private TopStatResult getStatResult(StatRequest completedRequest) {
        LinkedHashMap<String, Integer> stat = RequestExecutor.super.getStatCalculator()
                .getTopStats(completedRequest);
        TextComponent prettyStat = RequestExecutor.super.getStatFormatter()
                .formatTopStat(completedRequest, stat);

        return new TopStatResult(stat, prettyStat);
    }
}