package com.gmail.artemis.the.gr8.playerstats.enums;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/** This enum represents the colorscheme PlayerStats uses in its output messages.
 <p>GRAY: ChatColor Gray</p>
 <p>DARK_PURPLE: #6E3485 (used for default sub-titles, title-underscores and brackets)</p>
 <p>MEDIUM_BLUE: #55AAFF (used for all plain feedback and error messages)</p>
 <p>LIGHT_BLUE: #55C6FF (used for default hover-text)</p>
 <p>GOLD: ChatColor Gold (used for first parts of usage messages and for first parts of hover-text accent)</p>
 <p>LIGHT_GOLD: #FFB80E (used for second parts of usage messages and for second parts of hover-text accent) </p>
 <p>YELLOW: ChatColor Yellow (used for third parts of usage messages)</p>
 */
public enum PluginColor {
    GRAY (NamedTextColor.GRAY),
    DARK_PURPLE (TextColor.fromHexString("#6E3485")),
    MEDIUM_BLUE (TextColor.fromHexString("#55AAFF")),
    LIGHT_BLUE (TextColor.fromHexString("#55C6FF")),
    GOLD (NamedTextColor.GOLD),
    MEDIUM_GOLD (TextColor.fromHexString("#FFD52B")),
    YELLOW (NamedTextColor.YELLOW),
    LIGHT_YELLOW (TextColor.fromHexString("#FFFF8E"));

    //TODO try "#55AAFF" for LIGHT_BLUE
    private final TextColor color;

    PluginColor(TextColor color) {
        this.color = color;
    }

    public TextColor getColor() {
        return color;
    }

    public TextColor getConsoleColor() {
        return NamedTextColor.nearestTo(color);
    }
}
