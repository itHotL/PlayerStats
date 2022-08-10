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

    @Override
    public TextComponent getFormattedTopStatLine(int positionInTopList, String playerName, long statNumber, Statistic statistic) {
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

        Unit.Type statUnitType = Unit.getTypeFromStatistic(statistic);
        TextComponent numberComponent = getStatNumberComponent(statNumber, statUnitType, Target.TOP);

        return topStatLineBuilder
                .append(space())
                .append(numberComponent)
                .build();
    }

    @Override
    public TextComponent getFormattedServerStat(long statNumber, Statistic statistic) {
        return getFormattedServerStat(statNumber, statistic, null, null);
    }

    @Override
    public TextComponent getFormattedServerStat(long statNumber, Statistic statistic, String subStatName) {
        return getFormattedServerStat(statNumber, statistic, subStatName, null);
    }

    @Override
    public TextComponent getFormattedServerStat(long statNumber, Statistic statistic, Unit unit) {
        return getFormattedServerStat(statNumber, statistic, null, unit);
    }

    private TextComponent getFormattedServerStat(long statNumber, Statistic statistic, @Nullable String subStatName, @Nullable Unit unit) {
        String serverTitle = config.getServerTitle();
        String serverName = config.getServerName();
        String prettyStatName = StringUtils.prettify(statistic.toString());
        Unit.Type unitType = Unit.getTypeFromStatistic(statistic);

        TextComponent.Builder serverStatBuilder = Component.text()
                .append(componentFactory.title(serverTitle, Target.SERVER))
                .append(space())
                .append(componentFactory.serverName(serverName))
                .append(space())
                .append(getStatNumberComponent(statNumber, unitType, Target.SERVER))
                .append(space())
                .append(componentFactory.statAndSubStatName(prettyStatName, subStatName, Target.SERVER));

        if (unit != null) {
            serverStatBuilder.append(space())
                    .append(componentFactory.statUnit(unit.getLabel(), Target.SERVER));
        }
        return serverStatBuilder.build();
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

    private TextComponent getStatNumberComponent(long statNumber, Unit.Type unitType, Target target) {
        return switch (unitType) {
            case DISTANCE -> {
                Unit unit = Unit.getMostSuitableUnit(Unit.Type.DISTANCE, statNumber);
                yield componentFactory.distanceNumber(numberFormatter.formatDistanceNumber(statNumber, unit), target);
            }
            case DAMAGE -> {
                Unit unit = Unit.getMostSuitableUnit(Unit.Type.DAMAGE, statNumber);
                yield componentFactory.damageNumber(numberFormatter.formatDamageNumber(statNumber, unit), target);
            }
            case TIME -> {
                Unit bigUnit = Unit.getMostSuitableUnit(Unit.Type.TIME, statNumber);
                Unit smallUnit = bigUnit.getSmallerUnit(1);
                yield componentFactory.timeNumber(numberFormatter.formatTimeNumber(statNumber, bigUnit, smallUnit), target);
            }
            default -> componentFactory.statNumber(numberFormatter.formatNumber(statNumber), target);
        };
    }
}