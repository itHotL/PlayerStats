package com.artemis.the.gr8.playerstats.statistic.request;

import com.artemis.the.gr8.playerstats.Main;
import com.artemis.the.gr8.playerstats.statistic.result.ServerStatResult;
import com.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.artemis.the.gr8.playerstats.msg.components.ComponentUtils;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public final class ServerStatRequest extends StatRequest<Long> implements RequestGenerator<Long> {

    private final RequestHandler requestHandler;

    public ServerStatRequest(RequestSettings request) {
        super(request);
        requestHandler = new RequestHandler(requestSettings);
    }

    @Override
    public ServerStatRequest untyped(@NotNull Statistic statistic) {
        RequestSettings completedRequest = requestHandler.untyped(statistic);
        return new ServerStatRequest(completedRequest);
    }

    @Override
    public ServerStatRequest blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) {
        RequestSettings completedRequest = requestHandler.blockOrItemType(statistic, material);
        return new ServerStatRequest(completedRequest);
    }

    @Override
    public ServerStatRequest entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) {
        RequestSettings completedRequest = requestHandler.entityType(statistic, entityType);
        return new ServerStatRequest(completedRequest);
    }

    @Override
    public ServerStatResult execute() {
        return getStatResult(requestSettings);
    }

    private ServerStatResult getStatResult(RequestSettings completedRequest) {
        long stat = Main
                .getStatCalculator()
                .getServerStat(completedRequest);

        TextComponent prettyComponent = Main
                .getOutputManager()
                .formatAndSaveServerStat(completedRequest, stat);

        String prettyString = ComponentUtils
                .getTranslatableComponentSerializer()
                .serialize(prettyComponent);

        return new ServerStatResult(stat, prettyComponent, prettyString);
    }
}