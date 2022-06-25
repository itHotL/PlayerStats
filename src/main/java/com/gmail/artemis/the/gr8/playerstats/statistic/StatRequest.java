package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.enums.Query;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StatRequest {

    private final CommandSender sender;
    private String statName;
    private String subStatEntry;
    private String playerName;
    private Query selection;
    private boolean playerFlag;

    private Statistic statEnum;
    private EntityType entity;
    private Material block;
    private Material item;

    //playerFlag is set to false by default, will be set to true if "player" is in the args
    public StatRequest(@NotNull CommandSender s) {
        sender = s;
        playerFlag = false;
    }

    /** Sets the statName, and automatically tries to set the correct statType and get the corresponding item/block/entity if there is a subStatEntry. */
    public void setStatName(String statName) {
        this.statName = statName;
        if (statName != null) {
            setStatEnum();
            if (subStatEntry != null) {
                extractSubStat();
            }
        }
    }

    private void setStatEnum() throws IllegalArgumentException {
        try {
            statEnum = EnumHandler.getStatEnum(statName);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning(e.toString());
        }
    }

    /** Sets the subStatEntry, and automatically tries to get the corresponding item/block/entity if there is a valid statType present.
    If the subStatEntry is set to null, any present item/block/entity is set to null again. */
    public void setSubStatEntry(String subStatEntry) {
        this.subStatEntry = subStatEntry;
        if (subStatEntry != null && statEnum != null) {
            extractSubStat();
        }
        else if (subStatEntry == null) {
            entity = null;
            item = null;
            block = null;
        }
    }

    private void extractSubStat() {
        switch (statEnum.getType()) {
            case ENTITY -> {
                try {
                    if (EnumHandler.isEntity(subStatEntry)) {
                        entity = EnumHandler.getEntityEnum(subStatEntry);
                    }
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning(e.toString());
                }
            }
            case ITEM -> {
                try {
                    if (EnumHandler.isItem(subStatEntry)) {
                        item = EnumHandler.getItemEnum(subStatEntry);
                    }
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning(e.toString());
                }
            }
            case BLOCK -> {
                try {
                    if (EnumHandler.isBlock(subStatEntry)) {
                        block = EnumHandler.getBlockEnum(subStatEntry);
                    }
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning(e.toString());
                }
            }
        }
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /** False by default, set to true if args[] contains "player". */
    public void setPlayerFlag(boolean playerFlag) {
        this.playerFlag = playerFlag;
    }

    public void setSelection(Query selection) {
        this.selection = selection;
    }

    public CommandSender getCommandSender() {
        return sender;
    }

    public String getStatName() {
        return statName;
    }

    /** Returns the type of the stored statistic, or null if no statName has been set. */
    public Statistic.Type getStatType() {
        return statEnum.getType();
    }

    public Statistic getStatEnum() {
        return statEnum;
    }

    public String getSubStatEntry() {
        return subStatEntry;
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

    public String getPlayerName() {
        return playerName;
    }

    /** The "player" arg is a special case, because it could either be a valid subStatEntry, or indicate that the lookup action should target a specific player.
     This is why the playerFlag exists - if this is true, and playerName is null, subStatEntry should be set to "player". */
    public boolean playerFlag() {
        return playerFlag;
    }

    public @Nullable Query getSelection() {
        return selection;
    }
}
