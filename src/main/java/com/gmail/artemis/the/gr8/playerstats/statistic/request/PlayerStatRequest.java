package com.gmail.artemis.the.gr8.playerstats.statistic.request;

import com.gmail.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.gmail.artemis.the.gr8.playerstats.api.RequestExecutor;
import com.gmail.artemis.the.gr8.playerstats.statistic.result.PlayerStatResult;
import com.gmail.artemis.the.gr8.playerstats.statistic.result.StatResult;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public final class PlayerStatRequest implements RequestGenerator<Integer>, RequestExecutor<Integer> {

    private final StatRequest statRequest;
    private final StatRequestHandler statRequestHandler;

    public PlayerStatRequest(StatRequest request) {
        statRequest = request;
        statRequestHandler = new StatRequestHandler(request);
    }

    @Override
    public RequestExecutor<Integer> untyped(@NotNull Statistic statistic) {
        StatRequest completedRequest = statRequestHandler.untyped(statistic);
        return new PlayerStatRequest(completedRequest);
    }

    @Override
    public RequestExecutor<Integer> blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) {
        StatRequest completedRequest = statRequestHandler.blockOrItemType(statistic, material);
        return new PlayerStatRequest(completedRequest);
    }

    @Override
    public RequestExecutor<Integer> entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) {
        StatRequest completedRequest = statRequestHandler.entityType(statistic, entityType);
        return new PlayerStatRequest(completedRequest);
    }

    @Override
    public StatResult<Integer> execute() {
        return getStatResult(statRequest);
    }

    private PlayerStatResult getStatResult(StatRequest completedRequest) {
        int stat = RequestExecutor.getStatCalculator()
                .getPlayerStat(completedRequest);
        TextComponent prettyStat = RequestExecutor.getStatFormatter()
                .formatPlayerStat(completedRequest, stat);

        return new PlayerStatResult(stat, prettyStat);
    }
}