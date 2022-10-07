package com.artemis.the.gr8.playerstats.statistic.request;

import com.artemis.the.gr8.playerstats.Main;
import com.artemis.the.gr8.playerstats.api.PlayerStats;
import com.artemis.the.gr8.playerstats.statistic.result.StatResult;
import com.artemis.the.gr8.playerstats.enums.Target;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Holds all the information PlayerStats needs to perform
 * a lookup, and can be executed to get the results. Calling
 * {@link #execute()} on a Top- or ServerRequest can take some
 * time (especially if there is a substantial amount of
 * OfflinePlayers on this particular server), so I strongly
 * advice you to call this asynchronously!
 */
public abstract class StatRequest<T> {

  protected final Settings settings;

  protected StatRequest(CommandSender requester) {
    settings = new Settings(requester);
  }

  protected StatRequest<T> configureUntyped(@NotNull Statistic statistic) {
    if (statistic.getType() == Statistic.Type.UNTYPED) {
      settings.setStatistic(statistic);
      return this;
    }
    throw new IllegalArgumentException("This statistic is not of Type.Untyped");
  }

  protected StatRequest<T> configureBlockOrItemType(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
    Statistic.Type type = statistic.getType();
    if (type == Statistic.Type.BLOCK && material.isBlock()) {
      settings.setBlock(material);
    }
    else if (type == Statistic.Type.ITEM && material.isItem()){
      settings.setItem(material);
    }
    else {
      throw new IllegalArgumentException("Either this statistic is not of Type.Block or Type.Item, or no valid block or item has been provided");
    }
    settings.setStatistic(statistic);
    settings.setSubStatEntryName(material.toString());
    return this;
  }

  protected StatRequest<T> configureEntityType(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
    if (statistic.getType() == Statistic.Type.ENTITY) {
      settings.setStatistic(statistic);
      settings.setSubStatEntryName(entityType.toString());
      settings.setEntity(entityType);
      return this;
    }
    throw new IllegalArgumentException("This statistic is not of Type.Entity");
  }

  /**
   * Executes this StatRequest. For a Top- or ServerRequest, this can
   * take some time!
   *
   * @return a StatResult containing the value of this lookup, both as
   * numerical value and as formatted message
   * @see PlayerStats
   * @see StatResult
   */
  public abstract StatResult<T> execute();

  /**
   * Use this method to view the settings that have
   * been configured for this StatRequest.
   */
  public Settings getSettings() {
    return settings;
  }

  public boolean isValid() {
    if (settings.statistic == null) {
      return false;
    } else if (settings.target == Target.PLAYER && settings.playerName == null) {
      return false;
    } else if (settings.statistic.getType() != Statistic.Type.UNTYPED &&
            settings.subStatEntryName == null) {
      return false;
    } else {
      return hasMatchingSubStat();
    }
  }

  private boolean hasMatchingSubStat() {
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

    void configureForPlayer(String playerName) {
        this.target = Target.PLAYER;
        this.playerName = playerName;
    }

    void configureForServer() {
      this.target = Target.SERVER;
    }

    void configureForTop() {
        configureForTop(Main.getConfigHandler().getTopListMaxSize());
    }

    void configureForTop(int topListSize) {
        this.target = Target.TOP;
        this.topListSize = topListSize != 0 ?
                topListSize :
                Main.getConfigHandler().getTopListMaxSize();
    }

    public @NotNull CommandSender getCommandSender() {
      return sender;
    }

    public boolean isConsoleSender() {
      return sender instanceof ConsoleCommandSender;
    }

    void setStatistic(Statistic statistic) {
      this.statistic = statistic;
    }

    public Statistic getStatistic() {
      return statistic;
    }

    private void setSubStatEntryName(String subStatEntry) {
      this.subStatEntryName = subStatEntry;
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

    void setEntity(EntityType entity) {
      this.entity = entity;
    }

    public EntityType getEntity() {
      return entity;
    }

    void setBlock(Material block) {
      this.block = block;
    }

    public Material getBlock() {
      return block;
    }

    void setItem(Material item) {
      this.item = item;
    }

    public Material getItem() {
      return item;
    }
  }
}