package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.enums.Unit;
import net.kyori.adventure.text.TextComponent;

public interface Formatter extends StatFormatter {

    /** @return [PlayerStats]*/
    TextComponent getPluginPrefix();

    TextComponent getRainbowPluginPrefix();

    /** @return ________ [PlayerStats] ________*/
    TextComponent getPluginPrefixAsTitle();

    TextComponent getRainbowPluginPrefixAsTitle();

    TextComponent formatSingleTopStatLine(int positionInTopList, String playerName, long statNumber, Unit statNumberUnit);
}
