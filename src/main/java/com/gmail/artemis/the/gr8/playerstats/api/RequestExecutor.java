package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.statistic.result.StatResult;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

public interface RequestExecutor<T> {

  @Internal
  default StatCalculator getStatCalculator() {
    return PlayerStatsAPI.statCalculator();
  }

  @Internal
  default StatFormatter getStatFormatter() {
    return PlayerStatsAPI.statFormatter();
  }

  StatResult<T> untyped(@NotNull Statistic statistic);

  /** @throws IllegalArgumentException if <code>statistic</code> is not of Type.Block
  (with a block as <code>material</code>), or <code>statistic</code> is not of Type.Item
  (with an item as <code>material</code>)
   @return a {@link StatResult} */
  StatResult<T> blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException;

  StatResult<T> entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException;
}
