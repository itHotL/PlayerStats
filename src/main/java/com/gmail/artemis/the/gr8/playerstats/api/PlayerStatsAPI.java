package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatManager;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;


import java.util.LinkedHashMap;

import static org.jetbrains.annotations.ApiStatus.Internal;

/** The implementation of the API Interface */
public final class PlayerStatsAPI implements PlayerStats {

    private static StatFormatter statFormatter;
    private static StatManager statManager;

    @Internal
    private PlayerStatsAPI(StatManager stat, StatFormatter format) {
        statFormatter = format;
        statManager = stat;
    }

    @Internal
    public static PlayerStatsAPI load(StatManager statManager, StatFormatter statFormatter) {
        return new PlayerStatsAPI(statManager, statFormatter);
    }

    @Override
    public TextComponent getFancyStat(CommandSender sender, String[] args) throws IllegalArgumentException {
        StatRequest request = statManager.generateRequest(sender, args);
        if (statManager.requestIsValid(request)) {
            switch (request.getSelection()) {
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

    public String componentToString(TextComponent component) {
        return statFormatter.toString(component);
    }
}