package com.gmail.artemis.the.gr8.playerstats.models;

import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

/** The Object PlayerStats uses to calculate the appropriate statistic.
 It is generated from the args provided by a CommandSender when /stat is called,
 and always contains this CommandSender. By default, {@link #getSelection()}
 will return {@link Target#TOP}, unless another selection is specified in the args.*/
public final class StatRequest {

    private final CommandSender sender;
    private Statistic statistic;
    private String playerName;
    private Target selection;

    private String subStatEntry;
    private EntityType entity;
    private Material block;
    private Material item;
    private boolean playerFlag;

    //make a StatRequest for a given CommandSender with some default values
    public StatRequest(@NotNull CommandSender s) {
        sender = s;
        selection = Target.TOP;
        playerFlag = false;
    }

    public @NotNull CommandSender getCommandSender() {
        return sender;
    }

    public boolean isConsoleSender() {
        return sender instanceof ConsoleCommandSender;
    }

    public void setStatistic(Statistic statistic) {
        this.statistic = statistic;
    }

    /** Returns the set enum constant Statistic, or null if none was set. */
    public Statistic getStatistic() {
        return statistic;
    }

    /** Sets the subStatEntry, and automatically tries to get the corresponding item/block/entity if there is a valid statType present.
    If the subStatEntry is set to null, any present item/block/entity is set to null again. */
    public void setSubStatEntry(String subStatEntry) {
        this.subStatEntry = subStatEntry;
    }

    public String getSubStatEntry() {
        return subStatEntry;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    /** False by default, set to true if args[] contains "player". */
    public void setPlayerFlag(boolean playerFlag) {
        this.playerFlag = playerFlag;
    }

    /** The "player" arg is a special case, because it could either be a valid subStatEntry, or indicate that the lookup action should target a specific player.
     This is why the playerFlag exists - if this is true, and playerName is null, subStatEntry should be set to "player". */
    public boolean playerFlag() {
        return playerFlag;
    }

    public void setSelection(Target selection) {
        this.selection = selection;
    }

    public @NotNull Target getSelection() {
        return selection;
    }

    public void setEntity(EntityType entity) {
        this.entity = entity;
    }

    public EntityType getEntity() {
        return entity;
    }

    public void setBlock(Material block) {
        this.block = block;
    }

    public Material getBlock() {
        return block;
    }

    public void setItem(Material item) {
        this.item = item;
    }

    public Material getItem() {
        return item;
    }
}