package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.statistic.RequestManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatManager;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;


import java.util.LinkedHashMap;

import static org.jetbrains.annotations.ApiStatus.Internal;

/** The implementation of the API Interface */
public final class PlayerStatsAPI implements PlayerStats {

    private static RequestManager requestManager;
    private static StatFormatter statFormatter;
    private static StatManager statManager;

    @Internal
    public PlayerStatsAPI(RequestManager request, StatManager stat, StatFormatter format) {
        requestManager = request;
        statFormatter = format;
        statManager = stat;
    }

    private TextComponent getFancyStat(CommandSender sender, String[] args) throws IllegalArgumentException {
        StatRequest request = requestManager.generateRequest(sender, args);
        if (requestManager.requestIsValid(request)) {
            switch (request.getSelection()) {
                case PLAYER -> {
                    int stat = statManager.getPlayerStat(request);
                    return statFormatter.formatPlayerStat(request, stat, true);
                }
                case SERVER -> {
                    long stat = statManager.getServerStat(request);
                    return statFormatter.formatServerStat(request, stat, true);
                }
                case TOP -> {
                    LinkedHashMap<String, Integer> stats = statManager.getTopStats(request);
                    return statFormatter.formatTopStat(request, stats, true);
                }
            }
        }
        throw new IllegalArgumentException("This is not a valid stat-request!");
    }

    @Override
    public TextComponent getPlayerStat(@NotNull Statistic statistic, @NotNull OfflinePlayer player) {
        return null;
    }

    @Override
    public TextComponent getPlayerStat(@NotNull Statistic statistic, @NotNull Material material, @NotNull OfflinePlayer player) {
        return null;
    }

    @Override
    public TextComponent getPlayerStat(@NotNull Statistic statistic, @NotNull EntityType entity, @NotNull OfflinePlayer player) {
        return null;
    }

    @Override
    public TextComponent getServerStat(@NotNull Statistic statistic) {
        return null;
    }

    @Override
    public TextComponent getServerStat(@NotNull Statistic statistic, @NotNull Material material) {
        return null;
    }

    @Override
    public TextComponent getServerStat(@NotNull Statistic statistic, @NotNull EntityType entity) {
        return null;
    }

    @Override
    public TextComponent getTopStats(@NotNull Statistic statistic) {
        return null;
    }

    @Override
    public TextComponent getTopStats(@NotNull Statistic statistic, @NotNull Material material) {
        return null;
    }

    @Override
    public TextComponent getTopStats(@NotNull Statistic statistic, @NotNull EntityType entity) {
        return null;
    }

    @Override
    public String statResultComponentToString(TextComponent component) {
        return statFormatter.statResultToString(component);
    }
}