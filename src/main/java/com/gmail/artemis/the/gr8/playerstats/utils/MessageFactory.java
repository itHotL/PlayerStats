package com.gmail.artemis.the.gr8.playerstats.utils;

import com.gmail.artemis.the.gr8.playerstats.enums.Query;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.kyori.adventure.text.Component.*;

public class MessageFactory {

    private static ConfigHandler config;

    private static final TextColor msgColor = TextColor.fromHexString("#55AAFF");  //my favorite shade of light blue, somewhere between blue and aqua
    private static final TextColor hoverBaseColor = TextColor.fromHexString("#55C6FF");  //light blue - one shade lighter than msgColor
    private static final TextColor accentColor1 = TextColor.fromHexString("#FFB80E");  //gold - one shade lighter than standard gold
    private static final TextColor accentColor2 = TextColor.fromHexString("#FFD52B");  //yellow - a few shades darker than standard yellow


    public MessageFactory(ConfigHandler c) {
        config = c;
    }

    public static String getPluginPrefix() {
        return ChatColor.GRAY + "[" + ChatColor.GOLD + "PlayerStats" + ChatColor.GRAY + "] " + ChatColor.RESET;
    }

    private static TextComponent pluginPrefix() {
        return text("[")
                .append(text("PlayerStats").color(NamedTextColor.GOLD))
                .append(text("]")
                        .append(space()))
                .color(NamedTextColor.GRAY);
    }

    public TextComponent reloadedConfig() {
        return pluginPrefix().append(text("Config reloaded!").color(NamedTextColor.GREEN));
    }

    public TextComponent stillReloading() {
        return pluginPrefix().append(text("The plugin is still (re)loading, your request will be processed when it is done!").color(msgColor));
    }

    public TextComponent partiallyReloaded() {
        return pluginPrefix().append(
                text("The reload process was interrupted. If you notice unexpected behavior, please reload PlayerStats again to fix it!").color(msgColor));
    }

    public TextComponent waitAMoment(boolean longWait) {
        return longWait ? pluginPrefix().append(text("Calculating statistics, this may take a minute...").color(msgColor))
                : pluginPrefix().append(text("Calculating statistics, this may take a few moments...").color(msgColor));
    }

    public TextComponent formatExceptions(String exception) {
        return pluginPrefix().append(text(exception).color(msgColor));
    }

    public TextComponent missingStatName() {
        return pluginPrefix().append(text("Please provide a valid statistic name!").color(msgColor));
    }

    public TextComponent missingSubStatName(Statistic.Type statType) {
        String subStat = getSubStatTypeName(statType) == null ? "sub-statistic" : getSubStatTypeName(statType);
        return pluginPrefix()
                .append(text("Please add a valid ")
                        .append(text(subStat))
                        .append(text(" to look up this statistic!")))
                .color(msgColor);
    }

    public TextComponent missingPlayerName() {
        return pluginPrefix().append(text("Please specify a valid player-name!").color(msgColor));
    }

    public TextComponent wrongSubStatType(Statistic.Type statType, String subStatEntry) {
        String subStat = getSubStatTypeName(statType) == null ? "sub-statistic for this statistic" : getSubStatTypeName(statType);
        return pluginPrefix()
                .append(text("\"")
                        .append(text(subStatEntry))
                        .append(text("\""))
                        .append(text(" is not a valid "))
                        .append(text(subStat)))
                .color(msgColor);
    }

    public TextComponent unknownError() {
        return pluginPrefix()
                .append(text("Something went wrong with your request, please try again or see /statistic for a usage explanation!")
                        .color(msgColor));
    }

    public TextComponent helpMsg(boolean isConsoleSender) {
        if (!isConsoleSender) {
            return config.useHoverText() ? helpMsgHover() : helpMsgPlain(false);
        }
        else {
            return helpMsgPlain(true);
        }
    }

    public TextComponent usageExamples(boolean isConsoleSender) {
        TextComponent spaces = text("    "); //4 spaces
        TextComponent underscores = text("_____________").color(TextColor.fromHexString("#6E3485"));
        TextComponent arrow = text("→ ").color(NamedTextColor.GOLD);
        TextColor accentColor = TextColor.fromHexString("#FFE339");

        if (isConsoleSender) {
            arrow = text("-> ").color(NamedTextColor.GOLD);
            accentColor = NamedTextColor.YELLOW;
        }

        return Component.newline()
                .append(underscores).append(spaces).append(pluginPrefix()).append(spaces).append(underscores)
                .append(newline())
                .append(text("Examples: ").color(NamedTextColor.GOLD))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("/statistic animals_bred top").color(accentColor))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("/statistic mine_block diorite me").color(accentColor))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("/statistic deaths player Artemis_the_gr8").color(accentColor))
                .append(newline());
    }

    public TextComponent formatPlayerStat(String playerName, String statName, String subStatEntryName, int stat) {
        TextComponent.Builder singleStat = Component.text();

        singleStat.append(playerNameComponent(Query.PLAYER, playerName + ": "))
                .append(statNumberComponent(Query.PLAYER, stat))
                .append(space())
                .append(statNameComponent(Query.PLAYER, statName))
                .append(space())
                .append(subStatNameComponent(Query.PLAYER, subStatEntryName));

        return singleStat.build();
    }

    public TextComponent formatTopStats(LinkedHashMap<String, Integer> topStats, String statName, String subStatEntryName) {
        TextComponent.Builder topList = Component.text();

        topList.append(newline()).append(pluginPrefix())
                .append(titleComponent(Query.TOP, "Top")).append(space())
                .append(titleNumberComponent(topStats.size())).append(space())
                .append(statNameComponent(Query.TOP, statName)).append(space())
                .append(subStatNameComponent(Query.TOP, subStatEntryName));

        boolean useDots = config.useDots();
        Set<String> playerNames = topStats.keySet();
        MinecraftFont font = new MinecraftFont();

        int count = 0;
        for (String playerName : playerNames) {
            count = count+1;

            topList.append(newline())
                    .append(rankingNumberComponent(count + ". "))
                    .append(playerNameComponent(Query.TOP, playerName));

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
                topList.append(playerNameComponent(Query.TOP, ":"));
            }
            topList.append(space()).append(statNumberComponent(Query.TOP, topStats.get(playerName)));
        }
        return topList.build();
    }

    public TextComponent formatServerStat(String statName, String subStatEntry, int stat) {
        TextComponent.Builder serverStat = Component.text();
        serverStat.append(titleComponent(Query.SERVER, config.getServerTitle()))
                .append(space())
                .append(serverNameComponent())
                .append(space())
                .append(statNumberComponent(Query.SERVER, stat))
                .append(space())
                .append(statNameComponent(Query.SERVER, statName))
                .append(space())
                .append(subStatNameComponent(Query.SERVER, subStatEntry));

        return  serverStat.build();
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

    private TextComponent playerNameComponent(Query selection, String playerName) {
        return getComponent(playerName,
                getColorFromString(config.getPlayerNameFormatting(selection, false)),
                getStyleFromString(config.getPlayerNameFormatting(selection, true)));
    }

    private TextComponent statNameComponent(Query selection, String statName) {
        return getComponent(statName.toLowerCase().replace("_", " "),
                getColorFromString(config.getStatNameFormatting(selection, false)),
                getStyleFromString(config.getStatNameFormatting(selection, true)));
    }

    private TextComponent subStatNameComponent(Query selection, String subStatName) {
        if (subStatName == null) {
            return empty();
        }
        else {
            return getComponent("(" + subStatName.toLowerCase().replace("_", " ") + ")",
                    getColorFromString(config.getSubStatNameFormatting(selection, false)),
                    getStyleFromString(config.getSubStatNameFormatting(selection, true)))
                    .append(space());
        }
    }

    private TextComponent statNumberComponent(Query selection, int number) {
        return getComponent(number + "",
                getColorFromString(config.getStatNumberFormatting(selection, false)),
                getStyleFromString(config.getStatNumberFormatting(selection, true)));
    }

    private TextComponent titleComponent(Query selection, String content) {
        return getComponent(content,
                getColorFromString(config.getTitleFormatting(selection, false)),
                getStyleFromString(config.getTitleFormatting(selection, true)));
    }

    private TextComponent titleNumberComponent(int number) {
        return getComponent(number + "",
                getColorFromString(config.getTitleNumberFormatting(false)),
                getStyleFromString(config.getTitleNumberFormatting(true)));
    }

    private TextComponent serverNameComponent() {
        TextComponent colon = text(":").color(getColorFromString(config.getServerNameFormatting(false)));
        return getComponent(config.getServerName(),
                getColorFromString(config.getServerNameFormatting(false)),
                getStyleFromString(config.getServerNameFormatting(true)))
                .append(colon);
    }

    private TextComponent rankingNumberComponent(String number) {
        return getComponent(number,
                getColorFromString(config.getRankNumberFormatting(false)),
                getStyleFromString(config.getRankNumberFormatting(true)));
    }

    private TextComponent dotsComponent(String dots) {
        return getComponent(dots,
                getColorFromString(config.getDotsFormatting(false)),
                getStyleFromString(config.getDotsFormatting(true)));
    }

    private TextComponent getComponent(String content, TextColor color, @Nullable TextDecoration style) {
        return style == null ? text(content).color(color) : text(content).color(color).decoration(style, TextDecoration.State.TRUE);
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

    private TextColor getTextColorByName(String textColor) {
        Index<String, NamedTextColor> names = NamedTextColor.NAMES;
        return names.value(textColor);
    }

    private @Nullable TextDecoration getStyleFromString(String configString) {
        if (configString.equalsIgnoreCase("none")) {
            return null;
        }
        else if (configString.equalsIgnoreCase("magic")) {
            return TextDecoration.OBFUSCATED;
        }
        else {
            Index<String, TextDecoration> styles = TextDecoration.NAMES;
            return styles.value(configString);
        }
    }

    //returns the usage-explanation with hovering text
    private TextComponent helpMsgHover() {
        TextComponent spaces = text("    "); //4 spaces
        TextComponent underscores = text("____________").color(TextColor.fromHexString("#6E3485")); //12 underscores
        TextComponent arrow = text("→ ").color(NamedTextColor.GOLD);  //alt + 26
        TextColor arguments = NamedTextColor.YELLOW;

        return Component.newline()
                .append(underscores).append(spaces).append(pluginPrefix()).append(spaces).append(underscores)
                .append(newline())
                .append(text("Hover over the arguments for more information!").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                .append(newline())
                .append(text("Usage: ").color(NamedTextColor.GOLD)).append(text("/statistic").color(arguments))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("name").color(arguments)
                        .hoverEvent(HoverEvent.showText(text("The name that describes the statistic").color(hoverBaseColor)
                                .append(newline())
                                .append(text("Example: ").color(accentColor1))
                                .append(text("\"animals_bred\"").color(accentColor2)))))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("sub-statistic").color(arguments)
                        .hoverEvent(HoverEvent.showText(
                                text("Some statistics need an item, block or entity as extra input").color(hoverBaseColor)
                                        .append(newline())
                                        .append(text("Example: ").color(accentColor1)
                                                .append(text("\"mine_block diorite\"").color(accentColor2))))))
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
                .append(text("server").color(arguments)
                        .hoverEvent(HoverEvent.showText(
                                text("See the combined total for everyone on your server").color(hoverBaseColor))))
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
                                        .append(text("\"player\"").color(accentColor2)
                                                .append(text(", add the player's name").color(hoverBaseColor))))));
    }

    //returns the usage-explanation without any hovering text
    private TextComponent helpMsgPlain(boolean isConsoleSender) {
        TextComponent underscores = text("____________").color(TextColor.fromHexString("#6E3485")); //12 underscores
        TextComponent spaces = text("    "); //4 spaces
        TextComponent arrow = text("→ ").color(NamedTextColor.GOLD); //alt + 26;
        TextComponent bullet = text("• ").color(NamedTextColor.GOLD); //alt + 7
        TextColor arguments = NamedTextColor.YELLOW;
        TextColor accentColor = accentColor2;

        if (isConsoleSender) {
            arrow = text("-> ").color(NamedTextColor.GOLD);
            bullet = text("* ").color(NamedTextColor.GOLD);
            accentColor = NamedTextColor.GOLD;
        }

        return Component.newline()
                .append(underscores).append(spaces).append(pluginPrefix()).append(spaces).append(underscores)
                .append(newline())
                .append(text("Type \"/statistic examples\" to see examples!").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                .append(newline())
                .append(text("Usage: ").color(NamedTextColor.GOLD))
                .append(text("/statistic").color(arguments))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("name").color(arguments))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("{sub-statistic}").color(arguments))
                .append(space())
                .append(text("(a block, item or entity)").color(NamedTextColor.GRAY))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("me | player | server | top").color(arguments))
                .append(newline())
                .append(spaces).append(spaces).append(bullet)
                .append(text("me:").color(accentColor))
                .append(space()).append(text("your own statistic").color(NamedTextColor.GRAY))
                .append(newline())
                .append(spaces).append(spaces).append(bullet)
                .append(text("player:").color(accentColor))
                .append(space()).append(text("choose a player").color(NamedTextColor.GRAY))
                .append(newline())
                .append(spaces).append(spaces).append(bullet)
                .append(text("server:").color(accentColor))
                .append(space()).append(text("everyone on the server combined").color(NamedTextColor.GRAY))
                .append(newline())
                .append(spaces).append(spaces).append(bullet)
                .append(text("top:").color(accentColor))
                .append(space()).append(text("the top").color(NamedTextColor.GRAY)
                        .append(space()).append(text(config.getTopListMaxSize())))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("{player-name}").color(arguments));
    }
}