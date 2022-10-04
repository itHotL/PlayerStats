package com.artemis.the.gr8.playerstats.statistic.request;

import com.artemis.the.gr8.playerstats.Main;
import com.artemis.the.gr8.playerstats.statistic.result.PlayerStatResult;
import com.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.artemis.the.gr8.playerstats.msg.components.ComponentUtils;
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
    public PlayerStatRequest untyped(@NotNull Statistic statistic) {
        RequestSettings completedRequest = requestHandler.untyped(statistic);
        return new PlayerStatRequest(completedRequest);
    }

    @Override
    public PlayerStatRequest blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) {
        RequestSettings completedRequest = requestHandler.blockOrItemType(statistic, material);
        return new PlayerStatRequest(completedRequest);
    }

    @Override
    public PlayerStatRequest entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) {
        RequestSettings completedRequest = requestHandler.entityType(statistic, entityType);
        return new PlayerStatRequest(completedRequest);
    }

    @Override
    public PlayerStatResult execute() {
        return getStatResult(super.requestSettings);
    }

    private PlayerStatResult getStatResult(RequestSettings completedRequest) {
        int stat = Main
                .getStatCalculator()
                .getPlayerStat(completedRequest);

        TextComponent prettyComponent = Main
                .getOutputManager()
                .formatAndSavePlayerStat(completedRequest, stat);

        String prettyString = ComponentUtils
                .getTranslatableComponentSerializer()
                .serialize(prettyComponent);

        return new PlayerStatResult(stat, prettyComponent, prettyString);
    }
}