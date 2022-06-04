package com.gmail.artemis.the.gr8.playerstats.utils;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.util.Index;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.map.MinecraftFont;

import java.util.*;

import static net.kyori.adventure.text.Component.*;

public class MessageFactory {

    private static ConfigHandler config;

    private static final TextColor msgColor = TextColor.fromHexString("#55aaff");
    private static final String pluginPrefix = ChatColor.GRAY + "[" + ChatColor.GOLD + "PlayerStats" + ChatColor.GRAY + "] " + ChatColor.RESET;

    public MessageFactory(ConfigHandler c) {
        config = c;
    }

    public static String getPluginPrefix() {
        return pluginPrefix;
    }

    public TextComponent stillReloading() {
        return text(getPluginPrefix()).append(text("The plugin is still (re)loading, your request will be processed when it is done!").color(msgColor));
    }

    public TextComponent waitAMoment(boolean longWait) {
        return longWait ? text(getPluginPrefix()).append(text("Calculating statistics, this may take a minute...").color(msgColor))
                : text(getPluginPrefix()).append(text("Calculating statistics, this may take a few moments...").color(msgColor));
    }

    public String formatExceptions(String exception) {
        return getPluginPrefix() + exception;
    }

    public TextComponent missingStatName() {
        return text(getPluginPrefix()).append(text("Please provide a valid statistic name!").color(msgColor));
    }

    public TextComponent missingSubStatName(Statistic.Type statType) {
        String subStat = getSubStatTypeName(statType) == null ? "sub-statistic" : getSubStatTypeName(statType);
        return text(getPluginPrefix())
                .append(text("Please add a valid ")
                        .append(text(subStat))
                        .append(text(" to look up this statistic!")))
                .color(msgColor);
    }

    public TextComponent missingTarget() {
        return text(getPluginPrefix()).append(text("Please add \"me\", \"player\" or \"top\"").color(msgColor));
    }

    public TextComponent missingPlayerName() {
        return text(getPluginPrefix()).append(text("Please specify a valid player-name!").color(msgColor));
    }

    public TextComponent wrongSubStatType(Statistic.Type statType, String subStatEntry) {
        String subStat = getSubStatTypeName(statType) == null ? "sub-statistic for this statistic" : getSubStatTypeName(statType);
        return text(getPluginPrefix())
                .append(text("\"")
                        .append(text(subStatEntry))
                        .append(text("\""))
                        .append(text(" is not a valid "))
                        .append(text(subStat)))
                .color(msgColor);
    }

    public TextComponent unknownError() {
        return text(getPluginPrefix()).append(text("Something went wrong with your request, please try again!").color(msgColor));
    }

    public TextComponent helpMsg() {
        TextComponent spaces = text("    ");
        TextComponent underscores = text("____________").color(TextColor.fromHexString("#6E3485"));
        TextComponent arrow = text("→ ").color(NamedTextColor.GOLD);
        TextColor arguments = NamedTextColor.YELLOW;
        TextColor hoverBaseColor = TextColor.fromHexString("#55C6FF");
        TextColor hoverAccentColor1 = TextColor.fromHexString("#FFB80E");
        TextColor hoverAccentColor2 = TextColor.fromHexString("#FFD52B");

        return Component.newline()
                .append(underscores).append(spaces).append(text(MessageFactory.getPluginPrefix())).append(spaces).append(underscores)
                .append(newline())
                .append(text("Hover over the arguments for more information!").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                .append(newline())
                .append(text("Usage: ").color(NamedTextColor.GOLD)).append(text("/statistic").color(arguments))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("name").color(arguments)
                        .hoverEvent(HoverEvent.showText(text("The name that describes the statistic").color(hoverBaseColor)
                                .append(newline())
                                .append(text("Example: ").color(hoverAccentColor1))
                                .append(text("\"animals_bred\"").color(hoverAccentColor2)))))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("sub-statistic").color(arguments)
                        .hoverEvent(HoverEvent.showText(
                                text("Some statistics need an item, block or entity as sub-statistic").color(hoverBaseColor)
                                        .append(newline())
                                        .append(text("Example: ").color(hoverAccentColor1)
                                                .append(text("\"mine_block diorite\"").color(hoverAccentColor2))))))
                .append(newline())
                .append(spaces)
                .append(text("→").color(NamedTextColor.GOLD)
                        .hoverEvent(HoverEvent.showText(
                                text("Choose one").color(TextColor.fromHexString("#6E3485")))))
                .append(space())
                .append(text("me").color(arguments)
                        .hoverEvent(HoverEvent.showText(
                                text("See your own statistic").color(hoverBaseColor))))
                .append(text(" | ").color(arguments))
                .append(text("player").color(arguments)
                        .hoverEvent(HoverEvent.showText(
                                text("Choose any player that has played on your server").color(hoverBaseColor))))
                .append(text(" | ").color(arguments))
                .append(text("top").color(arguments)
                        .hoverEvent(HoverEvent.showText(
                                text("See the top ").color(hoverBaseColor)
                                        .append(text(config.getTopListMaxSize()).color(hoverBaseColor)))))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("player-name").color(arguments)
                        .hoverEvent(HoverEvent.showText(
                                text("In case you typed ").color(hoverBaseColor)
                                        .append(text("\"player\"").color(hoverAccentColor2)
                                                .append(text(", add the player's name").color(hoverBaseColor))))));
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

    //returns the type of the substatistic in String-format, or null if this statistic is not of type block, item or entity
    private String getSubStatTypeName(Statistic.Type statType) {
        String subStat;
        if (statType == Statistic.Type.BLOCK) {
            subStat = "block";
        }
        else if (statType == Statistic.Type.ITEM) {
            subStat = "item";
        }
        else if (statType == Statistic.Type.ENTITY) {
            subStat = "entity";
        }
        else {
            subStat = null;
        }
        return subStat;
    }

    //try to get the hex color or NamedTextColor from config String, substitute a default ChatColor if both fail, and try to apply style where necessary
    private TextComponent playerNameComponent(String playerName, boolean topStat) {
        NamedTextColor defaultColor = topStat ? NamedTextColor.GREEN : NamedTextColor.GOLD;
        TextComponent.Builder player = applyColor(
                config.getPlayerNameFormatting(topStat, false), playerName, defaultColor);
        return applyStyle(config.getPlayerNameFormatting(topStat, true), player).build();
    }

    private TextComponent statNameComponent(String statName, boolean topStat) {
        TextComponent.Builder stat = applyColor(
                config.getStatNameFormatting(topStat, false), statName, NamedTextColor.YELLOW);
        return applyStyle(config.getStatNameFormatting(topStat, true), stat).build();
    }

    private TextComponent subStatNameComponent(String subStatName, boolean topStat) {
        TextComponent.Builder subStat = applyColor(
                config.getSubStatNameFormatting(topStat, false), subStatName, NamedTextColor.YELLOW);
        return applyStyle(config.getSubStatNameFormatting(topStat, true), subStat).build();
    }

    private TextComponent statNumberComponent(int statNumber, boolean topStat) {
        TextComponent.Builder number = applyColor(
                config.getStatNumberFormatting(topStat, false), statNumber + "", NamedTextColor.LIGHT_PURPLE);
        return applyStyle(config.getStatNumberFormatting(topStat, true), number).build();
    }

    private TextComponent listNumberComponent(String listNumber) {
        TextComponent.Builder list = applyColor(config.getListNumberFormatting(false), listNumber + "", NamedTextColor.GOLD);
        return applyStyle(config.getListNumberFormatting(true), list).build();
    }

    private TextComponent dotsComponent(String dots) {
        return text(dots).color(getColorFromString(config.getDotsColor())).colorIfAbsent(NamedTextColor.DARK_GRAY);
        //return applyColor(config.getDotsColor(), dots, NamedTextColor.DARK_GRAY).build();
    }

    private TextColor getColorFromString(String configString) {
        if (configString != null) {
            try {
                if (configString.contains("#")) {
                    return TextColor.fromHexString(configString);
                }
                else {
                    return getTextColorByName(configString);
                }
            }
            catch (IllegalArgumentException | NullPointerException exception) {
                Bukkit.getLogger().warning(exception.toString());
            }
        }
        return null;
    }

    private TextComponent.Builder applyColor(String configString, String content, NamedTextColor defaultColor) {
        TextComponent.Builder component = Component.text();

        if (configString != null) {
            try {
                if (configString.contains("#")) {
                    return component.content(content).color(TextColor.fromHexString(configString));
                }
                else {
                    return component.content(content).color(getTextColorByName(configString));
                }
            }
            catch (IllegalArgumentException | NullPointerException exception) {
                Bukkit.getLogger().warning(exception.toString());
            }
        }
        return component.content(content).colorIfAbsent(defaultColor);
    }

    private TextColor getTextColorByName(String textColor) {
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
