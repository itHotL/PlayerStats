package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

public interface APIRequest {

    StatRequest untyped(Statistic statistic);

    StatRequest blockOrItemType(Statistic statistic, Material material);

    StatRequest entityType(Statistic statistic, EntityType entityType);
}
