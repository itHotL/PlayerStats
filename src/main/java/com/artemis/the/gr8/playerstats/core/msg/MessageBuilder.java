package com.artemis.the.gr8.playerstats.core.msg;

import com.artemis.the.gr8.playerstats.api.StatTextFormatter;
import com.artemis.the.gr8.playerstats.core.msg.components.*;
import com.artemis.the.gr8.playerstats.core.msg.msgutils.*;
import com.artemis.the.gr8.playerstats.api.StatRequest;
import com.artemis.the.gr8.playerstats.core.utils.EnumHandler;
import com.artemis.the.gr8.playerstats.core.utils.MyLogger;
import com.artemis.the.gr8.playerstats.api.enums.Target;
import com.artemis.the.gr8.playerstats.core.config.ConfigHandler;
import com.artemis.the.gr8.playerstats.api.enums.Unit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;

import static net.kyori.adventure.text.Component.*;

/**
 * Composes messages to send to a Player or Console. This class is responsible
 * for constructing a final {@link TextComponent} with the text content of the
 * desired message. The component parts (with appropriate formatting) are supplied
 * by a {@link ComponentFactory}. By default, this class works with the standard
 * ComponentFactory, but you can give it a different ComponentFactory upon creation.
 *
 * @see PrideComponentFactory
 * @see BukkitConsoleComponentFactory
 */
public final class MessageBuilder implements StatTextFormatter {

    private final ConfigHandler config;
    private final boolean useHoverText;

    private final ComponentFactory componentFactory;
    private final LanguageKeyHandler languageKeyHandler;
    private final NumberFormatter formatter;
    private final ComponentSerializer serializer;

    private MessageBuilder(ComponentFactory factory) {
        config = ConfigHandler.getInstance();
        languageKeyHandler = LanguageKeyHandler.getInstance();
        componentFactory = factory;

        if (componentFactory.isConsoleFactory()) {
            useHoverText = false;
        } else {
            useHoverText = config.useHoverText();
        }
        formatter = new NumberFormatter();
        serializer = new ComponentSerializer();
    }

    @Contract(" -> new")
    public static @NotNull MessageBuilder defaultBuilder() {
        return new MessageBuilder(new ComponentFactory());
    }

    @Contract("_ -> new")
    public static @NotNull MessageBuilder fromComponentFactory(ComponentFactory factory) {
        return new MessageBuilder(factory);
    }

    @Override
    public @NotNull String textComponentToString(TextComponent component) {
        return serializer.getTranslatableComponentSerializer().serialize(component);
    }

    @Override
    public TextComponent getPluginPrefix() {
        return componentFactory.pluginPrefix();
    }

    @Override
    public TextComponent getRainbowPluginPrefix() {
        PrideComponentFactory pride = new PrideComponentFactory();
        return pride.rainbowPrefix();
    }

    @Override
    public TextComponent getPluginPrefixAsTitle() {
        return componentFactory.pluginPrefixAsTitle();
    }

    @Override
    public TextComponent getRainbowPluginPrefixAsTitle() {
        PrideComponentFactory pride = new PrideComponentFactory();
        return pride.pluginPrefixAsTitle();
    }

    public @NotNull TextComponent reloadedConfig() {
        return composePluginMessage("Config reloaded!");
    }

    public @NotNull TextComponent stillReloading() {
        return composePluginMessage("The plugin is (re)loading, your request will be processed when it is done!");
    }

    public @NotNull TextComponent waitAMinute() {
        return composePluginMessage("Calculating statistics, this may take a minute...");
    }

    public @NotNull TextComponent waitAMoment() {
        return composePluginMessage("Calculating statistics, this may take a few moments...");
    }

    public @NotNull TextComponent missingStatName() {
        return composePluginMessage("Please provide a valid statistic name!");
    }

    public @NotNull TextComponent missingSubStatName(String statType) {
        return composePluginMessage("Please add a valid " + statType + " to look up this statistic!");
    }

    public @NotNull TextComponent missingPlayerName() {
        return composePluginMessage("Please specify a valid player-name!");
    }

    public @NotNull TextComponent wrongSubStatType(String statType, String subStatName) {
        return componentFactory.pluginPrefix()
                .append(space())
                .append(componentFactory.messageAccent().content("\"" + subStatName + "\""))
                .append(space())
                .append(componentFactory.message().content(
                "is not a valid " + statType + "!"));
    }

    public @NotNull TextComponent requestAlreadyRunning() {
        return composePluginMessage("Please wait for your previous lookup to finish!");
    }

    public @NotNull TextComponent stillOnShareCoolDown() {
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

    public @NotNull TextComponent resultsAlreadyShared() {
        return composePluginMessage("You already shared these results!");
    }

    public @NotNull TextComponent statResultsTooOld() {
        return composePluginMessage("It has been too long since you looked up " +
                "this statistic, please repeat the original command!");
    }

    public @NotNull TextComponent unknownError() {
        return composePluginMessage("Something went wrong with your request, " +
                "please try again or see /statistic for a usage explanation!");
    }

    private @NotNull TextComponent composePluginMessage(String content) {
        return getPluginPrefix()
                .append(space())
                .append(componentFactory.message().content(content));
    }

    @Contract(" -> new")
    public @NotNull TextComponent usageExamples() {
        return ExampleMessage.construct(componentFactory);
    }

    public TextComponent helpMsg() {
        int listSize = config.getTopListMaxSize();
        if (useHoverText) {
            return HelpMessage.constructHoverMsg(componentFactory, listSize);
        } else {
            return HelpMessage.constructPlainMsg(componentFactory, listSize);
        }
    }

    public @NotNull TextComponent excludeInfoMsg() {
        return ExcludeInfoMessage.construct(componentFactory);
    }

    @Override
    public @NotNull TextComponent getStatTitle(Statistic statistic, @Nullable String subStatName) {
        return getTopStatTitleComponent(0, statistic, subStatName, null);
    }

    @Override
    public @NotNull TextComponent getStatTitle(Statistic statistic, Unit unit) {
        return getTopStatTitleComponent(0, statistic, null, unit);
    }

    @Override
    public @NotNull TextComponent getTopStatTitle(int topListSize, Statistic statistic, @Nullable String subStatName) {
        return getTopStatTitleComponent(topListSize, statistic, subStatName, null);
    }

    @Override
    public @NotNull TextComponent getTopStatTitle(int topStatSize, Statistic statistic, Unit unit) {
        return getTopStatTitleComponent(topStatSize, statistic, null, unit);
    }

    @Override
    public @NotNull TextComponent formatTopStatLine(int positionInTopList, String playerName, long statNumber, Statistic statistic) {
        TextComponent statNumberComponent = getStatNumberComponent(statNumber, Target.TOP, statistic);
        return getTopStatLineComponent(positionInTopList, playerName, statNumberComponent);
    }

    @Override
    public @NotNull TextComponent formatTopStatLine(int positionInTopList, String playerName, long statNumber, Unit unit) {
        TextComponent statNumberComponent = getStatNumberComponent(statNumber, Target.TOP, unit);
        return getTopStatLineComponent(positionInTopList, playerName, statNumberComponent);
    }

    /**
     * Time-number does not hover
     */
    @Override
    public @NotNull TextComponent formatTopStatLineForTypeTime(int positionInTopList, String playerName, long statNumber, Unit bigUnit, Unit smallUnit) {
        TextComponent statNumberComponent = getBasicTimeNumberComponent(statNumber, Target.TOP, bigUnit, smallUnit);
        return getTopStatLineComponent(positionInTopList, playerName, statNumberComponent);
    }

    @Override
    public @NotNull TextComponent formatServerStat(long statNumber, Statistic statistic) {
        TextComponent statNumberComponent = getStatNumberComponent(statNumber, Target.SERVER, statistic);
        return getServerStatComponent(statNumberComponent, statistic, null, null);
    }

    @Override
    public @NotNull TextComponent formatServerStat(long statNumber, Statistic statistic, String subStatName) {
        TextComponent statNumberComponent = getStatNumberComponent(statNumber, Target.SERVER, statistic);
        return getServerStatComponent(statNumberComponent, statistic, subStatName, null);
    }

    @Override
    public @NotNull TextComponent formatServerStat(long statNumber, Statistic statistic, Unit unit) {
        TextComponent statNumberComponent = getStatNumberComponent(statNumber, Target.SERVER, unit);
        return getServerStatComponent(statNumberComponent, statistic, null, unit);
    }

    @Override
    public @NotNull TextComponent formatServerStatForTypeTime(long statNumber, Statistic statistic, Unit bigUnit, Unit smallUnit) {
        TextComponent statNumberComponent = getBasicTimeNumberComponent(statNumber, Target.SERVER, bigUnit, smallUnit);
        return getServerStatComponent(statNumberComponent, statistic, null, null);
    }

    @Override
    public @NotNull TextComponent formatPlayerStat(String playerName, int statNumber, Statistic statistic) {
        TextComponent statNumberComponent = getStatNumberComponent(statNumber, Target.PLAYER, statistic);
        return getPlayerStatComponent(playerName, statNumberComponent, statistic, null, null);
    }

    @Override
    public @NotNull TextComponent formatPlayerStat(String playerName, int statNumber, Statistic statistic, Unit unit) {
        TextComponent statNumberComponent = getStatNumberComponent(statNumber, Target.PLAYER, unit);
        return getPlayerStatComponent(playerName, statNumberComponent, statistic, null, unit);
    }

    @Override
    public @NotNull TextComponent formatPlayerStat(String playerName, int statNumber, Statistic statistic, String subStatName) {
        TextComponent statNumberComponent = getStatNumberComponent(statNumber, Target.PLAYER, statistic);
        return getPlayerStatComponent(playerName, statNumberComponent, statistic, subStatName, null);
    }

    @Override
    public @NotNull TextComponent formatPlayerStatForTypeTime(String playerName, int statNumber, Statistic statistic, Unit bigUnit, Unit smallUnit) {
        TextComponent statNumberComponent = getBasicTimeNumberComponent(statNumber, Target.PLAYER, bigUnit, smallUnit);
        return getPlayerStatComponent(playerName, statNumberComponent, statistic, null, null);
    }

    /**
     * Returns a BiFunction for a player statistic. This BiFunction will return
     * a formattedComponent, the shape of which is determined by the 2 parameters
     * the BiFunction gets.
     * <p>- Integer shareCode: if a shareCode is provided, a clickable "share"
     * button will be added.
     * <br>- CommandSender sender: if a sender is provided, a signature with
     * "shared by sender-name" will be added.
     * <br>- If both parameters are null, the formattedComponent will be returned
     * as is.
     */
    public @NotNull FormattingFunction formattedPlayerStatFunction(int stat, @NotNull StatRequest.Settings request) {
        TextComponent playerStat = formatPlayerStat(request.getPlayerName(), stat, request.getStatistic(), request.getSubStatEntryName());
        return getFormattingFunction(playerStat, Target.PLAYER);
    }

    /**
     * Returns a BiFunction for a server statistic. This BiFunction will return
     * a formattedComponent, the shape of which is determined by the 2 parameters
     * the BiFunction gets.
     * <p>- Integer shareCode: if a shareCode is provided, a clickable "share"
     * button will be added.
     * <br>- CommandSender sender: if a sender is provided, a signature with
     * "shared by sender-name" will be added.
     * <br>- If both parameters are null, the formattedComponent will be returned
     * as is.
     */
    public @NotNull FormattingFunction formattedServerStatFunction(long stat, @NotNull StatRequest.Settings request) {
        TextComponent serverStat = formatServerStat(stat, request.getStatistic(), request.getSubStatEntryName());
        return getFormattingFunction(serverStat, Target.SERVER);
    }

    /**
     * Returns a BiFunction for a top statistic. This BiFunction will return
     * a formattedComponent, the shape of which is determined by the 2 parameters
     * the BiFunction gets.
     * <p>- Integer shareCode: if a shareCode is provided, a clickable "share"
     * button will be added.
     * <br>- CommandSender sender: if a sender is provided, a signature with
     * "shared by sender-name" will be added.
     * <br>- If both parameters are null, the formattedComponent will be returned
     * as is.
     */
    public @NotNull FormattingFunction formattedTopStatFunction(@NotNull LinkedHashMap<String, Integer> topStats, @NotNull StatRequest.Settings request) {
        final TextComponent title = getTopStatTitle(topStats.size(), request.getStatistic(), request.getSubStatEntryName());
        final TextComponent list = getTopStatListComponent(topStats, request.getStatistic());
        final boolean useEnters = config.useEnters(Target.TOP, false);
        final boolean useEntersForShared = config.useEnters(Target.TOP, true);

        BiFunction<Integer, CommandSender, TextComponent> biFunction = (shareCode, sender) -> {
            TextComponent.Builder topBuilder = text();

            //if we're adding a share-button
            if (shareCode != null) {
                if (useEnters) {
                    topBuilder.append(newline());
                }
                topBuilder.append(componentFactory.pluginPrefix())
                        .append(space())
                        .append(title)
                            .append(space())
                            .append(componentFactory.shareButton(shareCode))
                        .append(list);
            }
            //if we're adding a "shared by" component
            else if (sender != null) {
                if (useEntersForShared) {
                    topBuilder.append(newline());
                }
                topBuilder.append(title)
                            .append(space())
                            .append(componentFactory.statResultInHoverText(text()
                                    .append(componentFactory.pluginPrefix())
                                    .append(space())
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
                topBuilder.append(componentFactory.pluginPrefix())
                        .append(space())
                        .append(title)
                        .append(list);
            }
            return topBuilder.build();
        };
        return new FormattingFunction(biFunction);
    }

    private @NotNull TextComponent getPlayerStatComponent(String playerName, TextComponent statNumberComponent, Statistic statistic, @Nullable String subStatName, @Nullable Unit unit) {
        TextComponent statUnit = (unit == null) ?
                getStatUnitComponent(statistic, Target.PLAYER) :
                getStatUnitComponent(unit, Target.PLAYER);

        return Component.text()
                .append(componentFactory.playerName(playerName, Target.PLAYER)
                        .append(text(":"))
                        .append(space()))
                .append(statNumberComponent)
                .append(space())
                .append(getStatAndSubStatNameComponent(statistic, subStatName, Target.PLAYER))
                .append(statUnit)  //space is provided by statUnitComponent
                .build();
    }

    private @NotNull TextComponent getServerStatComponent(TextComponent statNumber, Statistic statistic, @Nullable String subStatName, @Nullable Unit unit) {
        String serverTitle = config.getServerTitle();
        String serverName = config.getServerName();
        TextComponent statUnit = (unit == null) ?
                getStatUnitComponent(statistic, Target.SERVER) :
                getStatUnitComponent(unit, Target.SERVER);

        return Component.text()
                .append(componentFactory.title(serverTitle, Target.SERVER))
                .append(space())
                .append(componentFactory.serverName(serverName))
                .append(space())
                .append(statNumber)
                .append(space())
                .append(getStatAndSubStatNameComponent(statistic, subStatName, Target.SERVER))
                .append(statUnit) //space is provided by statUnit
                .build();
    }

    private @NotNull TextComponent getTopStatTitleComponent(int topListSize, Statistic statistic, @Nullable String subStatName, @Nullable Unit unit) {
        TextComponent statUnit = (unit == null) ?
                getStatUnitComponent(statistic, Target.TOP) :
                getStatUnitComponent(unit, Target.TOP);

        if (topListSize == 0) {
            return Component.text()
                    .append(getStatAndSubStatNameComponent(statistic, subStatName, Target.TOP))
                    .append(statUnit) //space is provided by statUnitComponent
                    .build();
        } else {
            return Component.text()
                    .append(componentFactory.title(config.getTopStatsTitle(), Target.TOP))
                    .append(space())
                    .append(componentFactory.titleNumber(topListSize))
                    .append(space())
                    .append(getStatAndSubStatNameComponent(statistic, subStatName, Target.TOP))
                    .append(statUnit)  //space is provided by statUnitComponent
                    .build();
        }
    }

    private @NotNull TextComponent getTopStatListComponent(@NotNull LinkedHashMap<String, Integer> topStats, Statistic statistic) {
        TextComponent.Builder topList = Component.text();
        Set<String> playerNames = topStats.keySet();
        boolean useDots = config.useDots();

        int count = 0;
        for (String playerName : playerNames) {
            topList.append(newline());
            if (useDots) {
                topList.append(getTopStatLineComponent(
                        ++count, playerName, getStatNumberComponent(topStats.get(playerName), Target.TOP, statistic)));
            } else {
                topList.append(space())
                        .append(componentFactory.rankNumber(++count))
                        .append(space())
                        .append(componentFactory.playerName(playerName + ":", Target.TOP))
                        .append(space()).append(getStatNumberComponent(topStats.get(playerName), Target.TOP, statistic));
            }
        }
        return topList.build();
    }

    private @NotNull TextComponent getTopStatLineComponent(int positionInTopList, String playerName, TextComponent statNumberComponent) {
        boolean useDots = config.useDots();
        String fullPlayerName = useDots ? playerName : playerName + ":";

        TextComponent.Builder topStatLineBuilder = Component.text()
                .append(space())
                .append(componentFactory.rankNumber(positionInTopList))
                .append(space())
                .append(componentFactory.playerName(fullPlayerName, Target.TOP));

        if (config.useDots()) {
            int dots = getNumberOfDotsToAlign(positionInTopList + ". " + playerName);
            if (dots >= 1) {
                topStatLineBuilder
                        .append(space())
                        .append(componentFactory.dots(".".repeat(dots)));
            }
        }

        return topStatLineBuilder
                .append(space())
                .append(statNumberComponent)
                .build();
    }

    private TextComponent getStatAndSubStatNameComponent(Statistic statistic, @Nullable String subStatName, Target target) {
        EnumHandler enumHandler = EnumHandler.getInstance();

        String statKey = languageKeyHandler.getStatKey(statistic);
        String subStatKey = switch (statistic.getType()) {
            case UNTYPED -> null;
            case ENTITY -> languageKeyHandler.getEntityKey(enumHandler.getEntityEnum(subStatName));
            case BLOCK -> languageKeyHandler.getBlockKey(enumHandler.getBlockEnum(subStatName));
            case ITEM -> languageKeyHandler.getItemKey(enumHandler.getItemEnum(subStatName));
        };
        if (subStatKey == null) {
            subStatKey = StringUtils.prettify(subStatName);
        }

        if (config.useTranslatableComponents()) {
            return componentFactory.statAndSubStatNameTranslatable(statKey, subStatKey, target);
        }

        String prettyStatName = languageKeyHandler.convertLanguageKeyToDisplayName(statKey);
        String prettySubStatName = languageKeyHandler.convertLanguageKeyToDisplayName(subStatKey);
        return componentFactory.statAndSubStatName(prettyStatName, prettySubStatName, target);
    }

    private TextComponent getStatNumberComponent(long statNumber, Target target, @NotNull Unit unit) {
        return switch (unit.getType()) {
            case TIME -> getBasicTimeNumberComponent(statNumber, target, unit, null);
            case DAMAGE -> getDamageNumberComponent(statNumber, target, unit);
            case DISTANCE -> getDistanceNumberComponent(statNumber, target, unit);
            default -> getDefaultNumberComponent(statNumber, target);
        };
    }

    private TextComponent getStatNumberComponent(long statNumber, Target target, Statistic statistic) {
        Unit.Type unitType = Unit.getTypeFromStatistic(statistic);
        return switch (unitType) {
            case DISTANCE -> getDistanceNumberComponent(statNumber, target);
            case DAMAGE -> getDamageNumberComponent(statNumber, target);
            case TIME -> getTimeNumberComponent(statNumber, target);
            default -> getDefaultNumberComponent(statNumber, target);
        };
    }

    private TextComponent getDistanceNumberComponent(long statNumber, Target target) {
        Unit statUnit = Unit.fromString(config.getDistanceUnit(false));
        return getDistanceNumberComponent(statNumber, target, statUnit);
    }

    private TextComponent getDistanceNumberComponent(long statNumber, Target target, Unit unit) {
        String prettyNumber = formatter.formatDistanceNumber(statNumber, unit);
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
        return getDamageNumberComponent(statNumber, target, statUnit);
    }

    private TextComponent getDamageNumberComponent(long statNumber, Target target, Unit unit) {
        String prettyNumber = formatter.formatDamageNumber(statNumber, unit);
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
            MyLogger.logWarning("There is something wrong with the time-units you specified, please check your config!");
            return componentFactory.timeNumber(formatter.formatNumber(statNumber), target);
        }
        else {
            String mainNumber = formatter.formatTimeNumber(statNumber, unitRange.get(0), unitRange.get(1));
            if (!useHoverText) {
                return componentFactory.timeNumber(mainNumber, target);
            } else {
                String hoverNumber = formatter.formatTimeNumber(statNumber, unitRange.get(2), unitRange.get(3));
                MyLogger.logHighLevelMsg("mainNumber: " + mainNumber + ", hoverNumber: " + hoverNumber);
                return componentFactory.timeNumberWithHoverText(mainNumber, hoverNumber, target);
            }
        }
    }

    private TextComponent getBasicTimeNumberComponent(long statNumber, Target target, Unit bigUnit, @Nullable Unit smallUnit) {
        if (smallUnit == null) {
            smallUnit = bigUnit.getSmallerUnit(1);
        }
        return componentFactory.timeNumber(formatter.formatTimeNumber(statNumber, bigUnit, smallUnit), target);
    }

    private TextComponent getDefaultNumberComponent(long statNumber, Target target) {
        return componentFactory.statNumber(formatter.formatNumber(statNumber), target);
    }

    /**
     * Provides its own space in front of it!
     */
    private TextComponent getStatUnitComponent(Statistic statistic, Target target) {
        Unit unit = switch (Unit.getTypeFromStatistic(statistic)) {
            case DAMAGE -> Unit.fromString(config.getDamageUnit(false));
            case DISTANCE -> Unit.fromString(config.getDistanceUnit(false));
            default -> Unit.NUMBER;
        };
        return getStatUnitComponent(unit, target);
    }

    private TextComponent getStatUnitComponent(@NotNull Unit unit, Target target) {
        return switch (unit.getType()) {
            case DAMAGE -> getDamageUnitComponent(unit, target);
            case DISTANCE -> getDistanceUnitComponent(unit, target);
            default -> Component.empty();
        };
    }

    /**
     * Provides its own space in front of it!
     */
    private @NotNull TextComponent getDistanceUnitComponent(Unit unit, Target target) {
        if (config.useTranslatableComponents()) {
            String unitKey = languageKeyHandler.getUnitKey(unit);
            if (unitKey != null) {
                return Component.space()
                        .append(componentFactory.statUnitTranslatable(unitKey, target));
            }
        }
        return Component.space()
                .append(componentFactory.statUnit(unit.getLabel(), target));
    }

    /**
     * Provides its own space in front of it!
     */
    private @NotNull TextComponent getDamageUnitComponent(Unit unit, Target target) {
        if (unit == Unit.HEART) {
            TextComponent heartUnit = useHoverText ?
                    componentFactory.heartBetweenBracketsWithHoverText() :
                    componentFactory.heartBetweenBrackets();
            return Component.space().append(heartUnit);
        }
        return Component.space()
                .append(componentFactory.statUnit(unit.getLabel(), target));
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

    private @NotNull FormattingFunction getFormattingFunction(@NotNull TextComponent statResult, Target target) {
        boolean useEnters = config.useEnters(target, false);
        boolean useEntersForShared = config.useEnters(target, true);

        BiFunction<Integer, CommandSender, TextComponent> biFunction = (shareCode, sender) -> {
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
        return new FormattingFunction(biFunction);
    }

    private int getNumberOfDotsToAlign(String displayText) {
        if (componentFactory.isConsoleFactory()) {
            return FontUtils.getNumberOfDotsToAlignForConsole(displayText);
        } else if (config.playerNameIsBold()) {
            return FontUtils.getNumberOfDotsToAlignForBoldText(displayText);
        } else {
            return FontUtils.getNumberOfDotsToAlign(displayText);
        }
    }

    /**
     * Get an ArrayList consisting of 2 or 4 timeUnits. The order of items is:
     * <p>0. maxUnit</p>
     * <p>1. minUnit</p>
     * <p>2. maxHoverUnit</p>
     * <p>3. minHoverUnit</p>
     */
    private @NotNull ArrayList<Unit> getTimeUnitRange(long statNumber) {
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
        return unitRange;
    }
}