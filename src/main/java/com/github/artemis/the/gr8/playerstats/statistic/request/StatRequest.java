package com.github.artemis.the.gr8.playerstats.statistic.request;

import com.github.artemis.the.gr8.playerstats.statistic.result.StatResult;
import com.github.artemis.the.gr8.playerstats.enums.Target;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public abstract class StatRequest<T> {

  protected final RequestSettings requestSettings;

  protected StatRequest(RequestSettings request) {
    requestSettings = request;
  }

  /** Don't call this from the Main Thread!*/
  public abstract StatResult<T> execute();

  public Statistic getStatisticSetting() {
    return requestSettings.getStatistic();
  }

  public @Nullable Material getBlockSetting() {
    return requestSettings.getBlock();
  }

  public @Nullable Material getItemSetting() {
    return requestSettings.getItem();
  }

  public @Nullable EntityType getEntitySetting() {
    return requestSettings.getEntity();
  }

  public Target getTargetSetting() {
    return requestSettings.getTarget();
  }
}