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

    private final ComponentFactory componentFactory;
    private final PrideComponentFactory prideComponentFactory;
    private final NumberFormatter numberFormatter;

    public ApiOutputManager(ConfigHandler config) {
        componentFactory = new ComponentFactory(config);
        prideComponentFactory = new PrideComponentFactory(config);
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
    public TextComponent getTopStatTitle(int topListSize, Statistic statistic, @Nullable String subStatName) {
        return getTopStatTitle(topListSize, statistic, subStatName, null);
    }

    @Override
    public TextComponent getTopStatTitle(int topListSize, Statistic statistic, Unit unit) {
        return getTopStatTitle(topListSize, statistic, null, unit);
    }

    private TextComponent getTopStatTitle(int topListSize, Statistic statistic, String subStatName, Unit unit) {
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
    public TextComponent getFormattedTopStatLine(int positionInTopList, String playerName, long statNumber, Unit unit) {
        TextComponent.Builder topStatLineBuilder = Component.text()
                .append(space())
                .append(componentFactory.rankNumber(positionInTopList))
                .append(space());

        int dots = FontUtils.getNumberOfDotsToAlign(positionInTopList + ". " + playerName);
        if (dots >= 1) {
            topStatLineBuilder.append(componentFactory.dots(".".repeat(dots)));
        }

        TextComponent numberComponent = getTopStatNumberComponent(unit, statNumber);
        return topStatLineBuilder
                .append(space())
                .append(numberComponent)
                .build();
    }

    @Override
    public TextComponent getFormattedServerStat(long statNumber, Unit unit) {

    }

    private TextComponent getTopStatNumberComponent(Unit unit, long statNumber) {
        return switch (unit.getType()) {
            case DISTANCE -> componentFactory.distanceNumber(numberFormatter.formatDistanceNumber(statNumber, unit), Target.TOP);
            case DAMAGE -> componentFactory.damageNumber(numberFormatter.formatDamageNumber(statNumber, unit), Target.TOP);
            case TIME -> componentFactory.timeNumber(numberFormatter.formatTimeNumber(statNumber, unit, unit), Target.TOP);
            default -> componentFactory.statNumber(numberFormatter.formatNumber(statNumber), Target.TOP);
        };
    }
}