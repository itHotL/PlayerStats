package com.gmail.artemis.the.gr8.playerstats.utils;

import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.util.Index;
import org.bukkit.ChatColor;
import org.bukkit.map.MinecraftFont;

import java.util.*;

import static net.kyori.adventure.text.Component.*;

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

    public TextComponent formatPlayerStat(String playerName, String statName, String subStatEntryName, int stat) {
        TextComponent.Builder singleStat = Component.text();
        String subStat = subStatEntryName != null ?
                " (" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";

        singleStat.append(playerNameComponent(playerName + ": ", false))
                .append(statNumberComponent(stat, false)).append(space())
                .append(statNameComponent(statName.toLowerCase().replace("_", " "), false))
                .append(subStatNameComponent(subStat, false));

        return singleStat.build();
    }

    public TextComponent formatTopStats(LinkedHashMap<String, Integer> topStats, String statName, String subStatEntryName) {
        TextComponent.Builder topList = Component.text();
        String subStat = subStatEntryName != null ?
                "(" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";

        topList.append(newline()).append(text(getPluginPrefix()))
                .append(statNameComponent("Top", true)).append(space())
                .append(listNumberComponent(topStats.size() + "")).append(space())
                .append(statNameComponent(statName.toLowerCase().replace("_", " "), true)).append(space())
                .append(subStatNameComponent(subStat, true));

        boolean useDots = config.useDots();
        Set<String> playerNames = topStats.keySet();
        MinecraftFont font = new MinecraftFont();

        int count = 0;
        for (String playerName : playerNames) {
            count = count+1;

            topList.append(newline())
                    .append(listNumberComponent(count + ". "))
                    .append(playerNameComponent(playerName, true));

            if (useDots) {
                topList.append(space());

                int dots = (int) Math.round((130.0 - font.getWidth(count + ". " + playerName))/2);
                if (config.playerNameIsBold()) {
                    dots = (int) Math.round((130.0 - font.getWidth(count + ". ") - (font.getWidth(playerName) * 1.19))/2);
                }
                if (dots >= 1) {
                    topList.append(dotsComponent(".".repeat(dots)));
                }
            }
            else {
                topList.append(playerNameComponent(":", true));
            }
            topList.append(space()).append(statNumberComponent(topStats.get(playerName), true));
        }
        return topList.build();
    }

    //try to get the hex color or ChatColor from config String, substitute green if both fail, and try to apply style if necessary
    private TextComponent playerNameComponent(String playerName, boolean topStat) {
        ChatColor defaultColor = topStat ? ChatColor.GREEN : ChatColor.GOLD;
        TextComponent.Builder player = applyColor(
                config.getPlayerNameFormatting(topStat, false), playerName, defaultColor);
        return applyStyle(config.getPlayerNameFormatting(topStat, true), player).build();
    }

    private TextComponent statNameComponent(String statName, boolean topStat) {
        TextComponent.Builder stat = applyColor(
                config.getStatNameFormatting(topStat, false), statName, ChatColor.YELLOW);
        return applyStyle(config.getStatNameFormatting(topStat, true), stat).build();
    }

    private TextComponent subStatNameComponent(String subStatName, boolean topStat) {
        TextComponent.Builder subStat = applyColor(
                config.getSubStatNameFormatting(topStat, false), subStatName, ChatColor.YELLOW);
        return applyStyle(config.getSubStatNameFormatting(topStat, true), subStat).build();
    }

    private TextComponent statNumberComponent(int statNumber, boolean topStat) {
        TextComponent.Builder number = applyColor(
                config.getStatNumberFormatting(topStat, false), statNumber + "", ChatColor.LIGHT_PURPLE);
        return applyStyle(config.getStatNumberFormatting(topStat, true), number).build();
    }

    private TextComponent listNumberComponent(String listNumber) {
        TextComponent.Builder list = applyColor(config.getListNumberFormatting(false), listNumber + "", ChatColor.GOLD);
        return applyStyle(config.getListNumberFormatting(true), list).build();
    }

    private TextComponent dotsComponent(String dots) {
        return applyColor(config.getDotsColor(), dots, ChatColor.DARK_GRAY).build();
    }

    private TextComponent.Builder applyColor(String configString, String content, ChatColor defaultColor) {
        TextComponent.Builder component = Component.text();
        
        if (configString != null) {
            if (configString.contains("#")) {
                return component.content(content).color(TextColor.fromHexString(configString));
            }
            else {
                try {
                    return component.content(content).color(getTextColor(configString));
                }
                catch (IllegalArgumentException | NullPointerException exception) {
                    //color = ChatColor.valueOf(configString.toUpperCase().replace(" ", "_"));
                    exception.printStackTrace();
                }
            }
        }
        return component.content(defaultColor + content);
    }

    private TextColor getTextColor(String textColor) {
        Index<String, NamedTextColor> names = NamedTextColor.NAMES;
        return names.value(textColor);
    }

    private TextComponent.Builder applyStyle(String configString, TextComponent.Builder component) {
        if (configString != null) {
            if (configString.equalsIgnoreCase("none")) {
                return component;
            }
            else if (configString.equalsIgnoreCase("bold")) {
                return component.decoration(TextDecoration.BOLD, TextDecoration.State.TRUE);
            }
            else if (configString.equalsIgnoreCase("italic")) {
                return component.decoration(TextDecoration.ITALIC, TextDecoration.State.TRUE);
            }
            else if (configString.equalsIgnoreCase("magic")) {
                return component.decoration(TextDecoration.OBFUSCATED, TextDecoration.State.TRUE);
            }
            else if (configString.equalsIgnoreCase("strikethrough")) {
                return component.decoration(TextDecoration.STRIKETHROUGH, TextDecoration.State.TRUE);
            }
            else if (configString.equalsIgnoreCase("underlined")) {
                return component.decoration(TextDecoration.UNDERLINED, TextDecoration.State.TRUE);
            }
        }
        return component;
    }
}
