package com.gmail.artemis.the.gr8.playerstats.utils;

import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.map.MinecraftFont;

import java.util.*;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;

public class MessageFactory {

    private static ConfigHandler config;

    public MessageFactory(ConfigHandler c) {
        config = c;
    }

    public static String getPluginPrefix() {
        return ChatColor.GRAY + "[" + ChatColor.GOLD + "PlayerStats" + ChatColor.GRAY + "] " + ChatColor.RESET;
    }

    public String formatExceptions(String exception) {
        return getPluginPrefix() + exception;
    }

    public TextComponent getHelpMsg() {
        TextComponent spaces = text("    ");
        TextComponent underscores = text("____________").color(TextColor.fromHexString("#6E3485"));
        TextComponent arrow = text("→ ").color(NamedTextColor.GOLD);
        TextColor arguments = NamedTextColor.YELLOW;
        TextColor hoverDescription = NamedTextColor.GOLD;
        TextColor hoverExample1 = TextColor.fromHexString("#FFD52B");
        TextColor hoverExample2 = NamedTextColor.YELLOW;

        return Component.newline()
                .append(underscores).append(spaces).append(text(MessageFactory.getPluginPrefix())).append(spaces).append(underscores)
                .append(newline())
                .append(text("Hover over the arguments for more information!").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                .append(newline())
                .append(text("Usage: ").color(NamedTextColor.GOLD)).append(text("/statistic").color(arguments))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("name").color(arguments)
                        .hoverEvent(HoverEvent.showText(text("The name of the statistic").color(hoverDescription)
                                .append(newline())
                                .append(text("Example: ").color(hoverExample1))
                                .append(text("\"mine_block\"").color(hoverExample2)))))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("sub-statistic").color(arguments)
                        .hoverEvent(HoverEvent.showText(
                                text("Some statistics require an item, block or entity as sub-statistic").color(hoverDescription)
                                        .append(newline())
                                        .append(text("Example: ").color(hoverExample1)
                                                .append(text("\"mine_block diorite\"").color(hoverExample2))))))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("me | player | top").color(arguments)
                        .hoverEvent(HoverEvent.showText(
                                text("Choose whether you want to see your own statistic, another player's, or the top ").color(hoverDescription)
                                        .append(text(config.getTopListMaxSize()).color(hoverDescription)))))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("player-name").color(arguments)
                        .hoverEvent(HoverEvent.showText(
                                text("In case you selected ").color(hoverDescription)
                                        .append(text("\"player\"").color(hoverExample2)
                                                .append(text(", specify the player's name here").color(hoverDescription))))));
    }

    public String formatPlayerStat(String playerName, String statName, String subStatEntryName, int stat) {
        StringBuilder singleStat = new StringBuilder();
        String subStat = subStatEntryName != null ?
                " (" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";

        singleStat.append(config.getPlayerNamesColor(false)).append(playerName).append(": ")
                .append(config.getStatNumbersColor(false)).append(stat).append(" ")
                .append(config.getStatNamesColor(false))
                        .append(statName.toLowerCase().replace("_", " "))
                .append(config.getSubStatNamesColor(false)).append(subStat);

        return singleStat.toString();
    }

    public String formatTopStats(LinkedHashMap<String, Integer> topStats, String statName, String subStatEntryName) {
        StringBuilder topList = new StringBuilder();
        String subStat = subStatEntryName != null ?
                " (" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";

        topList.append("\n").append(getPluginPrefix())
                .append(config.getStatNamesColor(true)).append("Top ")
                .append(config.getListNumbersColor()).append(topStats.size())
                .append(config.getStatNamesColor(true)).append(" ")
                        .append(statName.toLowerCase().replace("_", " "))
                .append(config.getSubStatNamesColor(true)).append(subStat);

        boolean useDots = config.useDots();
        Set<String> playerNames = topStats.keySet();
        MinecraftFont font = new MinecraftFont();

        int count = 0;
        for (String playerName : playerNames) {
            count = count+1;

            topList.append("\n")
                    .append(config.getListNumbersColor()).append(count).append(". ")
                    .append(config.getPlayerNamesColor(true)).append(playerName);

            if (useDots) {
                topList.append(config.getDotsColor()).append(" ");

                int dots = (int) Math.round((130.0 - font.getWidth(count + ". " + playerName))/2);
                if (config.playerNamesStyleIsBold()) {
                    dots = (int) Math.round((130.0 - font.getWidth(count + ". ") - (font.getWidth(playerName) * 1.19))/2);
                }
                if (dots >= 1) {
                    topList.append(".".repeat(dots));
                }
            }
            else {
                topList.append(":");
            }
            topList.append(" ").append(config.getStatNumbersColor(true)).append(topStats.get(playerName).toString());
        }
        return topList.toString();
    }

    //try to get the hex color or ChatColor from config String, substitute green if both fail, and try to apply style if necessary
    private TextComponent playerNameComponent(String playerName, boolean topStat) {
        ChatColor defaultColor = topStat ? ChatColor.GREEN : ChatColor.GOLD;
        TextComponent player = applyColor(
                config.getPlayerNamesColor(topStat), playerName, defaultColor);
        return applyStyle(config.getPlayerNamesStyle(topStat), player);
    }

    private TextComponent statNameComponent(String statName, boolean topStat) {
        TextComponent stat = applyColor(
                config.getStatNamesColor(topStat), statName, ChatColor.YELLOW);
        return applyStyle(config.getStatNamesStyle(topStat), stat);
    }

    private TextComponent subStatNameComponent(String subStatName, boolean topStat) {
        TextComponent subStat = applyColor(
                config.getSubStatNamesColor(topStat), subStatName, ChatColor.YELLOW);
        return applyStyle(config.getSubStatNamesStyle(topStat), subStat);
    }

    private TextComponent statNumberComponent(int statNumber, boolean topStat) {
        TextComponent number = applyColor(
                config.getStatNumbersColor(topStat), statNumber + "", ChatColor.LIGHT_PURPLE);
        return applyStyle(config.getStatNumbersStyle(topStat), number);
    }

    private TextComponent listNumberComponent(int listNumber) {
        TextComponent list = applyColor(config.getListNumbersColor(), listNumber + ".", ChatColor.GOLD);
        return applyStyle(config.getListNumbersStyle(), list);
    }

    private TextComponent dotsComponent() {
        return applyColor(config.getDotsColor(), "", ChatColor.DARK_GRAY);
    }

    private TextComponent applyColor(String configString, String content, ChatColor defaultColor) {
        TextComponent component = Component.text().build();
        ChatColor color = defaultColor;

        if (configString != null) {
            if (configString.contains("#")) {
                return component.content(content).color(TextColor.fromHexString(configString));
            }
            else {
                try {
                    color = ChatColor.valueOf(configString.toUpperCase().replace(" ", "_"));
                }
                catch (IllegalArgumentException | NullPointerException exception) {
                    exception.printStackTrace();
                }
            }
        }
        return component.content(content + color);
    }

    private TextComponent applyStyle(String configString, TextComponent component) {
        if (configString != null) {
            if (configString.equalsIgnoreCase("none")) {
                return component;
            }
            else if (configString.equalsIgnoreCase("bold")) {
                return component.decorate(TextDecoration.BOLD);
            }
            else if (configString.equalsIgnoreCase("italic")) {
                return component.decorate(TextDecoration.ITALIC);
            }
            else if (configString.equalsIgnoreCase("magic")) {
                return component.decorate(TextDecoration.OBFUSCATED);
            }
            else if (configString.equalsIgnoreCase("strikethrough")) {
                return component.decorate(TextDecoration.STRIKETHROUGH);
            }
            else if (configString.equalsIgnoreCase("underlined")) {
                return component.decorate(TextDecoration.UNDERLINED);
            }
        }
        return component;
    }
}
