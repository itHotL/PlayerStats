package com.artemis.the.gr8.playerstats.api;

import com.artemis.the.gr8.playerstats.api.enums.Target;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Holds all the information PlayerStats needs to perform
 * a lookup, and can be executed by the {@link StatManager}
 * to get the results.
 */
public abstract class StatRequest<T> {

  private final Settings settings;

  protected StatRequest(CommandSender requester) {
    settings = new Settings(requester);
  }

  public abstract boolean isValid();

  /**
   * Use this method to view the settings that have
   * been configured for this StatRequest.
   */
  public Settings getSettings() {
    return settings;
  }

  protected void configureForPlayer(String playerName) {
    this.settings.target = Target.PLAYER;
    this.settings.playerName = playerName;
  }

  protected void configureForServer() {
    this.settings.target = Target.SERVER;
  }

  protected void configureForTop(int topListSize) {
    this.settings.target = Target.TOP;
    this.settings.topListSize = topListSize;
  }

  protected void configureUntyped(@NotNull Statistic statistic) {
    if (statistic.getType() != Statistic.Type.UNTYPED) {
      throw new IllegalArgumentException("This statistic is not of Type.Untyped");
    }
    this.settings.statistic = statistic;
  }

  protected void configureBlockOrItemType(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
    Statistic.Type type = statistic.getType();
    if (type == Statistic.Type.BLOCK && material.isBlock()) {
      this.settings.block = material;
    }
    else if (type == Statistic.Type.ITEM && material.isItem()){
      this.settings.item = material;
    }
    else {
      throw new IllegalArgumentException("Either this statistic is not of Type.Block or Type.Item, or no valid block or item has been provided");
    }
    this.settings.statistic = statistic;
    this.settings.subStatEntryName = material.toString();
  }

  protected void configureEntityType(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
    if (statistic.getType() != Statistic.Type.ENTITY) {
      throw new IllegalArgumentException("This statistic is not of Type.Entity");
    }
    this.settings.statistic = statistic;
    this.settings.entity = entityType;
    this.settings.subStatEntryName = entityType.toString();
  }

  protected boolean hasMatchingSubStat() {
    if (settings.statistic == null) {
      return false;
    }

    switch (settings.statistic.getType()) {
      case BLOCK -> {
        return settings.block != null;
      }
      case ENTITY -> {
        return settings.entity != null;
      }
      case ITEM -> {
        return settings.item != null;
      }
      default -> {
        return true;
      }
    }
  }


  public static final class Settings {
    private final CommandSender sender;
    private Statistic statistic;
    private String playerName;
    private Target target;
    private int topListSize;

    private String subStatEntryName;
    private EntityType entity;
    private Material block;
    private Material item;

    /**
     * @param sender the CommandSender who prompted this RequestGenerator
     */
    private Settings(@NotNull CommandSender sender) {
        this.sender = sender;
    }

    public @NotNull CommandSender getCommandSender() {
      return sender;
    }

    public boolean isConsoleSender() {
      return sender instanceof ConsoleCommandSender;
    }

    public Statistic getStatistic() {
      return statistic;
    }

    public @Nullable String getSubStatEntryName() {
      return subStatEntryName;
    }

    public String getPlayerName() {
      return playerName;
    }

    public @NotNull Target getTarget() {
      return target;
    }

    public int getTopListSize() {
      return this.topListSize;
    }

    public EntityType getEntity() {
      return entity;
    }

    public Material getBlock() {
      return block;
    }

    public Material getItem() {
      return item;
    }
  }
}