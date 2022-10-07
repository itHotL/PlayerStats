package com.artemis.the.gr8.playerstats.statistic.request;

import com.artemis.the.gr8.playerstats.Main;
import com.artemis.the.gr8.playerstats.statistic.result.StatResult;
import com.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.artemis.the.gr8.playerstats.msg.components.ComponentUtils;
import net.kyori.adventure.text.TextComponent;
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

    public TopStatRequest(CommandSender requester, int topListSize) {
        super(requester);
        super.settings.configureForTop(topListSize);
    }

    @Override
    public StatRequest<LinkedHashMap<String, Integer>> untyped(@NotNull Statistic statistic) {
        return super.configureUntyped(statistic);
    }

    @Override
    public StatRequest<LinkedHashMap<String, Integer>> blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) {
        return super.configureBlockOrItemType(statistic, material);
    }

    @Override
    public StatRequest<LinkedHashMap<String, Integer>> entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) {
        return super.configureEntityType(statistic, entityType);
    }

    @Override
    public @NotNull StatResult<LinkedHashMap<String, Integer>> execute() {
        LinkedHashMap<String, Integer> stat = Main
                .getStatCalculator()
                .getTopStats(settings);

        TextComponent prettyComponent = Main
                .getOutputManager()
                .formatAndSaveTopStat(settings, stat);

        String prettyString = ComponentUtils
                .getTranslatableComponentSerializer()
                .serialize(prettyComponent);

        return new StatResult<>(stat, prettyComponent, prettyString);
    }
}