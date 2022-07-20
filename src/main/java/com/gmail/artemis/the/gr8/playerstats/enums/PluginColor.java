package com.gmail.artemis.the.gr8.playerstats.enums;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.Random;

/** This enum represents the colorscheme PlayerStats uses in its output messages.
 <p>GRAY: ChatColor Gray</p>
 <p>DARK_PURPLE: #6E3485 (used for default sub-titles, title-underscores and brackets)</p>
 <p>MEDIUM_BLUE: #55AAFF (used for all plain feedback and error messages)</p>
 <p>LIGHT_BLUE: #55C6FF (used for default hover-text)</p>
 <p>GOLD: ChatColor Gold (used for first parts of usage messages and for first parts of hover-text accent)</p>
 <p>MEDIUM_GOLD: #FFD52B (used for second parts of usage messages and for second parts of hover-text accent) </p>
 <p>LIGHT_GOLD: #FFEA40 (used for third parts of usage messages)</p>
 <p>LIGHT_YELLOW: #FFFF8E (used for last parts of explanation message)</p>
 */
public enum PluginColor {
    GRAY (NamedTextColor.GRAY),  //#AAAAAA
    DARK_PURPLE (TextColor.fromHexString("#6E3485")),
    LIGHT_PURPLE (TextColor.fromHexString("#845EC2")),
    BLUE (NamedTextColor.BLUE),
    MEDIUM_BLUE (TextColor.fromHexString("#55AAFF")),
    LIGHT_BLUE (TextColor.fromHexString("#55C6FF")),
    GOLD (NamedTextColor.GOLD),  //#FFAA00
    MEDIUM_GOLD (TextColor.fromHexString("#FFD52B")),
    LIGHT_GOLD (TextColor.fromHexString("#FFEA40")),
    LIGHT_YELLOW (TextColor.fromHexString("#FFFF8E")),

    NAME_1 (NamedTextColor.BLUE), //#5555FF - blue
    NAME_2 (TextColor.fromHexString("#4287F5")),  //between blue and medium_blue
    NAME_3 (TextColor.fromHexString("#55AAFF")), //same as medium_blue
    NAME_4 (TextColor.fromHexString("#D65DB1")), //magenta-purple
    NAME_5 (TextColor.fromHexString("#EE8A19")), //dark orange
    NAME_6 (TextColor.fromHexString("#01C1A7")), //aqua-cyan-green-ish
    NAME_7 (TextColor.fromHexString("#46D858"));  //light green


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

    public static TextColor getRandomNameColor() {
        return getRandomNameColor(false);
    }

    public static TextColor getRandomNameColor(boolean isConsole) {
        Random randomizer = new Random();
        PluginColor color = switch (randomizer.nextInt(7)) {
            case 0 -> NAME_1;
            case 2 -> NAME_3;
            case 3 -> NAME_4;
            case 4 -> NAME_5;
            case 5 -> NAME_6;
            case 6 -> NAME_7;
            default -> NAME_2;
        };
        return getCorrespondingColor(color, isConsole);
    }

    private static TextColor getCorrespondingColor(PluginColor nameColor, boolean isConsole) {
        return isConsole ? nameColor.getConsoleColor() : nameColor.getColor();
    }
}