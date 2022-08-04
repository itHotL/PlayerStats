package com.gmail.artemis.the.gr8.playerstats.statistic.request;

import com.gmail.artemis.the.gr8.playerstats.api.RequestExecutor;
import com.gmail.artemis.the.gr8.playerstats.statistic.result.PlayerStatResult;
import com.gmail.artemis.the.gr8.playerstats.statistic.result.StatResult;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public final class PlayerStatRequest implements RequestExecutor<Integer> {

    private final StatRequestHandler statRequestHandler;

    public PlayerStatRequest(StatRequestHandler statRequestHandler) {
        this.statRequestHandler = statRequestHandler;
    }

    @Override
    public StatResult<Integer> untyped(@NotNull Statistic statistic) {
        StatRequest completedRequest = statRequestHandler.untyped(statistic);
        return getStatResult(completedRequest);
    }

    @Override
    public StatResult<Integer> blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) {
        StatRequest completedRequest = statRequestHandler.blockOrItemType(statistic, material);
        return getStatResult(completedRequest);
    }

    @Override
    public StatResult<Integer> entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) {
        StatRequest completedRequest = statRequestHandler.entityType(statistic, entityType);
        return getStatResult(completedRequest);
    }

    private PlayerStatResult getStatResult(StatRequest completedRequest) {
        int stat = RequestExecutor.super.getStatCalculator()
                .getPlayerStat(completedRequest);
        TextComponent prettyStat = RequestExecutor.super.getStatFormatter()
                .formatPlayerStat(completedRequest, stat);

        return new PlayerStatResult(stat, prettyStat);
    }
}