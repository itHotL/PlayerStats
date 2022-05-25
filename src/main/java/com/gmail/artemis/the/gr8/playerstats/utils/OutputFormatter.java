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
    private static String pluginPrefix;

    public OutputFormatter(ConfigHandler c, boolean enableHexColors) {
        config = c;
        pluginPrefix = ChatColor.GRAY + "[" + ChatColor.GOLD + "PlayerStats" + ChatColor.GRAY + "] " + ChatColor.RESET;

        useHex = enableHexColors;
        updateOutputColors();
    }

    public void updateOutputColors() {
        updateOutPutColors(useHex);
    }

    public static String getPluginPrefix() {
        return pluginPrefix;
    }

    public String formatExceptions(String exception) {
        return pluginPrefix + exception;
    }

    /*

    public BaseComponent[] formatHelpSpigot() {
        String spaces = "    ";
        String underscores = "____________";

        ComponentBuilder underscore = new ComponentBuilder(underscores).color(net.md_5.bungee.api.ChatColor.of("#6E3485"));
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

        ComponentBuilder help = new ComponentBuilder()
                .append("\n").append(underscore.create()).append(spaces).append(pluginPrefix).append(spaces).append(underscore.create()).append("\n")
                .append("Hover over the arguments for more information!").color(net.md_5.bungee.api.ChatColor.GRAY).italic(true).append("\n")
                .append("Usage: ").color(net.md_5.bungee.api.ChatColor.GOLD).italic(false)
                .append("/statistic ").color(net.md_5.bungee.api.ChatColor.YELLOW).append("\n")
                .append(spaces).append(arrow).append(statName).append("\n").reset()
                .append(spaces).append(arrow).append(subStatName).append("\n").reset()
                .append(spaces).append(arrow).append(target).append("\n").reset()
                .append(spaces).append(arrow).append(playerName);

        return help.create();
    }

     */

    public String formatHelpBukkit() {
        String spaces = "    ";
        String underscores = ChatColor.GRAY + "____________";
        String arrow = spaces + ChatColor.GOLD + "→ ";
        ChatColor argumentColor = ChatColor.YELLOW;
        ChatColor descriptionColor = ChatColor.WHITE;

        return "\n" +
                underscores + spaces + pluginPrefix + spaces + underscores + "\n" +
                ChatColor.GRAY + ChatColor.ITALIC + "A list of all the required and optional arguments" + ChatColor.RESET + "\n" +
                ChatColor.GOLD + "Usage: " + argumentColor + "/statistic " + "\n" +
                arrow + argumentColor + "name: " +
                descriptionColor + "The name of the statistic (example: \"mine_block\")" + "\n" +
                arrow + argumentColor + "sub-statistic: " +
                descriptionColor + "Some statistics require an item, block or entity as sub-statistic (example: \"mine_block diorite\")" + "\n" +
                arrow + argumentColor + "me | player | top: " +
                descriptionColor + "Choose whether you want to see your own statistic, another player's, or the top " + config.getTopListMaxSize() + "\n" +
                arrow + argumentColor + "player-name: " +
                descriptionColor + "In case you selected \"player\", specify the player's name here";
    }

    public String formatPlayerStat(String playerName, String statName, String subStatEntryName, int stat) {
        StringBuilder singleStat = new StringBuilder();
        String subStat = subStatEntryName != null ?
                " (" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";

        singleStat.append(getPlayerFormatting(false)).append(playerName).append(": ")
                .append(getStatNumberFormatting(false)).append(stat).append(" ")
                .append(getStatNameFormatting(false))
                        .append(statName.toLowerCase().replace("_", " "))
                .append(getSubStatNameFormatting(false)).append(subStat);

        return singleStat.toString();
    }

    public String formatTopStats(LinkedHashMap<String, Integer> topStats, String statName, String subStatEntryName) {
        StringBuilder topList = new StringBuilder();
        String subStat = subStatEntryName != null ?
                " (" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";

        topList.append("\n").append(pluginPrefix)
                .append(getStatNameFormatting(true)).append("Top ")
                .append(getListNumberFormatting()).append(topStats.size())
                .append(getStatNameFormatting(true)).append(" ")
                        .append(statName.toLowerCase().replace("_", " "))
                .append(getSubStatNameFormatting(true)).append(subStat);

        boolean useDots = config.useDots();
        Set<String> playerNames = topStats.keySet();
        MinecraftFont font = new MinecraftFont();

        int count = 0;
        for (String playerName : playerNames) {
            count = count+1;

            topList.append("\n")
                    .append(getListNumberFormatting()).append(count).append(". ")
                    .append(getPlayerFormatting(true)).append(playerName);

            if (useDots) {
                topList.append(getDotColor()).append(" ");

                int dots = (int) Math.round((130.0 - font.getWidth(count + ". " + playerName))/2);
                if (topPlayerStyleIsBold()) {
                    dots = (int) Math.round((130.0 - font.getWidth(count + ". ") - (font.getWidth(playerName) * 1.19))/2);
                }
                if (dots >= 1) {
                    topList.append(".".repeat(dots));
                }
            }
            else {
                topList.append(":");
            }
            topList.append(" ").append(getStatNumberFormatting(true)).append(topStats.get(playerName).toString());
        }
        return topList.toString();
    }

    private String getPlayerFormatting(boolean isTopStat) {
        return getColor("player-names", false, isTopStat) + "" +
                getColor("player-names", true, isTopStat);
    }

    private boolean topPlayerStyleIsBold() {
        return getColor("player-names", true, true).equals(ChatColor.BOLD);
    }

    private String getStatNameFormatting(boolean isTopStat) {
        return getColor("stat-names", false, isTopStat) + "" +
                getColor("stat-names", true, isTopStat);
    }

    private String getSubStatNameFormatting(boolean isTopStat) {
        return getColor("sub-stat-names", false, isTopStat) + "" +
                getColor("sub-stat-names", true, isTopStat);
    }

    private String getStatNumberFormatting(boolean isTopStat) {
        return getColor("stat-numbers", false, isTopStat) + "" +
                getColor("stat-numbers", true, isTopStat);
    }

    private String getListNumberFormatting() {
        return getColor("list-numbers", false, true) + "" +
                getColor("list-numbers", true, true);
    }

    private String getDotColor() {
        return getColor("dots", false,true) + "";
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
