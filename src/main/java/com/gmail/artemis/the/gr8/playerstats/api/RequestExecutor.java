package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.statistic.result.StatResult;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

public interface RequestExecutor<T> {

  StatResult<T> untyped(Statistic statistic);

  StatResult<T> blockOrItemType(Statistic statistic, Material material);

  StatResult<T> entityType(Statistic statistic, EntityType entityType);
}
