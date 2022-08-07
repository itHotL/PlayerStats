package com.gmail.artemis.the.gr8.playerstats.statistic.request;

import com.gmail.artemis.the.gr8.playerstats.api.RequestExecutor;
import com.gmail.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.gmail.artemis.the.gr8.playerstats.statistic.result.ServerStatResult;
import com.gmail.artemis.the.gr8.playerstats.statistic.result.StatResult;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public final class ServerStatRequest implements RequestGenerator<Long>, RequestExecutor<Long> {

    private final StatRequest statRequest;
    private final StatRequestHandler statRequestHandler;

    public ServerStatRequest(StatRequest request) {
        statRequest = request;
        statRequestHandler = new StatRequestHandler(statRequest);
    }

    @Override
    public RequestExecutor<Long> untyped(@NotNull Statistic statistic) {
        StatRequest completedRequest = statRequestHandler.untyped(statistic);
        return new ServerStatRequest(completedRequest);
    }

    @Override
    public RequestExecutor<Long> blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) {
        StatRequest completedRequest = statRequestHandler.blockOrItemType(statistic, material);
        return new ServerStatRequest(completedRequest);
    }

    @Override
    public RequestExecutor<Long> entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) {
        StatRequest completedRequest = statRequestHandler.entityType(statistic, entityType);
        return new ServerStatRequest(completedRequest);
    }

    @Override
    public StatResult<Long> execute() {
        return getStatResult(statRequest);
    }

    private ServerStatResult getStatResult(StatRequest completedRequest) {
        long stat = RequestExecutor.getStatCalculator()
                .getServerStat(completedRequest);
        TextComponent prettyStat = RequestExecutor.getStatFormatter()
                .formatServerStat(completedRequest, stat);

        return new ServerStatResult(stat, prettyStat);
    }
}