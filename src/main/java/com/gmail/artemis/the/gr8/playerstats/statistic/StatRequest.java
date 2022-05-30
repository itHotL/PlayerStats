package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StatRequest {

    private final CommandSender sender;
    private String statName;
    private Statistic.Type statType;
    private String subStatEntry;
    private String playerName;
    private boolean playerFlag;
    private boolean topFlag;

    //playerFlag and topFlag are false by default, will be set to true if "player" or "top" is in the args
    public StatRequest(@NotNull CommandSender s) {
        sender = s;
        playerFlag = false;
        topFlag = false;
    }

    public void setStatName(String statName) {
        this.statName = statName;
        setStatType(statName);
    }

    private void setStatType(String statName) {
        if (statName != null) {
            try {
                statType = EnumHandler.getStatType(statName);
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSubStatEntry(String subStatEntry) {
        this.subStatEntry = subStatEntry;
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

    public String getSubStatEntry() {
        return subStatEntry;
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
