package com.artemis.the.gr8.playerstats.statistic;

import com.artemis.the.gr8.playerstats.api.RequestGenerator;
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

    public ServerStatRequest(CommandSender sender) {
        super(sender);
        super.getSettings().configureForServer();
    }

    @Override
    public StatRequest<Long> untyped(@NotNull Statistic statistic) {
        super.getSettings().configureUntyped(statistic);
        return this;
    }

    @Override
    public StatRequest<Long> blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) {
        super.getSettings().configureBlockOrItemType(statistic, material);
        return this;
    }

    @Override
    public StatRequest<Long> entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) {
        super.getSettings().configureEntityType(statistic, entityType);
        return this;
    }
}