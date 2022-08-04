package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.statistic.result.StatResult;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.StatRequest;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

/** Completes a basic {@link StatRequest} provided by the {@link PlayerStatsAPI}
 and performs a statistic lookup with the information that is stored inside this StatRequest.*/
public interface RequestExecutor<T> {

  @Internal
  default StatCalculator getStatCalculator() {
    return PlayerStatsAPI.statCalculator();
  }

  @Internal
  default StatFormatter getStatFormatter() {
    return PlayerStatsAPI.statFormatter();
  }

  /** Gets a StatResult for a Statistic of Statistic.Type {@code Untyped}.

   @param statistic a Statistic of Type.Untyped
   @return a {@link StatResult}
   @throws IllegalArgumentException if <code>statistic</code> is not of Type.Untyped*/
  StatResult<T> untyped(@NotNull Statistic statistic) throws IllegalArgumentException;

  /** Gets a StatResult for a Statistic of Statistic.Type Block or Item.

   @param statistic a Statistic of Type.Block or Type.Item
   @param material a block if the <code>statistic</code> is of Type.Block,
   and an item if the <code>statistic</code> is of Type.Item
   @throws IllegalArgumentException if <code>statistic</code> is not of Type.Block
  (with a block as <code>material</code>), or <code>statistic</code> is not of Type.Item
  (with an item as <code>material</code>)
   @return a {@link StatResult} */
  StatResult<T> blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException;

  /** Gets a StatResult for a Statistic of Statistic.Type Entity.

   @param statistic a Statistic of Type.Entity
   @param entityType an EntityType
   @throws IllegalArgumentException if <code>statistic</code> is not of Type.Entity,
   or <code>entityType</code> is not a valid EntityType
   @return a {@link StatResult} */
  StatResult<T> entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException;
}