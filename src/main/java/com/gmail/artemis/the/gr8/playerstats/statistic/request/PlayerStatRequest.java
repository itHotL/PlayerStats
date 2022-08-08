package com.gmail.artemis.the.gr8.playerstats.statistic.request;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.gmail.artemis.the.gr8.playerstats.statistic.result.PlayerStatResult;
import com.gmail.artemis.the.gr8.playerstats.statistic.result.StatResult;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public final class PlayerStatRequest extends StatRequest<Integer> implements RequestGenerator<Integer> {

    private final RequestHandler requestHandler;

    public PlayerStatRequest(RequestSettings request) {
        super(request);
        requestHandler = new RequestHandler(request);
    }

    @Override
    public StatRequest<Integer> untyped(@NotNull Statistic statistic) {
        RequestSettings completedRequest = requestHandler.untyped(statistic);
        return new PlayerStatRequest(completedRequest);
    }

    @Override
    public StatRequest<Integer> blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) {
        RequestSettings completedRequest = requestHandler.blockOrItemType(statistic, material);
        return new PlayerStatRequest(completedRequest);
    }

    @Override
    public StatRequest<Integer> entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) {
        RequestSettings completedRequest = requestHandler.entityType(statistic, entityType);
        return new PlayerStatRequest(completedRequest);
    }

    @Override
    public StatResult<Integer> execute() {
        return getStatResult(super.requestSettings);
    }

    private PlayerStatResult getStatResult(RequestSettings completedRequest) {
        int stat = Main.getStatCalculator()
                .getPlayerStat(completedRequest);
        TextComponent prettyStat = Main.getStatFormatter()
                .formatPlayerStat(completedRequest, stat);

        return new PlayerStatResult(stat, prettyStat);
    }
}