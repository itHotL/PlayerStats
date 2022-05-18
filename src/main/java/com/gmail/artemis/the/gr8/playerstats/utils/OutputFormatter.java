package com.gmail.artemis.the.gr8.playerstats.utils;

import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import org.bukkit.ChatColor;
import org.bukkit.map.MinecraftFont;

import java.util.*;

public class OutputFormatter {

    //keys for the HashMaps are the same as the config options (so e.g. player-names/player-names-ranked)
    private final boolean useHex;
    private static ConfigHandler config;
    private HashMap<String, ChatColor> chatColors;
    private HashMap<String, ChatColor> styleOptions;
    private HashMap<String, net.md_5.bungee.api.ChatColor> hexChatColors;
    private final String pluginPrefix;

    public OutputFormatter(ConfigHandler c, boolean enableHexColors) {
        config = c;
        pluginPrefix = ChatColor.GRAY + "[" + ChatColor.GOLD + "PlayerStats" + ChatColor.GRAY + "] " + ChatColor.RESET;

        useHex = enableHexColors;
        updateOutputColors();
    }

    public void updateOutputColors() {
        updateOutPutColors(useHex);
    }

    public String formatExceptions(String exception) {
        return pluginPrefix + exception;
    }

    public String formatPlayerStat(String playerName, String statName, String subStatEntryName, int stat) {
        StringBuilder msg = new StringBuilder();
        String subStat = subStatEntryName != null ?
                " (" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";

        msg.append(getPlayerColor(false)).append(getPlayerStyle(false)).append(playerName).append(": ")
                .append(getStatNumberColor(false)).append(getStatNumberStyle(false)).append(stat).append(" ")
                .append(getStatNameColor(false)).append(getStatNameStyle(false))
                        .append(statName.toLowerCase().replace("_", " "))
                .append(getSubStatNameColor(false)).append(getSubStatNameStyle(false)).append(subStat);

        return msg.toString();
    }

    public String formatTopStats(LinkedHashMap<String, Integer> topStats, String statName, String subStatEntryName) {
        StringBuilder msg = new StringBuilder();
        String subStat = subStatEntryName != null ?
                " (" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";

        msg.append("\n").append(pluginPrefix)
                .append(getStatNameColor(true)).append(getStatNameStyle(true)).append("Top ")
                .append(getListNumberColor()).append(getListNumberStyle()).append(topStats.size())
                .append(getStatNameColor(true)).append(getStatNameStyle(true)).append(" ")
                        .append(statName.toLowerCase().replace("_", " "))
                .append(getSubStatNameColor(true)).append(getSubStatNameStyle(true)).append(subStat);

        boolean useDots = config.useDots();
        Set<String> playerNames = topStats.keySet();
        MinecraftFont font = new MinecraftFont();


        int count = 0;
        for (String playerName : playerNames) {
            count = count+1;

            msg.append("\n")
                    .append(getListNumberColor()).append(getListNumberStyle()).append(count).append(". ")
                    .append(getPlayerColor(true)).append(getPlayerStyle(true)).append(playerName);

            if (useDots) {
                msg.append(getDotColor()).append(" ");

                int dots = (int) Math.round((130.0 - font.getWidth(count + ". " + playerName))/2);
                if (getPlayerStyle(true).equals(ChatColor.BOLD)) {
                    dots = (int) Math.round((130.0 - font.getWidth(count + ". ") - (font.getWidth(playerName) * 1.19))/2);
                }
                if (dots >= 1) {
                    msg.append(".".repeat(dots));
                }
            }
            else {
                msg.append(":");
            }
            msg.append(" ").append(getStatNumberColor(true)).append(getStatNumberStyle(true)).append(topStats.get(playerName).toString());
        }
        return msg.toString();
    }

    private Object getPlayerColor(boolean isTopStat) {return getColor("player-names", false, isTopStat);}

    private Object getPlayerStyle(boolean isTopStat) {return getColor("player-names", true, isTopStat);}

    private Object getStatNameColor(boolean isTopStat) {
        return getColor("stat-names", false, isTopStat);
    }

    private Object getStatNameStyle(boolean isTopStat) {return getColor("stat-names", true, isTopStat);}

    private Object getSubStatNameColor(boolean isTopStat) {
        return getColor("sub-stat-names", false, isTopStat);
    }

    private Object getSubStatNameStyle(boolean isTopStat) {return getColor("sub-stat-names", true, isTopStat);}

    private Object getStatNumberColor(boolean isTopStat) {
        return getColor("stat-numbers", false, isTopStat);
    }

    private Object getStatNumberStyle(boolean isTopStat) {return getColor("stat-numbers", true, isTopStat);}

    private Object getListNumberColor() {
        return getColor("list-numbers", false, true);
    }

    private Object getListNumberStyle() {return getColor("list-numbers", true, true);}

    private Object getDotColor() {
        return getColor("dots", false,true);
    }

    //gets the appropriate ChatColor object (or empty string), depending on whether the Spigot ChatColor is available or not
    //the HashMap keys are config paths (with -top for the top-list variants)
    private Object getColor(String path, boolean isStyleOption, boolean isTopStat) {
        path = isTopStat ? path + "-top" : path;
        if (isStyleOption) {
            return styleOptions.get(path) != null ? styleOptions.get(path) : "";
        }
        else if (useHex){
            return hexChatColors.get(path) != null ? hexChatColors.get(path) : "";
        }
        else {
            return chatColors.get(path) != null ? chatColors.get(path) : "";
        }
    }

    private void updateOutPutColors(boolean useHex) {
        styleOptions = config.getStyleOptions();

        if (useHex) {
            hexChatColors = config.getHexChatColors();
        }
        else {
            chatColors = config.getChatColors();
        }
    }
}
