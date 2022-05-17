package com.gmail.artemis.the.gr8.playerstats.utils;

import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import org.bukkit.ChatColor;
import org.bukkit.map.MinecraftFont;

import java.util.*;

public class OutputFormatter {

    //keys for the HashMap are the same as the config options (so e.g. player-names/player-names-ranked)

    private final ConfigHandler config;
    private HashMap<String, ChatColor> chatColors;
    private final String pluginPrefix;

    public OutputFormatter(ConfigHandler c) {
        config = c;
        pluginPrefix = ChatColor.GRAY + "[" + ChatColor.GOLD + "PlayerStats" + ChatColor.GRAY + "] " + ChatColor.RESET;
        updateOutputColors();
    }

    public String formatExceptions(String exception) {
        return pluginPrefix + exception;
    }

    public String formatPlayerStat(String playerName, String statName, String subStatEntryName, int stat) {
        String subStat = subStatEntryName != null ?
                chatColors.get("sub-stat-names") + " (" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";

        return chatColors.get("player-names") + playerName + chatColors.get("stat-numbers") + ": " + stat + " " +
                chatColors.get("stat-names") + statName.toLowerCase().replace("_", " ") + subStat;
    }

    public String formatTopStats(LinkedHashMap<String, Integer> topStats, String statName, String subStatEntryName) {
        String subStat = subStatEntryName != null ?
                chatColors.get("sub-stat-names-ranked") + " (" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";
        String topCount = chatColors.get("list-numbers") + " " + topStats.size();
        String title = "\n" + pluginPrefix + chatColors.get("list-title") + "Top" + topCount + chatColors.get("list-title") + " " +
                statName.toLowerCase().replace("_", " ") + subStat;

        boolean useDots = config.getUseDots();
        int count = 0;
        Set<String> playerNames = topStats.keySet();
        MinecraftFont font = new MinecraftFont();

        StringBuilder rankList = new StringBuilder();
        for (String playerName : playerNames) {
            count = count+1;

            rankList.append("\n")
                    .append(chatColors.get("list-numbers")).append(count).append(". ")
                    .append(chatColors.get("player-names-ranked")).append(playerName)
                    .append(chatColors.get("dots"));

            if (useDots) {
                rankList.append(" ");
                int dots = (int) Math.round((125.0 - font.getWidth(count + ". " + playerName))/2);
                if (dots >= 1) {
                    rankList.append(".".repeat(dots));
                }
            }
            else {
                rankList.append(":");
            }

            rankList.append(" ").append(chatColors.get("stat-numbers-ranked")).append(topStats.get(playerName).toString());
        }
        return title + rankList;
    }

    public void updateOutputColors() {
        chatColors = config.getChatColors();
    }
}
