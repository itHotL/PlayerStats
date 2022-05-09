package com.gmail.artemis.the.gr8.playerstats.utils;

import com.gmail.artemis.the.gr8.playerstats.ConfigHandler;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class OutputFormatter {

    //keys for the HashMap are:
        //playerNames(Ranked)
        //statNames(Ranked)
        //subStatNames(Ranked)
        //numbers(Ranked)

    private final HashMap<String, ChatColor> chatColors;

    public OutputFormatter(ConfigHandler c) {
        chatColors = c.getChatColors();
    }

    public String formatTopStats(LinkedHashMap<String, Integer> topStats) {
        return "";
    }

    public String formatPlayerStat(String playerName, String statName, int stat) {
        return formatPlayerStat(playerName, statName, null, stat);
    }

    public String formatPlayerStat(String playerName, String statName, String subStatEntryName, int stat) {
        String subStat = subStatEntryName != null ?
                chatColors.get("subStatNames") + " (" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";

        return chatColors.get("playerNames") + playerName + chatColors.get("numbers") + ": " + stat + " " +
                chatColors.get("statNames") + statName.toLowerCase().replace("_", " ") + subStat;
    }
}
