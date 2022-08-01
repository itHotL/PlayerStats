package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.statistic.request.StatRequest;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

/** Turns user input into a valid {@link StatRequest}. This StatRequest should hold all
 the information PlayerStats needs to work with, and is used by the {@link StatCalculator}
 to get the desired statistic data.*/
public interface RequestGenerator {

    StatRequest untyped(Statistic statistic);

    StatRequest blockOrItemType(Statistic statistic, Material material);

    StatRequest entityType(Statistic statistic, EntityType entityType);
}