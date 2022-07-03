package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.enums.Unit;
import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.ExampleMessage;
import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.HelpMessage;
import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.LanguageKeyHandler;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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
    private final LanguageKeyHandler languageKeyHandler;
    private final NumberFormatter formatter;

    public MessageWriter(ConfigHandler c) {
        config = c;
        formatter = new NumberFormatter();
        languageKeyHandler = new LanguageKeyHandler();
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
        return componentFactory.pluginPrefixComponent(isBukkitConsole)
                .append(space())
                .append(componentFactory.messageComponent().content("Config reloaded!"));
    }

    public TextComponent stillReloading(boolean isBukkitConsole) {
        return componentFactory.pluginPrefixComponent(isBukkitConsole)
                .append(space())
                .append(componentFactory.messageComponent().content(
                "The plugin is (re)loading, your request will be processed when it is done!"));
    }

    public TextComponent waitAMoment(boolean longWait, boolean isBukkitConsole) {
        String msg = longWait ? "Calculating statistics, this may take a minute..." :
                "Calculating statistics, this may take a few moments...";
        return componentFactory.pluginPrefixComponent(isBukkitConsole)
                .append(space())
                .append(componentFactory.messageComponent().content(msg));
    }

    public TextComponent missingStatName(boolean isBukkitConsole) {
        return componentFactory.pluginPrefixComponent(isBukkitConsole)
                .append(space())
                .append(componentFactory.messageComponent().content(
                "Please provide a valid statistic name!"));
    }

    public TextComponent missingSubStatName(Statistic.Type statType, boolean isBukkitConsole) {
        return componentFactory.pluginPrefixComponent(isBukkitConsole)
                .append(space())
                .append(componentFactory.messageComponent().content(
                "Please add a valid " + getSubStatTypeName(statType) + " to look up this statistic!"));
    }

    public TextComponent missingPlayerName(boolean isBukkitConsole) {
        return componentFactory.pluginPrefixComponent(isBukkitConsole)
                .append(space())
                .append(componentFactory.messageComponent().content(
                "Please specify a valid player-name!"));
    }

    public TextComponent wrongSubStatType(Statistic.Type statType, String subStatEntry, boolean isBukkitConsole) {
        return componentFactory.pluginPrefixComponent(isBukkitConsole)
                .append(space())
                .append(componentFactory.messageComponent().content(
                "\"" + subStatEntry + "\" is not a valid " + getSubStatTypeName(statType) + "!"));
    }

    public TextComponent unknownError(boolean isBukkitConsole) {
        return componentFactory.pluginPrefixComponent(isBukkitConsole)
                .append(space())
                .append(componentFactory.messageComponent().content(
                "Something went wrong with your request, " +
                        "please try again or see /statistic for a usage explanation!"));
    }

    public TextComponent formatPlayerStat(int stat, @NotNull StatRequest request) {
        return Component.text()
                .append(componentFactory.playerNameBuilder(request.getPlayerName(), Target.PLAYER)
                        .append(text(":"))
                        .append(space()))
                .append(getStatNumberComponent(request.getStatistic(), stat, Target.PLAYER))
                .append(space())
                .append(getStatNameComponent(request))
                .append(getStatUnitComponent(request.getStatistic(), request.getSelection()))
                .build();
    }

    public TextComponent formatTopStats(@NotNull LinkedHashMap<String, Integer> topStats, @NotNull StatRequest request) {
        TextComponent.Builder topList = Component.text()
                .append(newline())
                .append(componentFactory.pluginPrefixComponent(request.isBukkitConsoleSender())).append(space())
                .append(componentFactory.titleComponent(config.getTopStatsTitle(), Target.TOP)).append(space())
                .append(componentFactory.titleNumberComponent(topStats.size())).append(space())
                .append(getStatNameComponent(request))
                .append(getStatUnitComponent(request.getStatistic(), request.getSelection()));

        boolean useDots = config.useDots();
        boolean boldNames = config.playerNameIsBold();

        Set<String> playerNames = topStats.keySet();
        MinecraftFont font = new MinecraftFont();

        int count = 0;
        for (String playerName : playerNames) {
            TextComponent.Builder playerNameBuilder = componentFactory.playerNameBuilder(playerName, Target.TOP);
            count = count+1;

            topList.append(newline())
                    .append(componentFactory.rankingNumberComponent(count + ". "))
                    .append(playerNameBuilder);

            if (useDots) {
                topList.append(space());
                TextComponent.Builder dotsBuilder = componentFactory.dotsBuilder();
                int dots;
                if (request.isConsoleSender()) {
                    dots = (int) Math.round((130.0 - font.getWidth(count + ". " + playerName))/6) + 7;
                } else if (!boldNames) {
                    dots = (int) Math.round((130.0 - font.getWidth(count + ". " + playerName))/2);
                } else {
                    dots = (int) Math.round((130.0 - font.getWidth(count + ". ") - (font.getWidth(playerName) * 1.19))/2);
                }
                if (dots >= 1) {
                    topList.append(dotsBuilder
                            .append(text((".".repeat(dots)))));
                }
            } else {
                topList.append(playerNameBuilder
                        .append(text(":")));
            }
            topList.append(space())
                    .append(getStatNumberComponent(request.getStatistic(), topStats.get(playerName), Target.TOP));

        }
        return topList.build();
    }

    public TextComponent formatServerStat(long stat, @NotNull StatRequest request) {
        return Component.text()
                .append(componentFactory.titleComponent(config.getServerTitle(), Target.SERVER))
                .append(space())
                .append(componentFactory.serverNameComponent(config.getServerName()))
                .append(space())
                .append(getStatNumberComponent(request.getStatistic(), stat, Target.SERVER))
                .append(space())
                .append(getStatNameComponent(request))
                .append(getStatUnitComponent(request.getStatistic(), request.getSelection()))  //space is provided by statUnit
                .build();
    }

    /** Depending on the config settings, return either a TranslatableComponent representing
     the statName (and potential subStatName), or a TextComponent with capitalized English names.*/
    private TextComponent getStatNameComponent(StatRequest request) {
        if (config.useTranslatableComponents()) {
            String statKey = languageKeyHandler.getStatKey(request.getStatistic());
            String subStatKey = request.getSubStatEntry();
            if (subStatKey != null) {
                switch (request.getStatistic().getType()) {
                    case BLOCK -> subStatKey = languageKeyHandler.getBlockKey(request.getBlock());
                    case ENTITY -> subStatKey = languageKeyHandler.getEntityKey(request.getEntity());
                    case ITEM -> subStatKey = languageKeyHandler.getItemKey(request.getItem());
                    default -> {
                    }
                }
            }
            return componentFactory.statNameTransComponent(statKey, subStatKey, request.getSelection());
        }
        else {
            return componentFactory.statNameTextComponent(
                    getPrettyName(request.getStatistic().toString()),
                    getPrettyName(request.getSubStatEntry()),
                    request.getSelection());
        }
    }

    private TextComponent getStatNumberComponent(Statistic statistic, long statNumber, Target selection) {
        Unit.Type type = Unit.fromStatistic(statistic);
        Unit statUnit;
        switch (type) {
            case DISTANCE -> statUnit = Unit.fromString(config.getDistanceUnit(false));
            case DAMAGE -> statUnit = Unit.fromString(config.getDamageUnit(false));
            case TIME -> {
                return getTimeNumberComponent(statNumber, selection);
            }
            default -> statUnit = Unit.NUMBER;
        }
        String prettyNumber = formatter.format(statNumber, statUnit);
        if (!config.useHoverText() || statUnit == Unit.NUMBER) {
            return componentFactory.statNumberComponent(prettyNumber, selection).build();
        }
        Unit hoverUnit = type == Unit.Type.DISTANCE ? Unit.fromString(config.getDistanceUnit(true)) :
                Unit.fromString(config.getDamageUnit(true));
        String prettyHoverNumber = formatter.format(statNumber, hoverUnit);
        if (config.useTranslatableComponents()) {
            String unitKey = languageKeyHandler.getUnitKey(hoverUnit);
            if (unitKey == null) {
                unitKey = hoverUnit.getName();
            }
            return componentFactory.statNumberHoverComponent(prettyNumber, prettyHoverNumber, null, unitKey, selection);
        }
        else {
            return componentFactory.statNumberHoverComponent(prettyNumber, prettyHoverNumber, hoverUnit.getName(), null, selection);
        }
    }

    private TextComponent getTimeNumberComponent(long statNumber, Target selection) {
        Unit max = Unit.fromString(config.getTimeUnit(false));
        Unit min = Unit.fromString(config.getTimeUnit(false, true));
        String mainNumber = formatter.format(statNumber, max, min);
        if (!config.useHoverText()) {
            return componentFactory.statNumberComponent(mainNumber, selection).build();
        } else {
            Unit hoverMax = Unit.fromString(config.getTimeUnit(true));
            Unit hoverMin = Unit.fromString(config.getTimeUnit(true, true));
            return componentFactory.statNumberHoverComponent(mainNumber,
                    formatter.format(statNumber, hoverMax, hoverMin),
                    null, null, selection);  //Time does not support translatable text,
        }                                                            //because the unit and number are so tightly interwoven.
    }

    private TextComponent getStatUnitComponent(Statistic statistic, Target selection) {
        Unit statUnit;
        switch (Unit.fromStatistic(statistic)) {
            case DAMAGE -> statUnit = Unit.fromString(config.getDamageUnit(false));
            case DISTANCE -> statUnit = Unit.fromString(config.getDistanceUnit(false));
            default -> {
                return Component.empty();
            }
        }
        if (config.useTranslatableComponents()) {
            String unitKey = languageKeyHandler.getUnitKey(statUnit);
            if (unitKey != null) {
                return Component.space()
                        .append(componentFactory.statUnitComponent(null, unitKey, selection));
            }
        }
        return Component.space()
                .append(componentFactory.statUnitComponent(statUnit.getName(), null, selection));
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

    /** Replace "_" with " " and capitalize each first letter of the input.
     @param input String to prettify, case-insensitive*/
    private String getPrettyName(String input) {
        if (input == null) return null;
        StringBuilder capitals = new StringBuilder(input.toLowerCase());
        capitals.setCharAt(0, Character.toUpperCase(capitals.charAt(0)));
        while (capitals.indexOf("_") != -1) {
            MyLogger.replacingUnderscores();

            int index = capitals.indexOf("_");
            capitals.setCharAt(index + 1, Character.toUpperCase(capitals.charAt(index + 1)));
            capitals.setCharAt(index, ' ');
        }
        return capitals.toString();
    }

    public TextComponent usageExamples(boolean isBukkitConsole) {
        return new ExampleMessage(componentFactory, isBukkitConsole);
    }

    public TextComponent helpMsg(boolean isConsoleSender) {
        return new HelpMessage(componentFactory,
                config.useHoverText() && !isConsoleSender,
                isConsoleSender && Bukkit.getName().equalsIgnoreCase("CraftBukkit"),
                config.getTopListMaxSize());
    }
}