package com.github.artemis.the.gr8.playerstats.api;

import com.github.artemis.the.gr8.playerstats.statistic.StatCalculator;
import com.github.artemis.the.gr8.playerstats.statistic.request.StatRequest;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

/** Is responsible for creating an executable {@link StatRequest}. This Request holds all
 the information PlayerStats needs to work with, and is used by the {@link StatCalculator}
 to get the desired statistic data.*/
public interface RequestGenerator<T> {

    /** Gets an executable Request object for a Statistic of Statistic.Type {@code Untyped}.

     @param statistic a Statistic of Type.Untyped
     @return a {@link StatRequest}
     @throws IllegalArgumentException if <code>statistic</code> is not of Type.Untyped*/
    StatRequest<T> untyped(@NotNull Statistic statistic) throws IllegalArgumentException;

    /** Gets an executable Request object for a Statistic of Statistic.Type Block or Item.

     @param statistic a Statistic of Type.Block or Type.Item
     @param material a block if the <code>statistic</code> is of Type.Block,
     and an item if the <code>statistic</code> is of Type.Item
     @return a {@link StatRequest}
     @throws IllegalArgumentException if <code>statistic</code> is not of Type.Block
     (with a block as <code>material</code>), or <code>statistic</code> is not of Type.Item
     (with an item as <code>material</code>) */
    StatRequest<T> blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException;

    /** Gets an executable Request object for a Statistic of Statistic.Type Entity.

     @param statistic a Statistic of Type.Entity
     @param entityType an EntityType
     @return a {@link StatRequest}
     @throws IllegalArgumentException if <code>statistic</code> is not of Type.Entity*/
    StatRequest<T> entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException;
}