package com.gmail.artemis.the.gr8.playerstats.api;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Statistic;

public interface Formatter extends StatFormatter {

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