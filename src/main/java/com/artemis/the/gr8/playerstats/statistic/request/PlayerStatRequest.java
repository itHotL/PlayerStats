package com.artemis.the.gr8.playerstats.statistic.request;

import com.artemis.the.gr8.playerstats.Main;
import com.artemis.the.gr8.playerstats.statistic.result.PlayerStatResult;
import com.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.artemis.the.gr8.playerstats.msg.components.ComponentUtils;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public final class PlayerStatRequest extends StatRequest<Integer> implements RequestGenerator<Integer> {

    public PlayerStatRequest(String playerName) {
        this(Bukkit.getConsoleSender(), playerName);
    }

    public PlayerStatRequest(CommandSender requester, String playerName) {
        super(requester);
        super.settings.configureForPlayer(playerName);
    }

    @Override
    public StatRequest<Integer> untyped(@NotNull Statistic statistic) {
        return super.configureUntyped(statistic);
    }

    @Override
    public StatRequest<Integer> blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) {
        return super.configureBlockOrItemType(statistic, material);
    }

    @Override
    public StatRequest<Integer> entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) {
        return super.configureEntityType(statistic, entityType);
    }

    @Override
    public PlayerStatResult execute() {
        return getStatResult();
    }

    private PlayerStatResult getStatResult() {
        int stat = Main
                .getStatCalculator()
                .getPlayerStat(settings);

        TextComponent prettyComponent = Main
                .getOutputManager()
                .formatAndSavePlayerStat(settings, stat);

        String prettyString = ComponentUtils
                .getTranslatableComponentSerializer()
                .serialize(prettyComponent);

        return new PlayerStatResult(stat, prettyComponent, prettyString);
    }
}