package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class PlayerStatRequest extends PlayerRequest {

    public PlayerStatRequest(String playerName) {
        super(playerName);
    }

    @Override
    public StatRequest untyped(Statistic statistic) {
        StatRequest request = getBasePlayerRequest();
        request.setStatistic(statistic);
        return request;
    }

    @Override
    public StatRequest blockOrItemType(@NotNull Statistic statistic, Material material) {
        StatRequest request = getBasePlayerRequest(statistic);
        request.setSubStatEntryName(material.toString());
        if (statistic.getType() == Statistic.Type.BLOCK) {
            request.setBlock(material);
        } else {
            request.setItem(material);
        }
        return request;
    }

    @Override
    public StatRequest entityType(Statistic statistic, EntityType entityType) {
        return null;
    }

    private StatRequest getBasePlayerRequest(@NotNull Statistic statistic) {
        StatRequest request = new StatRequest(Bukkit.getConsoleSender(), true);
        request.setStatistic(statistic);
        request.setTarget(Target.PLAYER);
        request.setPlayerName(super.playerName);
        return request;
    }
}
