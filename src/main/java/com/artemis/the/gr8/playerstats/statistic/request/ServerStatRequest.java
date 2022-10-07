package com.artemis.the.gr8.playerstats.statistic.request;

import com.artemis.the.gr8.playerstats.Main;
import com.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.artemis.the.gr8.playerstats.msg.components.ComponentUtils;
import com.artemis.the.gr8.playerstats.statistic.result.StatResult;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public final class ServerStatRequest extends StatRequest<Long> implements RequestGenerator<Long> {

    public ServerStatRequest() {
        this(Bukkit.getConsoleSender());
    }

    public ServerStatRequest(CommandSender requester) {
        super(requester);
        super.settings.configureForServer();
    }

    @Override
    public StatRequest<Long> untyped(@NotNull Statistic statistic) {
        return super.configureUntyped(statistic);
    }

    @Override
    public StatRequest<Long> blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) {
        return super.configureBlockOrItemType(statistic, material);
    }

    @Override
    public StatRequest<Long> entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) {
        return super.configureEntityType(statistic, entityType);
    }

    @Override
    public @NotNull StatResult<Long> execute() {
        long stat = Main
                .getStatCalculator()
                .getServerStat(settings);

        TextComponent prettyComponent = Main
                .getOutputManager()
                .formatAndSaveServerStat(settings, stat);

        String prettyString = ComponentUtils
                .getTranslatableComponentSerializer()
                .serialize(prettyComponent);

        return new StatResult<>(stat, prettyComponent, prettyString);
    }
}