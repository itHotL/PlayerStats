package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.models.*;
import com.gmail.artemis.the.gr8.playerstats.statistic.RequestManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatManager;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

import java.util.LinkedHashMap;

import static org.jetbrains.annotations.ApiStatus.Internal;

/** The implementation of the API Interface */
public final class PlayerStatsAPI implements PlayerStats {

    private static RequestGenerator requestGenerator;
    private static StatCalculator statCalculator;
    private static StatFormatter statFormatter;

    @Internal
    public PlayerStatsAPI(RequestManager request, StatManager stat, StatFormatter format) {
        PlayerStatsAPI.requestGenerator = request;
        statCalculator = stat;
        statFormatter = format;
    }

    @Override
    public StatResult<Integer> getPlayerStat(String playerName, Statistic statistic, Material material, EntityType entity) {
       StatRequest request = getStatRequest(Target.PLAYER, statistic, material, entity, playerName);
       int stat = statCalculator.getPlayerStat(request);
       TextComponent prettyStat = statFormatter.formatPlayerStat(request, stat);
       return new PlayerStatResult(stat, prettyStat);
    }

    @Override
    public StatResult<?> getServerStat(Statistic statistic, Material material, EntityType entity) {
        StatRequest request = getStatRequest(Target.SERVER, statistic, material, entity, null);
        long stat = statCalculator.getServerStat(request);
        TextComponent prettyStat = statFormatter.formatServerStat(request, stat);
        return new ServerStatResult(stat, prettyStat);
    }

    @Override
    public StatResult<?> getTopStats(Statistic statistic, Material material, EntityType entity) {
        StatRequest request = getStatRequest(Target.TOP, statistic, material, entity, null);
        LinkedHashMap<String, Integer> stat = statCalculator.getTopStats(request);
        TextComponent prettyStat = statFormatter.formatTopStat(request, stat);
        return new TopStatResult(stat, prettyStat);
    }

    private StatRequest getStatRequest(Target target, Statistic statistic, Material material, EntityType entity, String playerName) throws NullPointerException {
        StatRequest request = requestGenerator.generateAPIRequest(target, statistic, material, entity, playerName);
        if (requestGenerator.validateAPIRequest(request)) {
            return request;
        }
        throw new NullPointerException("The parameters you supplied did not result in a valid stat-request!");
    }
}