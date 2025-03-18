package com.artemis.the.gr8.playerstats.core.statistic;

import com.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.artemis.the.gr8.playerstats.api.StatRequest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

public final class TopStatRequest extends StatRequest<LinkedHashMap<String, Integer>> implements RequestGenerator<LinkedHashMap<String, Integer>> {

    public TopStatRequest(int topListSize) {
        this(Bukkit.getConsoleSender(), topListSize);
    }

    public TopStatRequest(CommandSender sender, int topListSize) {
        super(sender);
        super.configureForTop(topListSize);
    }

    @Override
    public boolean isValid() {
        return super.hasMatchingSubStat();
    }

    @Override
    public StatRequest<LinkedHashMap<String, Integer>> untyped(@NotNull Statistic statistic) {
        super.configureUntyped(statistic);
        return this;
    }

    @Override
    public StatRequest<LinkedHashMap<String, Integer>> blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) {
        super.configureBlockOrItemType(statistic, material);
        return this;
    }

    @Override
    public StatRequest<LinkedHashMap<String, Integer>> entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) {
        super.configureEntityType(statistic, entityType);
        return this;
    }
}