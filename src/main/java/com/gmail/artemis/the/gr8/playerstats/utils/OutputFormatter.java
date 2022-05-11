package com.gmail.artemis.the.gr8.playerstats.utils;

import com.gmail.artemis.the.gr8.playerstats.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.Main;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class OutputFormatter {

    //keys for the HashMap are:
        //playerNames(Ranked)
        //statNames(Ranked)
        //subStatNames(Ranked)
        //numbers(Ranked)
    private final ConfigHandler config;
    private final Main plugin;
    private HashMap<String, ChatColor> chatColors;
    private String pluginPrefix;
    private String className;

    public OutputFormatter(ConfigHandler c, Main p) {
        config = c;
        plugin = p;
        pluginPrefix = ChatColor.GRAY + "[" + ChatColor.GOLD + "PlayerStats" + ChatColor.GRAY + "]" + ChatColor.RESET;

        updateOutputColors();
        className = "OutputFormatter";
    }

    public String formatExceptions(String exception) {
        return pluginPrefix + " " + exception;
    }

    public String formatPlayerStat(String playerName, String statName, String subStatEntryName, int stat) {
        String methodName = "formatPlayerStats";
        long time = System.currentTimeMillis();
        time = plugin.logTimeTaken(className, methodName, time, 39);

        String subStat = subStatEntryName != null ?
                chatColors.get("subStatNames") + " (" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";
        time = plugin.logTimeTaken(className, methodName, time, 43);

        String msg = chatColors.get("playerNames") + playerName + chatColors.get("numbers") + ": " + stat + " " +
                chatColors.get("statNames") + statName.toLowerCase().replace("_", " ") + subStat;
        time = plugin.logTimeTaken(className, methodName, time, 47);
        return msg;
    }

    public String formatTopStats(LinkedHashMap<String, Integer> topStats) {
        return "";
    }

    public void updateOutputColors() {
        chatColors = config.getChatColors();
    }
}
