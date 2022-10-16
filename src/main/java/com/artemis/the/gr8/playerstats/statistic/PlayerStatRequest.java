package com.artemis.the.gr8.playerstats.statistic;

import com.artemis.the.gr8.playerstats.api.RequestGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public final class PlayerStatRequest extends StatRequest<Integer> implements RequestGenerator<Integer> {

    public PlayerStatRequest(String playerName) {
        super(Bukkit.getConsoleSender());
        super.getSettings().configureForPlayer(playerName);
    }

    @Override
    public StatRequest<Integer> untyped(@NotNull Statistic statistic) {
        super.getSettings().configureUntyped(statistic);
        return this;
    }

    @Override
    public StatRequest<Integer> blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) {
        super.getSettings().configureBlockOrItemType(statistic, material);
        return this;
    }

    @Override
    public StatRequest<Integer> entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) {
        super.getSettings().configureEntityType(statistic, entityType);
        return this;
    }
}