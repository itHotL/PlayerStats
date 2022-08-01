package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.statistic.result.StatResult;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface RequestExecutor<T> {

  @Internal
  default StatCalculator getStatCalculator() {
    return PlayerStatsAPI.statCalculator();
  }

  @Internal
  default StatFormatter getStatFormatter() {
    return PlayerStatsAPI.statFormatter();
  }

  StatResult<T> untyped(Statistic statistic);

  StatResult<T> blockOrItemType(Statistic statistic, Material material);

  StatResult<T> entityType(Statistic statistic, EntityType entityType);
}
