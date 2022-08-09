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

    /** @return [PlayerStats]*/
    TextComponent getPluginPrefix();

    TextComponent getRainbowPluginPrefix();

    /** @return ________ [PlayerStats] ________*/
    TextComponent getPluginPrefixAsTitle();

    TextComponent getRainbowPluginPrefixAsTitle();

    TextComponent getTopStatTitle(int topStatSize, Statistic statistic);

    TextComponent getTopStatTitle(int topStatSize, Statistic statistic, String subStatisticName);

    TextComponent getTopStatTitle(int topStatSize, Statistic statistic, Unit unit);

    /** @return a single line from a top-x statistic:
     * <br> x. Player-name ......... number */
    TextComponent getFormattedTopStatLine(int positionInTopList, String playerName, long statNumber, Unit unit);

    TextComponent getFormattedServerStat(long statNumber, Unit unit);
}