package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.enums.Unit;
import com.gmail.artemis.the.gr8.playerstats.msg.components.ComponentUtils;
import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.NumberFormatter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Statistic;

/** Formats messages meant for usage outside PlayerStats.

 The output is ready to be sent to a Minecraft client or console with the Adventure library.
 To send a Component, you need to get a {@link BukkitAudiences} object. Normally you would
 have to add the library as a dependency, but since the library is included in PlayerStats, you can
 access it directly. Information on how to get and use the BukkitAudiences object can be found on
 <a href="https://docs.adventure.kyori.net/platform/bukkit.html">Adventure's website</a>.*/
public interface ApiFormatter {

    /** Turns a TextComponent into its String representation. This method is equipped
     to turn all PlayerStats' formatted statResults into String.

     @return a String representation of this TextComponent, without hover/click events,
     but with color, style and formatting. TranslatableComponents will be turned into
     plain English.*/
    default String TextComponentToString(TextComponent component) {
        return ComponentUtils.getTranslatableComponentSerializer()
                .serialize(component);
    }

    /** Gets a {@link NumberFormatter} to format raw numbers into something more readable.*/
    default NumberFormatter getNumberFormatter() {
        return new NumberFormatter();
    }

    /** Gets the default prefix PlayerStats uses.
     @return [PlayerStats]*/
    TextComponent getPluginPrefix();

    /** Gets the special rainbow version of PlayerStats' prefix.
     @return [PlayerStats] in rainbow colors*/
    TextComponent getRainbowPluginPrefix();

    /** Gets the version of the prefix that is surrounded by underscores. This is
     meant to be used as a title above a message or statistic display.
    @return ________ [PlayerStats] ________ */
    TextComponent getPluginPrefixAsTitle();

    /** Gets the special rainbow version of the title-prefix.
     @return ________ [PlayerStats] ________ in rainbow colors*/
    TextComponent getRainbowPluginPrefixAsTitle();

    /** Gets the default top-stat-title for a Statistic of Type.Untyped.
     @return Top [topStatSize] [stat-name]*/
    TextComponent getTopStatTitle(int topStatSize, Statistic statistic);

    /** Gets the top-stat-title for a statistic that has a sub-statistic (block, item or entity).
     @return Top [topStatSize] [stat-name] [sub-stat-name] */
    TextComponent getTopStatTitle(int topStatSize, Statistic statistic, String subStatName);

    /** Gets the top-stat-title with the specified {@link Unit} in the title.
     @return Top [topStatSize] [stat-name] [unit-name] */
    TextComponent getTopStatTitle(int topStatSize, Statistic statistic, Unit unit);

    /** Formats the input into a single top-statistic line.
     @return a single line from a top-x statistic:
     * <br> [positionInTopList]. [player-name] ......... [stat-number] */
    TextComponent getFormattedTopStatLine(int positionInTopList, String playerName, long statNumber, Statistic statistic);

    /** Formats the input into a server statistic message.
     @return [Total on this server]: [stat-number] [stat-name] */
    TextComponent getFormattedServerStat(long statNumber, Statistic statistic);

    /** Formats the input into a server statistic message for a statistic that has a
     sub-statistic (block, item or entity).
     @return [Total on this server]: [stat-number] [stat-name] [sub-stat-name]*/
    TextComponent getFormattedServerStat(long statNumber, Statistic statistic, String subStatName);

    /** Formats the input into a server statistic message with the specified {@link Unit}.
     @return [Total on this server]: [stat-number] [stat-name] [unit-name]*/
    TextComponent getFormattedServerStat(long statNumber, Statistic statistic, Unit unit);
}