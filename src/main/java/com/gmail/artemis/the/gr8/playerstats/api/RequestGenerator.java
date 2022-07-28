package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.statistic.request.StatRequestCore;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.ApiStatus.Internal;

/** Turns user input into a valid {@link StatRequestCore}. This StatRequest should hold all
 the information PlayerStats needs to work with, and is used by the {@link StatCalculator}
 to get the desired statistic data.*/
@Internal
public abstract class RequestGenerator {

    protected StatRequestCore statRequest;

    protected abstract StatRequestCore generateBaseRequest();

    StatRequestCore untyped(Statistic statistic) {
        statRequest.setStatistic(statistic);
        return statRequest;
    }

    StatRequestCore blockOrItemType(Statistic statistic, Material material) {
        statRequest.setSubStatEntryName(material.toString());
        if (statistic.getType() == Statistic.Type.BLOCK) {
            statRequest.setBlock(material);
        } else {
            statRequest.setItem(material);
        }
        return statRequest;
    }

    StatRequestCore entityType(Statistic statistic, EntityType entityType) {
        return null;
    }
}
