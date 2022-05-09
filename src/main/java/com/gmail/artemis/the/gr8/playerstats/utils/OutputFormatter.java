package com.gmail.artemis.the.gr8.playerstats.utils;

import org.bukkit.ChatColor;

import java.util.LinkedHashMap;

public class OutputFormatter {

    public static String formatTopStats(LinkedHashMap<String, Integer> topStats) {
        return "";
    }

    public static String formatPlayerStat(String playerName, String statName, int stat) {
        return formatPlayerStat(playerName, statName, null, stat);
    }

    public static String formatPlayerStat(String playerName, String statName, String subStatEntryName, int stat) {
        String subStat = subStatEntryName != null ?
                ChatColor.BLUE + " (" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";

        return ChatColor.GOLD + playerName + ChatColor.WHITE + ": " + stat + " " +
                ChatColor.AQUA + statName.toLowerCase().replace("_", " ") + subStat;
    }
}
