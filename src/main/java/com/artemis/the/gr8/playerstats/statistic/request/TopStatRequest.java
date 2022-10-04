package com.artemis.the.gr8.playerstats.statistic.request;

import com.artemis.the.gr8.playerstats.Main;
import com.artemis.the.gr8.playerstats.statistic.result.TopStatResult;
import com.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.artemis.the.gr8.playerstats.msg.components.ComponentUtils;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

public final class TopStatRequest extends StatRequest<LinkedHashMap<String, Integer>> implements RequestGenerator<LinkedHashMap<String, Integer>> {

    private final RequestHandler requestHandler;

    public TopStatRequest(RequestSettings request) {
        super(request);
        requestHandler = new RequestHandler(request);
    }

    @Override
    public TopStatRequest untyped(@NotNull Statistic statistic) {
        RequestSettings completedRequest = requestHandler.untyped(statistic);
        return new TopStatRequest(completedRequest);
    }

    @Override
    public TopStatRequest blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) {
        RequestSettings completedRequest = requestHandler.blockOrItemType(statistic, material);
        return new TopStatRequest(completedRequest);
    }

    @Override
    public TopStatRequest entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) {
        RequestSettings completedRequest = requestHandler.entityType(statistic, entityType);
        return new TopStatRequest(completedRequest);
    }

    @Override
    public TopStatResult execute() {
        return getStatResult(super.requestSettings);
    }

    private TopStatResult getStatResult(RequestSettings completedRequest) {
        LinkedHashMap<String, Integer> stat = Main
                .getStatCalculator()
                .getTopStats(completedRequest);

        TextComponent prettyComponent = Main
                .getOutputManager()
                .formatAndSaveTopStat(completedRequest, stat);

        String prettyString = ComponentUtils
                .getTranslatableComponentSerializer()
                .serialize(prettyComponent);

        return new TopStatResult(stat, prettyComponent, prettyString);
    }
}