package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.msg.components.ComponentUtils;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Statistic;

public interface Formatter {

    /** Turns a TextComponent into its String representation. This method is equipped
     to turn all PlayerStats' formatted statResults into String.

     @return a String representation of this TextComponent, without hover/click events,
     but with color, style and formatting. TranslatableComponents will be turned into
     plain English.*/
    default String TextComponentToString(TextComponent component) {
        return ComponentUtils.getTranslatableComponentSerializer()
                .serialize(component);
    }

    /** @return [PlayerStats]*/
    TextComponent getPluginPrefix();

    TextComponent getRainbowPluginPrefix();

    /** @return ________ [PlayerStats] ________*/
    TextComponent getPluginPrefixAsTitle();

    TextComponent getRainbowPluginPrefixAsTitle();

    /** @return a single line from a top-x statistic:
     * <br> x. Player-name ......... number */
    TextComponent formatSingleTopStatLine(int positionInTopList, String playerName, long statNumber, Statistic statistic);
}