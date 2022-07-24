package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.statistic.RequestManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatManager;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.LinkedHashMap;

import static org.jetbrains.annotations.ApiStatus.Internal;

/** The implementation of the API Interface */
public final class PlayerStatsAPI implements PlayerStats {

    private static RequestManager requestManager;
    private static StatManager statManager;
    private static StatFormatter statFormatter;

    @Internal
    public PlayerStatsAPI(RequestManager request, StatManager stat, StatFormatter format) {
        requestManager = request;
        statManager = stat;
        statFormatter = format;
    }

    @Override
    public TextComponent getPlayerStat(@NotNull Statistic statistic, @NotNull String playerName) throws IllegalArgumentException {
        return getFormattedStatistic(Target.PLAYER, statistic, null, null, playerName);
    }

    @Override
    public TextComponent getPlayerStat(@NotNull Statistic statistic, @NotNull Material material, @NotNull String playerName) throws IllegalArgumentException {
        return getFormattedStatistic(Target.PLAYER, statistic, material, null, playerName);
    }

    @Override
    public TextComponent getPlayerStat(@NotNull Statistic statistic, @NotNull EntityType entity, @NotNull String playerName) throws IllegalArgumentException {
        return getFormattedStatistic(Target.PLAYER, statistic, null, entity, playerName);
    }

    @Override
    public TextComponent getServerStat(@NotNull Statistic statistic) throws IllegalArgumentException {
        return getFormattedStatistic(Target.SERVER, statistic, null, null, null);
    }

    @Override
    public TextComponent getServerStat(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        return getFormattedStatistic(Target.SERVER, statistic, material, null, null);
    }

    @Override
    public TextComponent getServerStat(@NotNull Statistic statistic, @NotNull EntityType entity) throws IllegalArgumentException {
        return getFormattedStatistic(Target.SERVER, statistic, null, entity, null);
    }

    @Override
    public TextComponent getTopStats(@NotNull Statistic statistic) throws IllegalArgumentException {
        return getFormattedStatistic(Target.TOP, statistic, null, null, null);
    }

    @Override
    public TextComponent getTopStats(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        return getFormattedStatistic(Target.TOP, statistic, material, null, null);
    }

    @Override
    public TextComponent getTopStats(@NotNull Statistic statistic, @NotNull EntityType entity) throws IllegalArgumentException {
        return getFormattedStatistic(Target.TOP, statistic, null, entity, null);
    }

    private TextComponent getFormattedStatistic(@NotNull Target selection, @NotNull Statistic statistic,
                                                @Nullable Material material, @Nullable EntityType entity, @Nullable String playerName) throws IllegalArgumentException {
        StatRequest request = requestManager.generateRequest(selection, statistic, material, entity, playerName);
        if (requestManager.validateAPIRequest(request)) {
            switch (selection) {
                case PLAYER -> {
                    int stat = statManager.getPlayerStat(request);
                    return statFormatter.formatPlayerStat(request, stat);
                }
                case SERVER -> {
                    long stat = statManager.getServerStat(request);
                    return statFormatter.formatServerStat(request, stat);
                }
                case TOP -> {
                    LinkedHashMap<String, Integer> stats = statManager.getTopStats(request);
                    return statFormatter.formatTopStat(request, stats);
                }
            }
        }
        throw new IllegalArgumentException("This is not a valid stat-request!");
    }
}