package com.gmail.artemis.the.gr8.playerstats.statistic.request;

import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.statistic.result.StatResult;
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

  public Statistic getStatistic() {
    return requestSettings.getStatistic();
  }

  public @Nullable Material getBlock() {
    return requestSettings.getBlock();
  }

  public @Nullable Material getItem() {
    return requestSettings.getItem();
  }

  public @Nullable EntityType getEntity() {
    return requestSettings.getEntity();
  }

  public Target getTarget() {
    return requestSettings.getTarget();
  }
}