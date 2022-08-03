package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.enums.DebugLevel;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.enums.Unit;

import com.gmail.artemis.the.gr8.playerstats.statistic.request.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.msg.components.ComponentFactory;
import com.gmail.artemis.the.gr8.playerstats.msg.components.ExampleMessage;
import com.gmail.artemis.the.gr8.playerstats.msg.components.HelpMessage;
import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.*;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;

import static net.kyori.adventure.text.Component.*;

/** Composes messages to send to a Player or Console. This class is responsible
 for constructing a final Component with the text content of the desired message.
 The component parts (with appropriate formatting) are supplied by a {@link ComponentFactory}.
 By default, this class works with the default ComponentFactory, but you can
 give it a different ComponentFactory upon creation.*/
public final class MessageBuilder {

    private static ConfigHandler config;
    private boolean useHoverText;
    private boolean isConsoleBuilder;

    private final ComponentFactory componentFactory;
    private final LanguageKeyHandler languageKeyHandler;
    private final NumberFormatter formatter;

    private MessageBuilder(ConfigHandler config) {
        this (config, new ComponentFactory(config));
    }

    private MessageBuilder(ConfigHandler configHandler, ComponentFactory factory) {
        config = configHandler;
        useHoverText = config.useHoverText();
        componentFactory = factory;

        formatter = new NumberFormatter();
        languageKeyHandler = new LanguageKeyHandler();
        MyLogger.logMsg("MessageBuilder created with factory: " + componentFactory.getClass().getSimpleName(), DebugLevel.MEDIUM);
    }

    public static MessageBuilder defaultBuilder(ConfigHandler config) {
        return new MessageBuilder(config);
    }

    public static MessageBuilder fromComponentFactory(ConfigHandler config, ComponentFactory factory) {
        return new MessageBuilder(config, factory);
    }

    /** Set whether this {@link MessageBuilder} should use hoverText.
     By default, this follows the setting specified in the {@link ConfigHandler}. */
    public void toggleHoverUse(boolean desiredSetting) {
        useHoverText = desiredSetting;
    }

    public void setConsoleBuilder(boolean isConsoleBuilder) {
        this.isConsoleBuilder = isConsoleBuilder;
    }

    public TextComponent reloadedConfig() {
        return componentFactory.pluginPrefix()
                .append(space())
                .append(componentFactory.message().content("Config reloaded!"));
    }

    public TextComponent stillReloading() {
        return componentFactory.pluginPrefix()
                .append(space())
                .append(componentFactory.message().content(
                "The plugin is (re)loading, your request will be processed when it is done!"));
    }

    public TextComponent waitAMoment(boolean longWait) {
        String msg = longWait ? "Calculating statistics, this may take a minute..." :
                "Calculating statistics, this may take a few moments...";
        return componentFactory.pluginPrefix()
                .append(space())
                .append(componentFactory.message().content(msg));
    }

    public TextComponent missingStatName() {
        return componentFactory.pluginPrefix()
                .append(space())
                .append(componentFactory.message().content(
                "Please provide a valid statistic name!"));
    }

    public TextComponent missingSubStatName(Statistic.Type statType) {
        return componentFactory.pluginPrefix()
                .append(space())
                .append(componentFactory.message().content(
                "Please add a valid " + getSubStatTypeName(statType) + " to look up this statistic!"));
    }

    public TextComponent missingPlayerName() {
        return componentFactory.pluginPrefix()
                .append(space())
                .append(componentFactory.message().content(
                "Please specify a valid player-name!"));
    }

    public TextComponent wrongSubStatType(Statistic.Type statType, String subStatName) {
        return componentFactory.pluginPrefix()
                .append(space())
                .append(componentFactory.messageAccent().content("\"" + subStatName + "\""))
                .append(space())
                .append(componentFactory.message().content(
                "is not a valid " + getSubStatTypeName(statType) + "!"));
    }

    public TextComponent requestAlreadyRunning() {
        return componentFactory.pluginPrefix()
                .append(space())
                .append(componentFactory.message().content(
                        "Please wait for your previous lookup to finish!"));
    }

    //TODO Make this say amount of time left
    public TextComponent stillOnShareCoolDown() {
        int waitTime = config.getStatShareWaitingTime();
        String minutes = waitTime == 1 ? " minute" : " minutes";

        return componentFactory.pluginPrefix()
                .append(space())
                .append(componentFactory.message().content("You need to wait")
                        .append(space())
                        .append(componentFactory.messageAccent()
                                .content(waitTime + minutes))
                        .append(space())
                        .append(text("between sharing!")));
    }

    public TextComponent resultsAlreadyShared() {
        return componentFactory.pluginPrefix()
                .append(space())
                .append(componentFactory.message().content("You already shared these results!"));
    }

    public TextComponent statResultsTooOld() {
        return componentFactory.pluginPrefix()
                .append(space())
                .append(componentFactory.message().content(
                        "It has been too long since you looked up this statistic, please repeat the original command!"));
    }

    public TextComponent unknownError() {
        return componentFactory.pluginPrefix()
                .append(space())
                .append(componentFactory.message().content(
                "Something went wrong with your request, " +
                        "please try again or see /statistic for a usage explanation!"));
    }

    public TextComponent usageExamples() {
        return ExampleMessage.construct(componentFactory);
    }

    public TextComponent helpMsg() {
        int listSize = config.getTopListMaxSize();
        if (!isConsoleBuilder && useHoverText) {
            return HelpMessage.constructHoverMsg(componentFactory, listSize);
        } else {
            return HelpMessage.constructPlainMsg(componentFactory, listSize);
        }
    }

    /** Returns a BiFunction for a player statistic. This BiFunction will return a statResult,
     the shape of which is determined by the 2 parameters the BiFunction gets.
     <p>- Integer shareCode: if a shareCode is provided, a clickable "share" button will be added.
     <br>- CommandSender sender: if a sender is provided, a signature with "shared by sender-name" will be added.</br>
     <br>- If both parameters are null, the statResult will be returned as is.</br>*/
    public BiFunction<Integer, CommandSender, TextComponent> formattedPlayerStatFunction(int stat, @NotNull StatRequest statRequest) {
        TextComponent playerStat = Component.text()
                .append(componentFactory.playerName(statRequest.getPlayerName(), Target.PLAYER)
                        .append(text(":"))
                        .append(space()))
                .append(getStatNumberComponent(statRequest, stat))
                .append(space())
                .append(getStatNameComponent(statRequest))
                .append(getStatUnitComponent(statRequest.getStatistic(), statRequest.getTarget()))  //space is provided by statUnitComponent
                .build();

        return getFormattingFunction(playerStat, Target.PLAYER);
    }

    /** Returns a BiFunction for a server statistic. This BiFunction will return a statResult,
     the shape of which is determined by the 2 parameters the BiFunction gets.
     <p>- Integer shareCode: if a shareCode is provided, a clickable "share" button will be added.
     <br>- CommandSender sender: if a sender is provided, a signature with "shared by sender-name" will be added.</br>
     <br>- If both parameters are null, the statResult will be returned as is.</br>*/
    public BiFunction<Integer, CommandSender, TextComponent> formattedServerStatFunction(long stat, @NotNull StatRequest statRequest) {
        TextComponent serverStat = text()
                .append(componentFactory.title(config.getServerTitle(), Target.SERVER))
                .append(space())
                .append(componentFactory.serverName(config.getServerName()))
                .append(space())
                .append(getStatNumberComponent(statRequest, stat))
                .append(space())
                .append(getStatNameComponent(statRequest))
                .append(getStatUnitComponent(statRequest.getStatistic(), statRequest.getTarget()))  //space is provided by statUnit
                .build();

        return getFormattingFunction(serverStat, Target.SERVER);
    }

    /** Returns a BiFunction for a top statistic. This BiFunction will return a statResult,
     the shape of which is determined by the 2 parameters the BiFunction gets.
     <p>- Integer shareCode: if a shareCode is provided, a clickable "share" button will be added.
     <br>- CommandSender sender: if a sender is provided, a signature with "shared by sender-name" will be added.</br>
     <br>- If both parameters are null, the statResult will be returned as is.</br>*/
    public BiFunction<Integer, CommandSender, TextComponent> formattedTopStatFunction(@NotNull LinkedHashMap<String, Integer> topStats, @NotNull StatRequest statRequest) {
        final TextComponent title = getTopStatsTitleComponent(statRequest, topStats.size());
        final TextComponent shortTitle = getTopStatDescription(statRequest, topStats.size());
        final TextComponent list = getTopStatListComponent(topStats, statRequest);
        final boolean useEnters = config.useEnters(Target.TOP, false);
        final boolean useEntersForShared = config.useEnters(Target.TOP, true);

        return (shareCode, sender) -> {
            TextComponent.Builder topBuilder = text();

            //if we're adding a share-button
            if (shareCode != null) {
                if (useEnters) {
                    topBuilder.append(newline());
                }
                topBuilder.append(title)
                            .append(space())
                            .append(componentFactory.shareButton(shareCode))
                        .append(list);
            }
            //if we're adding a "shared by" component
            else if (sender != null) {
                if (useEntersForShared) {
                    topBuilder.append(newline());
                }
                topBuilder.append(shortTitle)
                            .append(space())
                            .append(componentFactory.statResultInHoverText(text()
                                    .append(title)
                                    .append(list)
                                    .build()))
                        .append(newline())
                        .append(componentFactory.sharedByMessage(
                                getSharerNameComponent(sender)));
            }
            //if we're not adding a share-button or a "shared by" component
            else {
                if (useEnters) {
                    topBuilder.append(newline());
                }
                topBuilder.append(title)
                        .append(list);
            }
            return topBuilder.build();
        };
    }

    public TextComponent singleTopStatLine(int positionInTopList, String playerName, long statNumber, Unit statNumberUnit) {
        TextComponent.Builder topStatLineBuilder = Component.text()
                .append(space())
                .append(componentFactory.rankNumber(positionInTopList))
                .append(space());

        if (config.useDots()) {
            topStatLineBuilder.append(getPlayerNameWithDotsComponent(positionInTopList, playerName));
        } else {
            topStatLineBuilder.append(componentFactory.playerName(playerName + ":", Target.TOP));
        }
        //TODO add formatted number here
    }

    private Component getSharerNameComponent(CommandSender sender) {
        if (sender instanceof Player player) {
            Component senderName = EasterEggProvider.getPlayerName(player);
            if (senderName != null) {
                return senderName;
            }
        }
        return componentFactory.sharerName(sender.getName());
    }

    private TextComponent getTopStatsTitleComponent(StatRequest statRequest, int statListSize) {
        return Component.text()
                .append(componentFactory.pluginPrefix()).append(space())
                .append(componentFactory.title(config.getTopStatsTitle(), Target.TOP)).append(space())
                .append(componentFactory.titleNumber(statListSize)).append(space())
                .append(getStatNameComponent(statRequest))  //space is provided by statUnitComponent
                .append(getStatUnitComponent(statRequest.getStatistic(), statRequest.getTarget()))
                .build();
    }

    private TextComponent getTopStatDescription(StatRequest statRequest, int statListSize) {
        return Component.text()
                .append(componentFactory.title(config.getTopStatsTitle(), Target.TOP)).append(space())
                .append(componentFactory.titleNumber(statListSize)).append(space())
                .append(getStatNameComponent(statRequest))  //space is provided by statUnitComponent
                .build();
    }

    private TextComponent getTopStatListComponent(LinkedHashMap<String, Integer> topStats, StatRequest statRequest) {
        TextComponent.Builder topList = Component.text();
        Set<String> playerNames = topStats.keySet();
        boolean useDots = config.useDots();

        int count = 0;
        for (String playerName : playerNames) {
            topList.append(newline())
                    .append(space())
                    .append(componentFactory.rankNumber(++count))
                    .append(space());
            if (useDots) {
                topList.append(getPlayerNameWithDotsComponent(count, playerName));
            }
            else {
                topList.append(componentFactory.playerName(playerName + ":", Target.TOP));
            }
            topList.append(space()).append(getStatNumberComponent(statRequest, topStats.get(playerName)));
        }
        return topList.build();
    }

    private TextComponent getPlayerNameWithDotsComponent(int positionInTopList, String playerName) {
        int dots = FontUtils.getNumberOfDotsToAlign(positionInTopList + ". " + playerName, isConsoleBuilder, config.playerNameIsBold());

        TextComponent.Builder nameWithDots = Component.text()
                .append(componentFactory.playerName(playerName, Target.TOP))
                .append(space());
        if (dots >= 1) {
            nameWithDots.append(componentFactory.dots().append(text(".".repeat(dots))));
        }
        return nameWithDots.build();
    }

    /** Depending on the config settings, return either a TranslatableComponent representing
     the statName (and potential subStatName), or a TextComponent with capitalized English names.*/
    private TextComponent getStatNameComponent(StatRequest statRequest) {
        if (config.useTranslatableComponents()) {
            String statKey = languageKeyHandler.getStatKey(statRequest.getStatistic());
            String subStatKey = statRequest.getSubStatEntryName();
            if (subStatKey != null) {
                switch (statRequest.getStatistic().getType()) {
                    case BLOCK -> subStatKey = languageKeyHandler.getBlockKey(statRequest.getBlock());
                    case ENTITY -> subStatKey = languageKeyHandler.getEntityKey(statRequest.getEntity());
                    case ITEM -> subStatKey = languageKeyHandler.getItemKey(statRequest.getItem());
                    default -> {
                    }
                }
            }
            return componentFactory.statAndSubStatNameTranslatable(statKey, subStatKey, statRequest.getTarget());
        }
        else {
            return componentFactory.statAndSubStatName(
                    StringUtils.prettify(statRequest.getStatistic().toString()),
                    StringUtils.prettify(statRequest.getSubStatEntryName()),
                    statRequest.getTarget());
        }
    }

    private TextComponent getStatNumberComponent(StatRequest request, long statNumber) {
        Unit.Type statUnitType = Unit.getTypeFromStatistic(request.getStatistic());
        Target target = request.getTarget();
        return switch (statUnitType) {
            case DISTANCE -> getDistanceNumberComponent(statNumber, target);
            case DAMAGE -> getDamageNumberComponent(statNumber, target);
            case TIME -> getTimeNumberComponent(statNumber, target);
            default -> getDefaultNumberComponent(statNumber, target);
        };
    }

    private TextComponent getStatNumberComponent(Unit unit, Target target, long statNumber) {
        return switch (unit.getType()) {
            case DISTANCE ->
        }
    }

    private TextComponent getDistanceNumberComponent(long statNumber, Target target) {
        Unit statUnit = Unit.fromString(config.getDistanceUnit(false));
        String prettyNumber = formatter.formatDistanceNumber(statNumber, statUnit);
        if (!useHoverText) {
            return componentFactory.distanceNumber(prettyNumber, target);
        }

        Unit hoverUnit = Unit.fromString(config.getDistanceUnit(true));
        String hoverNumber = formatter.formatDistanceNumber(statNumber, hoverUnit);
        if (config.useTranslatableComponents()) {
            String unitKey = languageKeyHandler.getUnitKey(hoverUnit);
            if (unitKey != null) {
                return componentFactory.distanceNumberWithTranslatableHoverText(prettyNumber, hoverNumber, unitKey, target);
            }
        }
        return componentFactory.distanceNumberWithHoverText(prettyNumber, hoverNumber, hoverUnit.getLabel(), target);
    }

    private TextComponent getDamageNumberComponent(long statNumber, Target target) {
        Unit statUnit = Unit.fromString(config.getDamageUnit(false));
        String prettyNumber = formatter.formatDamageNumber(statNumber, statUnit);
        if (!useHoverText) {
            return componentFactory.damageNumber(prettyNumber, target);
        }

        Unit hoverUnit = Unit.fromString(config.getDamageUnit(true));
        String prettyHoverNumber = formatter.formatDamageNumber(statNumber, hoverUnit);
        if (hoverUnit == Unit.HEART) {
            return componentFactory.damageNumberWithHeartUnitInHoverText(prettyNumber, prettyHoverNumber, target);
        }
        return componentFactory.damageNumberWithHoverText(prettyNumber, prettyHoverNumber, hoverUnit.getLabel(), target);
    }

    private TextComponent getTimeNumberComponent(long statNumber, Target target) {
        ArrayList<Unit> unitRange = getTimeUnitRange(statNumber);
        if (unitRange.size() <= 1 || (useHoverText && unitRange.size() <= 3)) {
            MyLogger.logMsg(
                    "There is something wrong with the time-units you specified, please check your config!",
                    true);
            return componentFactory.statNumber("-", target);
        }
        else {
            String mainNumber = formatter.formatTimeNumber(statNumber, unitRange.get(0), unitRange.get(1));
            if (!useHoverText) {
                return componentFactory.statNumber(mainNumber, target);
            } else {
                String hoverNumber = formatter.formatTimeNumber(statNumber, unitRange.get(2), unitRange.get(3));
                MyLogger.logMsg("mainNumber: " + mainNumber + ", hoverNumber: " + hoverNumber, DebugLevel.HIGH);
                return componentFactory.statNumberWithHoverText(mainNumber, hoverNumber,
                        null, null, target);
            }
        }
    }

    private TextComponent getDefaultNumberComponent(long statNumber, Target target) {
        return componentFactory.statNumber(formatter.formatNumber(statNumber), target);
    }

    private TextComponent getStatUnitComponent(Statistic statistic, Target target) {
        return switch (Unit.getTypeFromStatistic(statistic)) {
            case DAMAGE -> getDamageUnit(target);
            case DISTANCE -> getDistanceUnit(target);
            default -> Component.empty();
        };
    }

    private TextComponent getDistanceUnit(Target target) {
        Unit statUnit = Unit.fromString(config.getDistanceUnit(false));
        if (config.useTranslatableComponents()) {
            String unitKey = languageKeyHandler.getUnitKey(statUnit);
            if (unitKey != null) {
                return Component.space()
                        .append(componentFactory.statUnitTranslatable(unitKey, target));
            }
        }
        return Component.space()
                .append(componentFactory.statUnit(statUnit.getLabel(), target));
    }

    private TextComponent getDamageUnit(Target target) {
        Unit statUnit = Unit.fromString(config.getDamageUnit(false));
        if (statUnit == Unit.HEART) {
            TextComponent heartUnit;
            if (isConsoleBuilder) {
                heartUnit = componentFactory.consoleHeart();
            } else if (useHoverText) {
                heartUnit = componentFactory.clientHeartWithHoverText();
            } else {
                heartUnit = componentFactory.clientHeart(false);
            }
            return Component.space()
                    .append(heartUnit);
        }
        return Component.space()
                .append(componentFactory.statUnit(statUnit.getLabel(), target));
    }


    private BiFunction<Integer, CommandSender, TextComponent> getFormattingFunction(@NotNull TextComponent statResult, Target target) {
        boolean useEnters = config.useEnters(target, false);
        boolean useEntersForShared = config.useEnters(target, true);

        return (shareCode, sender) -> {
            TextComponent.Builder statBuilder = text();

            //if we're adding a share-button
            if (shareCode != null) {
                if (useEnters) {
                    statBuilder.append(newline());
                }
                statBuilder.append(statResult)
                        .append(space())
                        .append(componentFactory.shareButton(shareCode));
            }
            //if we're adding a "shared by" component
            else if (sender != null) {
                if (useEntersForShared) {
                    statBuilder.append(newline());
                }
                statBuilder.append(statResult)
                        .append(newline())
                        .append(componentFactory.sharedByMessage(
                                getSharerNameComponent(sender)));
            }
            //if we're not adding a share-button or a "shared by" component
            else {
                if (useEnters) {
                    statBuilder.append(newline());
                }
                statBuilder.append(statResult);
            }
            return statBuilder.build();
        };
    }

    /** Get an ArrayList consisting of 2 or 4 timeUnits. The order of items is:
     <p>0. maxUnit</p>
     <p>1. minUnit</p>
     <p>2. maxHoverUnit</p>
     <p>3. minHoverUnit</p>*/
    private ArrayList<Unit> getTimeUnitRange(long statNumber) {
        ArrayList<Unit> unitRange = new ArrayList<>();
        if (!config.autoDetectTimeUnit(false)) {
            unitRange.add(Unit.fromString(config.getTimeUnit(false)));
            unitRange.add(Unit.fromString(config.getTimeUnit(false, true)));
        }
        else {
            Unit bigUnit = Unit.getMostSuitableUnit(Unit.Type.TIME, statNumber);
            unitRange.add(bigUnit);
            unitRange.add(bigUnit.getSmallerUnit(config.getNumberOfExtraTimeUnits(false)));
        }
        if (useHoverText) {
            if (!config.autoDetectTimeUnit(true)) {
                unitRange.add(Unit.fromString(config.getTimeUnit(true)));
                unitRange.add(Unit.fromString(config.getTimeUnit(true, true)));
            }
            else {
                Unit bigHoverUnit = Unit.getMostSuitableUnit(Unit.Type.TIME, statNumber);
                unitRange.add(bigHoverUnit);
                unitRange.add(bigHoverUnit.getSmallerUnit(config.getNumberOfExtraTimeUnits(true)));
            }
        }
        MyLogger.logMsg("total selected unitRange for this statistic: " + unitRange, DebugLevel.MEDIUM);
        return unitRange;
    }

    /** Returns "block", "entity", "item", or "sub-statistic" if the provided Type is null. */
    public static String getSubStatTypeName(Statistic.Type statType) {
        String subStat = "sub-statistic";
        if (statType == null) return subStat;
        switch (statType) {
            case BLOCK -> subStat = "block";
            case ENTITY -> subStat = "entity";
            case ITEM -> subStat = "item";
        }
        return subStat;
    }
}