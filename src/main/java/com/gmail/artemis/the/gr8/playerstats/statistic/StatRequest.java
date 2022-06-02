package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class StatRequest {

    private final CommandSender sender;
    private String statName;
    private String subStatEntry;
    private String playerName;
    private boolean playerFlag;
    private boolean topFlag;

    private Statistic statEnum;
    private Statistic.Type statType;
    private EntityType entity;
    private Material block;
    private Material item;

    //playerFlag and topFlag are false by default, will be set to true if "player" or "top" is in the args
    public StatRequest(@NotNull CommandSender s) {
        sender = s;
        playerFlag = false;
        topFlag = false;
    }

    //sets the statName, and automatically tries to set the correct statType and get the corresponding item/block/entity if there is a subStatEntry
    public void setStatName(String statName) {
        this.statName = statName;
        if (statName != null) {
            setStatEnumAndType();
            if (subStatEntry != null) {
                extractSubStat();
            }
        }
    }

    private void setStatEnumAndType() {
        try {
            statEnum = EnumHandler.getStatEnum(statName);
            statType = statEnum.getType();
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning(e.toString());
        }
    }


    //sets the subStatEntry, and automatically tries to get the corresponding item/block/entity if there is a valid statType present
    //if the subStatEntry is set to null, any present item/block/entity is set to null again
    public void setSubStatEntry(String subStatEntry) {
        this.subStatEntry = subStatEntry;
        if (subStatEntry != null && statType != null) {
            extractSubStat();
        }
        else if (subStatEntry == null) {
            entity = null;
            item = null;
            block = null;
        }
    }

    private void extractSubStat() {
        switch (statType) {
            case ENTITY -> {
                try {
                    entity = EnumHandler.getEntityType(subStatEntry);
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning(e.toString());
                }
            }
            case ITEM -> {
                try {
                    item = EnumHandler.getItem(subStatEntry);
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning(e.toString());
                }
            }
            case BLOCK -> {
                try {
                    block = EnumHandler.getBlock(subStatEntry);
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning(e.toString());
                }
            }
        }
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    //the "player" arg is a special case, because it could either be a valid subStatEntry, or indicate that the lookup action should target a specific player
    //this is why the playerFlag exists - if this is true, and playerName is null, subStatEntry should be set to "player"
    public void setPlayerFlag(boolean playerFlag) {
        this.playerFlag = playerFlag;
    }

    public void setTopFlag(boolean topFlag) {
        this.topFlag = topFlag;
    }

    public CommandSender getCommandSender() {
        return sender;
    }

    public String getStatName() {
        return statName;
    }

    //returns the type of the stored statistic, or null if no statName has been set
    public Statistic.Type getStatType() {
        return statType;
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

    public boolean playerFlag() {
        return playerFlag;
    }

    public boolean topFlag() {
        return topFlag;
    }

}
