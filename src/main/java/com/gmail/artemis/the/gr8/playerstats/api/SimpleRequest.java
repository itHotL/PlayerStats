package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

public interface SimpleRequest {

    void setStatistic(Statistic statistic);

    Statistic getStatistic();

    void setSubStatEntry(String subStatEntry);

    String getSubStatEntry();

    void setPlayerName(String playerName);

    String getPlayerName();

    void setSelection(Target selection);

    Target getSelection();

    void setEntity(EntityType entity);

    EntityType getEntity();

    void setBlock(Material material);

    Material getBlock();

    void setItem(Material item);

    Material getItem();
}