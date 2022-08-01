package com.gmail.artemis.the.gr8.playerstats.statistic.request;

import com.gmail.artemis.the.gr8.playerstats.api.RequestExecutor;
import com.gmail.artemis.the.gr8.playerstats.statistic.result.ServerStatResult;
import com.gmail.artemis.the.gr8.playerstats.statistic.result.StatResult;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

public final class ServerStatRequest implements RequestExecutor<Long> {

    private final StatRequestHandler statRequestHandler;

    public ServerStatRequest(StatRequestHandler statRequestHandler) {
        this.statRequestHandler = statRequestHandler;
    }

    @Override
    public StatResult<Long> untyped(Statistic statistic) {
        StatRequest completedRequest = statRequestHandler.untyped(statistic);
        return getStatResult(completedRequest);
    }

    @Override
    public StatResult<Long> blockOrItemType(Statistic statistic, Material material) {
        StatRequest completedRequest = statRequestHandler.blockOrItemType(statistic, material);
        return getStatResult(completedRequest);
    }

    @Override
    public StatResult<Long> entityType(Statistic statistic, EntityType entityType) {
        StatRequest completedRequest = statRequestHandler.entityType(statistic, entityType);
        return getStatResult(completedRequest);
    }

    private ServerStatResult getStatResult(StatRequest completedRequest) {
        long stat = RequestExecutor.super.getStatCalculator()
                .getServerStat(completedRequest);
        TextComponent prettyStat = RequestExecutor.super.getStatFormatter()
                .formatServerStat(completedRequest, stat);

        return new ServerStatResult(stat, prettyStat);
    }
}