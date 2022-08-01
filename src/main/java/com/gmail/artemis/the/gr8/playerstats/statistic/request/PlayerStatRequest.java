package com.gmail.artemis.the.gr8.playerstats.statistic.request;

import com.gmail.artemis.the.gr8.playerstats.api.PlayerStats;
import com.gmail.artemis.the.gr8.playerstats.api.RequestExecutor;
import com.gmail.artemis.the.gr8.playerstats.statistic.result.StatResult;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

public class PlayerStatRequest implements RequestExecutor<Integer> {

    private final StatRequest statRequest;

    public PlayerStatRequest(StatRequest request) {
        statRequest = request;
    }

    @Override
    public StatResult<Integer> untyped(Statistic statistic) {

        return null;
    }

    @Override
    public StatResult<Integer> blockOrItemType(Statistic statistic, Material material) {
        return null;
    }

    @Override
    public StatResult<Integer> entityType(Statistic statistic, EntityType entityType) {
        return null;
    }
}