package com.gmail.artemis.the.gr8.playerstats.utils;

import com.gmail.artemis.the.gr8.playerstats.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.Main;
import org.bukkit.ChatColor;
import org.bukkit.map.MinecraftFont;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class OutputFormatter {

    //keys for the HashMap are the same as the config options:
        //player-names(-ranked)
        //stat-names OR list-title
        //sub-stat-names(-ranked)
        //stat-numbers(-ranked)
        //list-numbers

    private final ConfigHandler config;
    private final Main plugin;
    private HashMap<String, ChatColor> chatColors;
    private String pluginPrefix;
    private String className;

    public OutputFormatter(ConfigHandler c, Main p) {
        config = c;
        plugin = p;
        pluginPrefix = ChatColor.GRAY + "[" + ChatColor.GOLD + "PlayerStats" + ChatColor.GRAY + "] " + ChatColor.RESET;

        updateOutputColors();
        className = "OutputFormatter";
    }

    public String formatExceptions(String exception) {
        return pluginPrefix + exception;
    }

    public String formatPlayerStat(String playerName, String statName, String subStatEntryName, int stat) {
        String methodName = "formatPlayerStats";
        long time = System.currentTimeMillis();
        time = plugin.logTimeTaken(className, methodName, time, 39);

        String subStat = subStatEntryName != null ?
                chatColors.get("sub-stat-names") + " (" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";
        time = plugin.logTimeTaken(className, methodName, time, 43);

        String msg = chatColors.get("player-names") + playerName + chatColors.get("stat-numbers") + ": " + stat + " " +
                chatColors.get("stat-names") + statName.toLowerCase().replace("_", " ") + subStat;
        plugin.logTimeTaken(className, methodName, time, 47);
        return msg;
    }

    public String formatTopStats(LinkedHashMap<String, Integer> topStats, String statName, String subStatEntryName) {
        String subStat = subStatEntryName != null ?
                chatColors.get("sub-stat-names-ranked") + " (" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";
        String topCount = chatColors.get("list-numbers") + " " + topStats.size();
        String title = pluginPrefix + chatColors.get("list-title") + "Top" + topCount + chatColors.get("list-title") + " " +
                statName.toLowerCase().replace("_", " ") + subStat;

        int count = 0;
        final int[] longestName = {0};
        Set<String> playerNames = topStats.keySet();
        MinecraftFont font = new MinecraftFont();
        playerNames.stream().map(font::getWidth).max(Integer::compareTo).orElseThrow();

        try {
            longestName[0] = playerNames.stream().map(String::length).max(Integer::compareTo).orElseThrow();
        }
        catch (NoSuchElementException e) {
            longestName[0] = 20;
        }


        StringBuilder rankList = new StringBuilder();
        for (String playerName : playerNames) {
            count = count+1;

            String spaces = (longestName[0] - playerName.length() > 0) ? " ".repeat(longestName[0] - playerName.length()) : "";
            rankList.append("\n")
                    .append(chatColors.get("list-numbers")).append(count).append(". ")
                    .append(chatColors.get("player-names-ranked")).append(playerName).append(": ")
                    .append(spaces)
                    .append(chatColors.get("stat-numbers-ranked")).append(topStats.get(playerName).toString());
        }
        return title + rankList;
    }

    public void updateOutputColors() {
        chatColors = config.getChatColors();
    }
}
