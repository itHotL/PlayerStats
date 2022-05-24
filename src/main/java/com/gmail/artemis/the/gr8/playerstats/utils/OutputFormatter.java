package com.gmail.artemis.the.gr8.playerstats.utils;

import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
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

    public String getPluginPrefix() {
        return pluginPrefix;
    }

    public String formatExceptions(String exception) {
        return pluginPrefix + exception;
    }

    public BaseComponent[] formatHelp() {
        String spaces = "    ";

        if (useHex) {
            ComponentBuilder underscores = new ComponentBuilder("____________").color(net.md_5.bungee.api.ChatColor.of("#6E3485"));
            TextComponent arrow = new TextComponent("→ ");
            arrow.setColor(net.md_5.bungee.api.ChatColor.GOLD);

            TextComponent statName = new TextComponent("name");
            statName.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
            statName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new Text("The name of the statistic (example: \"mine_block\")")));

            TextComponent subStatName = new TextComponent("sub-statistic");
            subStatName.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
            subStatName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new Text("Some statistics require an item, block or entity as sub-statistic (example: \"mine_block diorite\")")));

            TextComponent target = new TextComponent("me | player | top");
            target.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
            target.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new Text("Choose whether you want to see your own statistic, another player's, or the top " + config.getTopListMaxSize())));

            TextComponent playerName = new TextComponent("player-name");
            playerName.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
            playerName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new Text("In case you selected \"player\", specify the player's name here")));

            ComponentBuilder title = new ComponentBuilder()
                    .append(underscores.create()).append("\n").append(spaces).append(pluginPrefix).append(spaces).append(underscores.create()).append("\n")
                    .append("Hover over the arguments for more information!").color(net.md_5.bungee.api.ChatColor.GRAY).italic(true).append("\n")
                    .append("Usage: ").color(net.md_5.bungee.api.ChatColor.GOLD).italic(false)
                    .append("/statistic ").color(net.md_5.bungee.api.ChatColor.YELLOW).append("\n")
                    .append(spaces).append(arrow).append(statName).append("\n").reset()
                    .append(spaces).append(arrow).append(subStatName).append("\n").reset()
                    .append(spaces).append(arrow).append(target).append("\n").reset()
                    .append(spaces).append(arrow).append(playerName);

            return title.create();
        }
        else {
            return new BaseComponent[0];
        }
    }

    public String formatPlayerStat(String playerName, String statName, String subStatEntryName, int stat) {
        StringBuilder singleStat = new StringBuilder();
        String subStat = subStatEntryName != null ?
                " (" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";

        singleStat.append(getPlayerFormatting(false)).append(playerName).append(": ")
                .append(getStatNumberColor(false)).append(getStatNumberStyle(false)).append(stat).append(" ")
                .append(getStatNameColor(false)).append(getStatNameStyle(false))
                        .append(statName.toLowerCase().replace("_", " "))
                .append(getSubStatNameColor(false)).append(getSubStatNameStyle(false)).append(subStat);

        return singleStat.toString();
    }

    public String formatTopStats(LinkedHashMap<String, Integer> topStats, String statName, String subStatEntryName) {
        StringBuilder topList = new StringBuilder();
        String subStat = subStatEntryName != null ?
                " (" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";

        topList.append("\n").append(pluginPrefix)
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

            topList.append("\n")
                    .append(getListNumberColor()).append(getListNumberStyle()).append(count).append(". ")
                    .append(getPlayerColor(true)).append(getPlayerStyle(true)).append(playerName);

            if (useDots) {
                topList.append(getDotColor()).append(" ");

                int dots = (int) Math.round((130.0 - font.getWidth(count + ". " + playerName))/2);
                if (getPlayerStyle(true).equals(ChatColor.BOLD)) {
                    dots = (int) Math.round((130.0 - font.getWidth(count + ". ") - (font.getWidth(playerName) * 1.19))/2);
                }
                if (dots >= 1) {
                    topList.append(".".repeat(dots));
                }
            }
            else {
                topList.append(":");
            }
            topList.append(" ").append(getStatNumberColor(true)).append(getStatNumberStyle(true)).append(topStats.get(playerName).toString());
        }
        return topList.toString();
    }

    private String getPlayerFormatting(boolean isTopStat) {
        return getPlayerColor(isTopStat) + "" + getPlayerStyle(isTopStat);
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
