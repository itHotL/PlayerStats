package com.gmail.artemis.the.gr8.playerstats;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StatRequest {

    private final CommandSender sender;
    private String statName;
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

    public CommandSender getCommandSender() {
        return sender;
    }

    public String getStatName() {
        return statName;
    }

    public void setStatName(String statName) {
        this.statName = statName;
    }

    public String getSubStatEntry() {
        return subStatEntry;
    }

    public void setSubStatEntry(String subStatEntry) {
        this.subStatEntry = subStatEntry;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    //the "player" arg in the statCommand is a special case, because it could either be a valid subStatEntry, or indicate that the lookup action should target a specific player
    //this is why the playerFlag exists - if this is true, and playerName is null, subStatEntry will be "player"
    public boolean playerFlag() {
        return playerFlag;
    }

    public void setPlayerFlag(boolean playerFlag) {
        this.playerFlag = playerFlag;
    }

    public boolean topFlag() {
        return topFlag;
    }

    public void setTopFlag(boolean topFlag) {
        this.topFlag = topFlag;
    }
}
