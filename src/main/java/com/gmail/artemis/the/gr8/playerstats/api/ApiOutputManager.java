package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.enums.Unit;
import com.gmail.artemis.the.gr8.playerstats.msg.components.ComponentFactory;
import com.gmail.artemis.the.gr8.playerstats.msg.components.PrideComponentFactory;
import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.FontUtils;
import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.NumberFormatter;
import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Statistic;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.space;

public class ApiOutputManager implements ApiFormatter {

    private static ConfigHandler config;
    private final ComponentFactory componentFactory;
    private final PrideComponentFactory prideComponentFactory;
    private final NumberFormatter numberFormatter;

    public ApiOutputManager(ConfigHandler configHandler) {
        config = configHandler;
        componentFactory = new ComponentFactory(configHandler);
        prideComponentFactory = new PrideComponentFactory(configHandler);
        numberFormatter = new NumberFormatter();
    }

    @Override
    public TextComponent getPluginPrefix() {
        return componentFactory.pluginPrefix();
    }

    @Override
    public TextComponent getRainbowPluginPrefix() {
        return prideComponentFactory.pluginPrefix();
    }

    @Override
    public TextComponent getPluginPrefixAsTitle() {
        return componentFactory.pluginPrefixAsTitle();
    }

    @Override
    public TextComponent getRainbowPluginPrefixAsTitle() {
        return prideComponentFactory.pluginPrefixAsTitle();
    }

    @Override
    public TextComponent getTopStatTitle(int topStatSize, Statistic statistic) {
        return getTopStatTitle(topStatSize, statistic, null, null);
    }

    @Override
    public TextComponent getTopStatTitle(int topListSize, Statistic statistic, String subStatName) {
        return getTopStatTitle(topListSize, statistic, subStatName, null);
    }

    @Override
    public TextComponent getTopStatTitle(int topListSize, Statistic statistic, Unit unit) {
        return getTopStatTitle(topListSize, statistic, null, unit);
    }

    private TextComponent getTopStatTitle(int topListSize, Statistic statistic, @Nullable String subStatName, @Nullable Unit unit) {
        String prettyStatName = StringUtils.prettify(statistic.toString());
        TextComponent.Builder titleBuilder = Component.text()
                .append(componentFactory.title("Top", Target.TOP))
                .append(space())
                .append(componentFactory.titleNumber(topListSize))
                .append(space())
                .append(componentFactory.statAndSubStatName(prettyStatName, subStatName, Target.TOP));

        if (unit != null) {
            titleBuilder.append(space())
                    .append(componentFactory.statUnit(unit.getLabel(), Target.TOP));
        }
        return titleBuilder.build();
    }

    @Override
    public TextComponent getTopStatLine(int positionInTopList, String playerName, long statNumber, Unit.Type unitType) {
        Unit unit = Unit.getMostSuitableUnit(unitType, statNumber);
        TextComponent statNumberComponent = getStatNumberComponent(statNumber, Target.TOP, unit);
        return getTopStatLine(positionInTopList, playerName, statNumberComponent);
    }

    @Override
    public TextComponent getTopStatLine(int positionInTopList, String playerName, long statNumber, Unit unit) {
        TextComponent statNumberComponent = getStatNumberComponent(statNumber, Target.TOP, unit);
        return getTopStatLine(positionInTopList, playerName, statNumberComponent);
    }

    @Override
    public TextComponent getTopStatLineForTypeTime(int positionInList, String playerName, long statNumber, Unit bigUnit, Unit smallUnit) {
        TextComponent statNumberComponent = getTimeNumberComponent(statNumber, Target.TOP, bigUnit, smallUnit);
        return getTopStatLine(positionInList, playerName, statNumberComponent);
    }

    private TextComponent getTopStatLine(int positionInTopList, String playerName, TextComponent statNumberComponent) {
        TextComponent.Builder topStatLineBuilder = Component.text()
                .append(space())
                .append(componentFactory.rankNumber(positionInTopList))
                .append(space())
                .append(componentFactory.playerName(playerName, Target.TOP))
                .append(space());

        int dots = FontUtils.getNumberOfDotsToAlign(positionInTopList + ". " + playerName);
        if (dots >= 1) {
            topStatLineBuilder.append(componentFactory.dots(".".repeat(dots)));
        }

        return topStatLineBuilder
                .append(space())
                .append(statNumberComponent)
                .build();
    }

    @Override
    public TextComponent getServerStat(long statNumber, Statistic statistic) {
        Unit.Type unitType = Unit.getTypeFromStatistic(statistic);
        Unit unit = Unit.getMostSuitableUnit(unitType, statNumber);
        return getServerStat(statNumber, statistic, null, unit);
    }

    @Override
    public TextComponent getServerStat(long statNumber, Statistic statistic, String subStatName) {
        return getServerStat(statNumber, statistic, subStatName, Unit.NUMBER);
    }

    @Override
    public TextComponent getServerStat(long statNumber, Statistic statistic, Unit unit) {
        return getServerStat(statNumber, statistic, null, unit);
    }

    private TextComponent getServerStat(long statNumber, Statistic statistic, @Nullable String subStatName, Unit unit) {
        String serverTitle = config.getServerTitle();
        String serverName = config.getServerName();
        String prettyStatName = StringUtils.prettify(statistic.toString());
        Unit.Type unitType = unit.getType();

        TextComponent.Builder serverStatBuilder = Component.text()
                .append(componentFactory.title(serverTitle, Target.SERVER))
                .append(space())
                .append(componentFactory.serverName(serverName))
                .append(space())
                .append(getStatNumberComponent(statNumber, Target.SERVER, unit))
                .append(space())
                .append(componentFactory.statAndSubStatName(prettyStatName, subStatName, Target.SERVER));

        if (unitType== Unit.Type.DAMAGE || unitType == Unit.Type.DISTANCE) {
            serverStatBuilder
                    .append(space())
                    .append(componentFactory.statUnit(unit.getLabel(), Target.SERVER));
        }
        return serverStatBuilder.build();
    }

    private TextComponent getStatNumberComponent(long statNumber, Target target, Unit unit) {
        return switch (unit.getType()) {
            case TIME -> getTimeNumberComponent(statNumber, target, unit, null);
            case DAMAGE -> getDamageNumberComponent(statNumber, target, unit);
            case DISTANCE -> getDistanceNumberComponent(statNumber, target, unit);
            default -> componentFactory.statNumber(numberFormatter.formatNumber(statNumber), target);
        };
    }

    private TextComponent getTimeNumberComponent(long statNumber, Target target, Unit bigUnit, @Nullable Unit smallUnit) {
        if (smallUnit == null) {
            smallUnit = bigUnit.getSmallerUnit(1);
        }
        return componentFactory.timeNumber(numberFormatter.formatTimeNumber(statNumber, bigUnit, smallUnit), target);
    }

    private TextComponent getDamageNumberComponent(long statNumber, Target target, Unit unit) {
        return componentFactory.damageNumber(numberFormatter.formatDamageNumber(statNumber, unit), target);
    }

    private TextComponent getDistanceNumberComponent(long statNumber, Target target, Unit unit) {
        return componentFactory.distanceNumber(numberFormatter.formatDistanceNumber(statNumber, unit), target);
    }
}