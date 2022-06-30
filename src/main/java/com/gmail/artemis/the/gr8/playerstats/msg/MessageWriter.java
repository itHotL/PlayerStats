package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.enums.PluginColor;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.map.MinecraftFont;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.kyori.adventure.text.Component.*;

/** Composes messages to send to Players or Console. This class is responsible
 for constructing a final Component with the text content of the desired message.
 The component parts (with appropriate formatting) are supplied by a ComponentFactory.*/
public class MessageWriter {

    private static ConfigHandler config;
    private static ComponentFactory componentFactory;

    public MessageWriter(ConfigHandler c) {
        config = c;
        getComponentFactory();
    }

    public static void updateComponentFactory() {
        getComponentFactory();
    }

    private static void getComponentFactory() {
        if (config.useFestiveFormatting() || config.useRainbowMode()) {
            componentFactory = new PrideComponentFactory(config);
        }
        else {
            componentFactory = new ComponentFactory(config);
        }
    }

    public TextComponent reloadedConfig(boolean isBukkitConsole) {
        return componentFactory.msg(
                "Config reloaded!", isBukkitConsole);
    }

    public TextComponent stillReloading(boolean isBukkitConsole) {
        return componentFactory.msg(
                "The plugin is (re)loading, " +
                        "your request will be processed when it is done!", isBukkitConsole);
    }

    public TextComponent waitAMoment(boolean longWait, boolean isBukkitConsole) {
        String msg = longWait ? "Calculating statistics, this may take a minute..." :
                "Calculating statistics, this may take a few moments...";
        return componentFactory.msg(msg, isBukkitConsole);
    }

    public TextComponent missingStatName(boolean isBukkitConsole) {
        return componentFactory.msg(
                "Please provide a valid statistic name!", isBukkitConsole);
    }

    public TextComponent missingSubStatName(Statistic.Type statType, boolean isBukkitConsole) {
        return componentFactory.msg(
                "Please add a valid " +
                        getSubStatTypeName(statType) +
                        " to look up this statistic!", isBukkitConsole);
    }

    public TextComponent missingPlayerName(boolean isBukkitConsole) {
        return componentFactory.msg(
                "Please specify a valid player-name!", isBukkitConsole);
    }

    public TextComponent wrongSubStatType(Statistic.Type statType, String subStatEntry, boolean isBukkitConsole) {
        return componentFactory.msg(
                "\"" + subStatEntry + "\" is not a valid " + getSubStatTypeName(statType) + "!", isBukkitConsole);
    }

    public TextComponent unknownError(boolean isBukkitConsole) {
        return componentFactory.msg(
                "Something went wrong with your request, " +
                        "please try again or see /statistic for a usage explanation!", isBukkitConsole);
    }


    public TextComponent formatPlayerStat(int stat, @NotNull StatRequest request) {
        if (!request.isValid()) return unknownError(request.isBukkitConsoleSender());
        return Component.text()
                .append(componentFactory.playerName( request.getPlayerName() + ": ", Target.PLAYER))
                .append(componentFactory.statNumber(stat, request.getStatistic(), Target.PLAYER))
                .append(space())
                .append(componentFactory.statName(request))
                .append(space())
                .build();
    }

    public TextComponent formatTopStats(@NotNull LinkedHashMap<String, Integer> topStats, @NotNull StatRequest request) {
        if (!request.isValid()) return unknownError(request.isBukkitConsoleSender());

        TextComponent.Builder topList = Component.text()
                .append(newline())
                .append(componentFactory.pluginPrefix(request.isBukkitConsoleSender()))
                .append(componentFactory.title(config.getTopStatsTitle(), Target.TOP))
                .append(space())
                .append(componentFactory.titleNumber(topStats.size()))
                .append(space())
                .append(componentFactory.statName(request));

        boolean useDots = config.useDots();
        boolean boldNames = config.playerNameIsBold();

        Set<String> playerNames = topStats.keySet();
        MinecraftFont font = new MinecraftFont();

        int count = 0;
        for (String playerName : playerNames) {
            count = count+1;

            topList.append(newline())
                    .append(componentFactory.rankingNumber(count + ". "))
                    .append(componentFactory.playerName(playerName, Target.TOP));

            if (useDots) {
                topList.append(space());

                int dots = (int) Math.round((130.0 - font.getWidth(count + ". " + playerName))/2);
                if (request.isConsoleSender()) {
                    dots = (int) Math.round((130.0 - font.getWidth(count + ". " + playerName))/6) + 7;
                }
                else if (boldNames) {
                    dots = (int) Math.round((130.0 - font.getWidth(count + ". ") - (font.getWidth(playerName) * 1.19))/2);
                }
                if (dots >= 1) {
                    topList.append(componentFactory.dots(".".repeat(dots)));
                }
            }
            else {
                topList.append(componentFactory.playerName(":", Target.TOP));
            }
            topList.append(space()).append(componentFactory.statNumber(topStats.get(playerName), request.getStatistic(), Target.TOP));
        }
        return topList.build();
    }

    public TextComponent formatServerStat(long stat, @NotNull StatRequest request) {
        if (!request.isValid()) return unknownError(request.isBukkitConsoleSender());
        return Component.text()
                .append(componentFactory.title(config.getServerTitle(), Target.SERVER))
                .append(space())
                .append(componentFactory.serverName(config.getServerName()))
                .append(space())
                .append(componentFactory.statNumber(stat, request.getStatistic(), Target.SERVER))
                .append(space())
                .append(componentFactory.statName(request))
                .append(space())
                .build();
    }

    /** Returns "block", "entity", "item", or "sub-statistic" if the provided Type is null. */
    private String getSubStatTypeName(Statistic.Type statType) {
        String subStat = "sub-statistic";
        if (statType == null) return subStat;
        switch (statType) {
            case BLOCK -> subStat = "block";
            case ENTITY -> subStat = "entity";
            case ITEM -> subStat = "item";
        }
        return subStat;
    }

    public TextComponent usageExamples(boolean isBukkitConsole) {
        TextColor mainColor = isBukkitConsole ? PluginColor.GOLD.getConsoleColor() : PluginColor.GOLD.getColor();
        TextColor accentColor1 = isBukkitConsole ? PluginColor.MEDIUM_GOLD.getConsoleColor() : PluginColor.MEDIUM_GOLD.getColor();
        TextColor accentColor3 = isBukkitConsole ? PluginColor.LIGHT_YELLOW.getConsoleColor() : PluginColor.LIGHT_YELLOW.getColor();
        String arrow = isBukkitConsole ? "    -> " : "    → ";  //4 spaces, alt + 26, 1 space

        return Component.newline()
                .append(componentFactory.prefixTitle(isBukkitConsole))
                .append(newline())
                .append(text("Examples: ").color(mainColor))
                .append(newline())
                .append(text(arrow).color(mainColor)
                        .append(text("/statistic ")
                                .append(text("animals_bred ").color(accentColor1)
                                        .append(text("top").color(accentColor3)))))
                .append(newline())
                .append(text(arrow).color(mainColor)
                        .append(text("/statistic ")
                                .append(text("mine_block diorite ").color(accentColor1)
                                        .append(text("me").color(accentColor3)))))
                .append(newline())
                .append(text(arrow).color(mainColor)
                        .append(text("/statistic ")
                                .append(text("deaths ").color(accentColor1)
                                        .append(text("player ").color(accentColor3)
                                                .append(text("Artemis_the_gr8"))))));
    }

    public TextComponent helpMsg(boolean isConsoleSender) {
        if (isConsoleSender || !config.useHoverText()) {
            return helpMsgPlain(isConsoleSender && Bukkit.getName().equalsIgnoreCase("CraftBukkit"));
        }
        else {
            return helpMsgHover();
        }
    }

    /** Returns the usage-explanation with hovering text */
    private TextComponent helpMsgHover() {
        String arrow = "    →";  //4 spaces, alt + 26
        return Component.newline()
                .append(componentFactory.prefixTitle(false))
                .append(newline())
                .append(componentFactory.subTitle("Hover over the arguments for more information!"))
                .append(newline())
                .append(componentFactory.msgPart("Usage:", null, "/statistic", null))
                .append(newline())
                .append(componentFactory.msgPart(arrow, null, null, null)
                        .append(componentFactory.complexHoverPart("name", PluginColor.LIGHT_GOLD,
                                "The name that describes the statistic",
                                "Example:",
                                "\"animals_bred\"")))
                .append(newline())
                .append(componentFactory.msgPart(arrow, null, null, null)
                        .append(componentFactory.complexHoverPart("sub-statistic", PluginColor.LIGHT_GOLD,
                                "Some statistics need an item, block or entity as extra input",
                                "Example:",
                                "\"mine_block diorite\"")))
                .append(newline())
                .append(text("    ").color(PluginColor.LIGHT_GOLD.getColor())
                        .append(componentFactory.simpleHoverPart(
                                "→", PluginColor.GOLD,
                                "Choose one", PluginColor.DARK_PURPLE))
                        .append(space())
                        .append(componentFactory.simpleHoverPart(
                                "me",
                                "See your own statistic", PluginColor.LIGHT_BLUE))
                        .append(text(" | "))
                        .append(componentFactory.simpleHoverPart(
                                "player",
                                "Choose any player that has played on your server", PluginColor.LIGHT_BLUE))
                        .append(text(" | "))
                        .append(componentFactory.simpleHoverPart(
                                "server",
                                "See the combined total for everyone on your server", PluginColor.LIGHT_BLUE))
                        .append(text(" | "))
                        .append(componentFactory.simpleHoverPart(
                                "top",
                                "See the top " + config.getTopListMaxSize(), PluginColor.LIGHT_BLUE)))
                .append(newline())
                .append(componentFactory.msgPart(arrow, null, null, null)
                        .append(text("player-name").color(PluginColor.LIGHT_GOLD.getColor())
                                .hoverEvent(HoverEvent.showText(
                                        text("In case you typed ").color(PluginColor.LIGHT_BLUE.getColor())
                                                .append(text("\"player\"").color(PluginColor.MEDIUM_GOLD.getColor()))
                                                .append(text(", add the player's name"))))));
    }

    /** Returns the usage-explanation without any hovering text.
    If BukkitVersion is CraftBukkit, this doesn't use unicode symbols or hex colors */
    private TextComponent helpMsgPlain(boolean isBukkitConsole) {
        String arrow = isBukkitConsole ? "    ->" : "    →";  //4 spaces, alt + 26
        String bullet = isBukkitConsole ? "        *" : "        •";  //8 spaces, alt + 7
        return Component.newline()
                .append(componentFactory.prefixTitle(isBukkitConsole))
                .append(newline())
                .append(componentFactory.subTitle("Type \"statistic examples\" to see examples!"))
                .append(newline())
                .append(componentFactory.msgPart("Usage:", null, "/statistic", null, isBukkitConsole))
                .append(newline())
                .append(componentFactory.msgPart(arrow, null, "name", null, isBukkitConsole))
                .append(newline())
                .append(componentFactory.msgPart(arrow, null, "{sub-statistic}", "(a block, item or entity)", isBukkitConsole))
                .append(newline())
                .append(componentFactory.msgPart(arrow, null, "me | player | server | top", null, isBukkitConsole))
                .append(newline())
                .append(componentFactory.msgPart(bullet, "me:", null, "your own statistic", isBukkitConsole))
                .append(newline())
                .append(componentFactory.msgPart(bullet, "player:", null, "choose a player", isBukkitConsole))
                .append(newline())
                .append(componentFactory.msgPart(bullet, "server:", null, "everyone on the server combined", isBukkitConsole))
                .append(newline())
                .append(componentFactory.msgPart(bullet, "top:", null, "the top " + config.getTopListMaxSize(), isBukkitConsole))
                .append(newline())
                .append(componentFactory.msgPart(arrow, null, "{player-name}", null, isBukkitConsole));
    }
}