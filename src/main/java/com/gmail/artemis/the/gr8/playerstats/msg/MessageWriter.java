package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.enums.DebugLevel;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.enums.Unit;

import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.*;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.function.BiFunction;

import static net.kyori.adventure.text.Component.*;

/** Composes messages to send to Players or Console. This class is responsible
 for constructing a final Component with the text content of the desired message.
 The component parts (with appropriate formatting) are supplied by a ComponentFactory.*/
public class MessageWriter {

    protected static ConfigHandler config;

    private static ComponentFactory componentFactory;
    private final LanguageKeyHandler languageKeyHandler;
    private final NumberFormatter formatter;

    public MessageWriter(ConfigHandler c) {
        config = c;
        formatter = new NumberFormatter();
        languageKeyHandler = new LanguageKeyHandler();
        getComponentFactory();
    }

    protected void getComponentFactory() {
       if (config.enableRainbowMode() ||
                (config.enableFestiveFormatting() && LocalDate.now().getMonth().equals(Month.JUNE))) {

           componentFactory = new PrideComponentFactory(config);
        }
        else {
            componentFactory = new ComponentFactory(config);
        }
    }

    public TextComponent reloadedConfig() {
        return componentFactory.pluginPrefixComponent()
                .append(space())
                .append(componentFactory.messageComponent().content("Config reloaded!"));
    }

    public TextComponent stillReloading() {
        return componentFactory.pluginPrefixComponent()
                .append(space())
                .append(componentFactory.messageComponent().content(
                "The plugin is (re)loading, your request will be processed when it is done!"));
    }

    public TextComponent waitAMoment(boolean longWait) {
        String msg = longWait ? "Calculating statistics, this may take a minute..." :
                "Calculating statistics, this may take a few moments...";
        return componentFactory.pluginPrefixComponent()
                .append(space())
                .append(componentFactory.messageComponent().content(msg));
    }

    public TextComponent missingStatName() {
        return componentFactory.pluginPrefixComponent()
                .append(space())
                .append(componentFactory.messageComponent().content(
                "Please provide a valid statistic name!"));
    }

    public TextComponent missingSubStatName(Statistic.Type statType) {
        return componentFactory.pluginPrefixComponent()
                .append(space())
                .append(componentFactory.messageComponent().content(
                "Please add a valid " + EnumHandler.getSubStatTypeName(statType) + " to look up this statistic!"));
    }

    public TextComponent missingPlayerName() {
        return componentFactory.pluginPrefixComponent()
                .append(space())
                .append(componentFactory.messageComponent().content(
                "Please specify a valid player-name!"));
    }

    public TextComponent wrongSubStatType(Statistic.Type statType, String subStatName) {
        return componentFactory.pluginPrefixComponent()
                .append(space())
                .append(componentFactory.messageAccentComponent().content(subStatName))
                .append(space())
                .append(componentFactory.messageComponent().content(
                "is not a valid " + EnumHandler.getSubStatTypeName(statType) + "!"));
    }

    public TextComponent requestAlreadyRunning() {
        return componentFactory.pluginPrefixComponent()
                .append(space())
                .append(componentFactory.messageComponent().content(
                        "Please wait for your previous lookup to finish!"));
    }

    //TODO Make this say amount of time left
    public TextComponent stillOnShareCoolDown() {
        int waitTime = config.getStatShareWaitingTime();
        String minutes = waitTime == 1 ? " minute" : " minutes";

        return componentFactory.pluginPrefixComponent()
                .append(space())
                .append(componentFactory.messageComponent().content("You need to wait")
                        .append(space())
                        .append(componentFactory.messageAccentComponent()
                                .content(waitTime + minutes))
                        .append(space())
                        .append(text("between sharing!")));
    }

    public TextComponent resultsAlreadyShared() {
        return componentFactory.pluginPrefixComponent()
                .append(space())
                .append(componentFactory.messageComponent().content("You already shared these results!"));
    }

    public TextComponent statResultsTooOld() {
        return componentFactory.pluginPrefixComponent()
                .append(space())
                .append(componentFactory.messageComponent().content(
                        "It has been too long since you looked up this statistic, please repeat the original look-up if you want to share it!"));
    }

    public TextComponent unknownError() {
        return componentFactory.pluginPrefixComponent()
                .append(space())
                .append(componentFactory.messageComponent().content(
                "Something went wrong with your request, " +
                        "please try again or see /statistic for a usage explanation!"));
    }

    public TextComponent usageExamples() {
        return new ExampleMessage(componentFactory);
    }

    public TextComponent helpMsg() {
        return new HelpMessage(componentFactory,
                config.useHoverText(),
                config.getTopListMaxSize());
    }

    public BiFunction<UUID, CommandSender, TextComponent> formattedPlayerStatFunction(int stat, @NotNull StatRequest request) {
        TextComponent playerStat = Component.text()
                .append(componentFactory.playerNameBuilder(request.getPlayerName(), Target.PLAYER)
                        .append(text(":"))
                        .append(space()))
                .append(getStatNumberComponent(request.getStatistic(), stat, Target.PLAYER, request.isConsoleSender()))
                .append(space())
                .append(getStatNameComponent(request))
                .append(getStatUnitComponent(request.getStatistic(), request.getSelection(), request.isConsoleSender()))  //space is provided by statUnitComponent
                .build();

        return getFormattingFunction(playerStat, Target.PLAYER);
    }

    public BiFunction<UUID, CommandSender, TextComponent> formattedServerStatFunction(long stat, @NotNull StatRequest request) {
        TextComponent serverStat = text()
                .append(componentFactory.titleComponent(config.getServerTitle(), Target.SERVER))
                .append(space())
                .append(componentFactory.serverNameComponent(config.getServerName()))
                .append(space())
                .append(getStatNumberComponent(request.getStatistic(), stat, Target.SERVER, request.isConsoleSender()))
                .append(space())
                .append(getStatNameComponent(request))
                .append(getStatUnitComponent(request.getStatistic(), request.getSelection(), request.isConsoleSender()))  //space is provided by statUnit
                .build();

        return getFormattingFunction(serverStat, Target.SERVER);
    }

    public BiFunction<UUID, CommandSender, TextComponent> formattedTopStatFunction(@NotNull LinkedHashMap<String, Integer> topStats, @NotNull StatRequest request) {
        final TextComponent title = getTopStatsTitle(request, topStats.size());
        final TextComponent shortTitle = getTopStatsTitleShort(request, topStats.size());
        final TextComponent list = getTopStatList(topStats, request);
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
                            .append(componentFactory.shareButtonComponent(shareCode))
                        .append(list);
            }
            //if we're adding a "shared by" component
            else if (sender != null) {
                if (useEntersForShared) {
                    topBuilder.append(newline());
                }
                topBuilder.append(shortTitle)
                            .append(space())
                            .append(componentFactory.hoveringStatResultComponent(text()
                                    .append(title)
                                    .append(list)
                                    .build()))
                        .append(newline())
                        .append(componentFactory.messageSharedComponent(
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

    private BiFunction<UUID, CommandSender, TextComponent> getFormattingFunction(@NotNull TextComponent statResult, Target selection) {
        boolean useEnters = config.useEnters(selection, false);
        boolean useEntersForShared = config.useEnters(selection, true);

        return (shareCode, sender) -> {
            TextComponent.Builder statBuilder = text();

            //if we're adding a share-button
            if (shareCode != null) {
                if (useEnters) {
                    statBuilder.append(newline());
                }
                statBuilder.append(statResult)
                        .append(space())
                        .append(componentFactory.shareButtonComponent(shareCode));
            }
            //if we're adding a "shared by" component
            else if (sender != null) {
                if (useEntersForShared) {
                    statBuilder.append(newline());
                }
                statBuilder.append(statResult)
                        .append(newline())
                        .append(componentFactory.messageSharedComponent(
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

    private Component getSharerNameComponent(CommandSender sender) {
        if (sender instanceof Player player) {
            Component senderName = EasterEggProvider.getPlayerName(player);
            if (senderName != null) {
                return senderName;
            }
        }
        return componentFactory.sharerNameComponent(sender.getName());
    }

    private TextComponent getTopStatsTitle(StatRequest request, int statListSize) {
        return Component.text()
                .append(componentFactory.pluginPrefixComponent()).append(space())
                .append(componentFactory.titleComponent(config.getTopStatsTitle(), Target.TOP)).append(space())
                .append(componentFactory.titleNumberComponent(statListSize)).append(space())
                .append(getStatNameComponent(request))  //space is provided by statUnitComponent
                .append(getStatUnitComponent(request.getStatistic(), request.getSelection(), request.isConsoleSender()))
                .build();
    }

    private TextComponent getTopStatsTitleShort(StatRequest request, int statListSize) {
        return Component.text()
                .append(componentFactory.titleComponent(config.getTopStatsTitle(), Target.TOP)).append(space())
                .append(componentFactory.titleNumberComponent(statListSize)).append(space())
                .append(getStatNameComponent(request))  //space is provided by statUnitComponent
                .build();
    }

    private TextComponent getTopStatList(LinkedHashMap<String, Integer> topStats, StatRequest request) {
        TextComponent.Builder topList = Component.text();
        boolean useDots = config.useDots();
        boolean boldNames = config.playerNameIsBold();
        Set<String> playerNames = topStats.keySet();

        int count = 0;
        for (String playerName : playerNames) {
            TextComponent.Builder playerNameBuilder = componentFactory.playerNameBuilder(playerName, Target.TOP);
            topList.append(newline())
                    .append(componentFactory.rankingNumberComponent(" " + ++count + "."))
                    .append(space());
            if (useDots) {
                topList.append(playerNameBuilder)
                        .append(space());
                int dots = FontUtils.getNumberOfDotsToAlign(count + ". " + playerName, request.isConsoleSender(), boldNames);
                if (dots >= 1) {
                    topList.append(componentFactory.dotsBuilder().append(text((".".repeat(dots)))));
                }
            }
            else {
                topList.append(playerNameBuilder.append(text(":")));
            }
            topList.append(space()).append(getStatNumberComponent(request.getStatistic(), topStats.get(playerName), Target.TOP, request.isConsoleSender()));
        }
        return topList.build();
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
                    StringUtils.prettify(request.getStatistic().toString()),
                    StringUtils.prettify(request.getSubStatEntry()),
                    request.getSelection());
        }
    }

    private TextComponent getStatNumberComponent(Statistic statistic, long statNumber, Target selection, boolean isConsoleSender) {
        Unit.Type type = Unit.getTypeFromStatistic(statistic);
        Unit statUnit;
        switch (type) {
            case DISTANCE -> statUnit = Unit.fromString(config.getDistanceUnit(false));
            case DAMAGE -> statUnit = Unit.fromString(config.getDamageUnit(false));
            case TIME -> {
                return getTimeNumberComponent(statNumber, selection, getTimeUnitRange(statNumber));
            }
            default -> statUnit = Unit.NUMBER;
        }
        String prettyNumber = formatter.format(statNumber, statUnit);
        if (!config.useHoverText() || statUnit == Unit.NUMBER) {
            return componentFactory.statNumberComponent(prettyNumber, selection);
        }
        Unit hoverUnit = type == Unit.Type.DISTANCE ? Unit.fromString(config.getDistanceUnit(true)) :
                Unit.fromString(config.getDamageUnit(true));
        String prettyHoverNumber = formatter.format(statNumber, hoverUnit);
        MyLogger.logMsg("mainNumber: " + prettyNumber + ", hoverNumber: " + prettyHoverNumber, DebugLevel.HIGH);

        if (hoverUnit == Unit.HEART) {
            return componentFactory.damageNumberHoverComponent(
                    prettyNumber, prettyHoverNumber,
                    componentFactory.heartComponent(isConsoleSender, true), selection);
        }
        if (config.useTranslatableComponents()) {
            String unitKey = languageKeyHandler.getUnitKey(hoverUnit);
            if (unitKey != null) {
                return componentFactory.statNumberHoverComponent(prettyNumber, prettyHoverNumber, null, unitKey, selection);
            }
        }
        return componentFactory.statNumberHoverComponent(prettyNumber, prettyHoverNumber, hoverUnit.getLabel(), null, selection);
    }

    private TextComponent getTimeNumberComponent(long statNumber, Target selection, ArrayList<Unit> unitRange) {
        if (unitRange.size() <= 1 || (config.useHoverText() && unitRange.size() <= 3)) {
            MyLogger.logMsg(
                    "There is something wrong with the time-units you specified, please check your config!",
                    true);
            return componentFactory.statNumberComponent("-", selection);
        }
        else {
            String mainNumber = formatter.format(statNumber, unitRange.get(0), unitRange.get(1));
            if (!config.useHoverText()) {
                return componentFactory.statNumberComponent(mainNumber, selection);
            } else {
                String hoverNumber = formatter.format(statNumber, unitRange.get(2), unitRange.get(3));
                MyLogger.logMsg("mainNumber: " + mainNumber + ", hoverNumber: " + hoverNumber, DebugLevel.HIGH);
                return componentFactory.statNumberHoverComponent(mainNumber, hoverNumber,
                        null, null, selection);
            }
        }
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
        if (config.useHoverText()) {
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

    private TextComponent getStatUnitComponent(Statistic statistic, Target selection, boolean isConsoleSender) {
        Unit statUnit;
        switch (Unit.getTypeFromStatistic(statistic)) {
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
        String statName = statUnit.getLabel();
        if (statUnit == Unit.HEART) {  //console can do u2665, u2764 looks better in-game
            return Component.space()
                    .append(componentFactory.heartComponent(isConsoleSender, false));
        }
        return Component.space()
                .append(componentFactory.statUnitComponent(statName, null, selection));
    }
}