package com.gmail.artemis.the.gr8.playerstats.utils;

import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import org.bukkit.ChatColor;
import org.bukkit.map.MinecraftFont;

import java.util.*;

public class OutputFormatter {

    //keys for the HashMaps are the same as the config options (so e.g. player-names/player-names-ranked)
    private final boolean useHex;
    private final ConfigHandler config;
    private HashMap<String, ChatColor> chatColors;
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

        msg.append(getPlayerColor(false)).append(playerName).append(": ")
                .append(getStatNumberColor(false)).append(stat).append(" ")
                .append(getStatNameColor(false)).append(statName.toLowerCase().replace("_", " "))
                .append(getSubStatNameColor(false)).append(subStat);

        return msg.toString();
    }

    public String formatTopStats(LinkedHashMap<String, Integer> topStats, String statName, String subStatEntryName) {
        StringBuilder msg = new StringBuilder();
        String subStat = subStatEntryName != null ?
                " (" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";

        msg.append("\n").append(pluginPrefix)
                .append(getStatNameColor(true)).append("Top ")
                .append(getListNumberColor()).append(topStats.size())
                .append(getStatNameColor(true)).append(" ").append(statName.toLowerCase().replace("_", " "))
                .append(getSubStatNameColor(true)).append(subStat);

        boolean useDots = config.useDots();
        Set<String> playerNames = topStats.keySet();
        MinecraftFont font = new MinecraftFont();

        int count = 0;
        for (String playerName : playerNames) {
            count = count+1;

            msg.append("\n")
                    .append(getListNumberColor()).append(count).append(". ")
                    .append(getPlayerColor(true)).append(playerName);

            if (useDots) {
                msg.append(getDotColor()).append(" ");
                int dots = (int) Math.round((130.0 - font.getWidth(count + ". " + playerName))/2);
                if (dots >= 1) {
                    msg.append(".".repeat(dots));
                }
            }
            else {
                msg.append(":");
            }
            msg.append(" ").append(getStatNumberColor(true)).append(topStats.get(playerName).toString());
        }
        return msg.toString();
    }

    private Object getPlayerColor(boolean isTopStat) {
        return getColor("player-names", isTopStat);
    }

    private Object getStatNameColor(boolean isTopStat) {
        return getColor("stat-names", isTopStat);
    }

    private Object getSubStatNameColor(boolean isTopStat) {
        return getColor("sub-stat-names", isTopStat);
    }

    private Object getStatNumberColor(boolean isTopStat) {
        return getColor("stat-numbers", isTopStat);
    }

    private Object getListNumberColor() {
        return getColor("list-numbers", true);
    }

    private Object getDotColor() {
        return getColor("dots", true);
    }

    //gets the appropriate ChatColor object, depending on whether the Spigot ChatColor is available or not
    private Object getColor(String path, boolean isTopStat) {
        path = isTopStat ? path + "-top" : path;
        return useHex ? hexChatColors.get(path) : chatColors.get(path);
    }

    private void updateOutPutColors(boolean useHex) {
        if (useHex) {
            hexChatColors = config.getHexChatColors();
        }
        else {
            chatColors = config.getChatColors();
        }
    }
}
