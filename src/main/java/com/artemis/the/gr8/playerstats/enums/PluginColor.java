package com.artemis.the.gr8.playerstats.enums;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.Random;

/**
 * This enum represents the colorscheme PlayerStats uses in its output messages.
 * The first set of colors is used throughout the plugin, while the set of NAME-colors
 * represents the colors that player-names can be in the "shared by player-name"
 * section of shared statistics
 */
public enum PluginColor {
    /**
     * ChatColor Gray (#AAAAAA)
     */
    GRAY (NamedTextColor.GRAY),

    /**
     * A Dark Purple that is mainly used for title-underscores (#6E3485).
     */
    DARK_PURPLE (TextColor.fromHexString("#6E3485")),

    /**
     * A Light Purple that is meant to simulate the color of a clicked link.
     * Used for the "Hover Here" part of shared statistics (#845EC2)
     * */
    LIGHT_PURPLE (TextColor.fromHexString("#845EC2")),

    /**
     * A Light Blue that is used for the share-button and feedback message accents (#55C6FF).
     */
    LIGHT_BLUE (TextColor.fromHexString("#55C6FF")),

    /**
     * A very light blue that is used for feedback messages and hover-text (#ADE7FF)
     */
    LIGHTEST_BLUE(TextColor.fromHexString("#ADE7FF")),

    /**
     * ChatColor Gold (#FFAA00)
     */
    GOLD (NamedTextColor.GOLD),

    /**
     * A Medium Gold that is used for the example message and for hover-text accents (#FFD52B).
     */
    MEDIUM_GOLD (TextColor.fromHexString("#FFD52B")),

    /**
     * A Light Gold that is used for the example message and for hover-text accents (#FFEA40).
     */
    LIGHT_GOLD (TextColor.fromHexString("#FFEA40")),

    /**
     * A Light Yellow that is used for final accents in the example message (#FFFF8E).
     */
    LIGHT_YELLOW (TextColor.fromHexString("#FFFF8E")),

    /**
     * The color of vanilla Minecraft hearts (#FF1313).
     */
    RED (TextColor.fromHexString("#FF1313")),

    /**
     * ChatColor Blue (#5555FF)
     */
    NAME_1 (NamedTextColor.BLUE), //#5555FF - blue

    /**
     * A shade of blue between Blue and Medium Blue (#4287F5)
     */
    NAME_2 (TextColor.fromHexString("#4287F5")),

    /**
     * Medium Blue (#55AAFF)
     */
    NAME_3 (TextColor.fromHexString("#55AAFF")),

    /**
     * A shade of magenta/purple (#D65DB1)
     */
    NAME_4 (TextColor.fromHexString("#D65DB1")),

    /**
     * A dark shade of orange (#EE8A19)
     */
    NAME_5 (TextColor.fromHexString("#EE8A19")),

    /**
     * A shade of green/aqua/cyan-ish (#01C1A7)
     */
    NAME_6 (TextColor.fromHexString("#01C1A7")),

    /**
     * A light shade of green (#46D858)
     */
    NAME_7 (TextColor.fromHexString("#46D858"));


    private final TextColor color;

    PluginColor(TextColor color) {
        this.color = color;
    }

    /**
     * Returns the TextColor value belonging to the corresponding enum constant.
     */
    public TextColor getColor() {
        return color;
    }

    /**
     * Gets the nearest NamedTextColor for the corresponding enum constant.
     */
    public TextColor getConsoleColor() {
        return NamedTextColor.nearestTo(color);
    }

    /**
     * Randomly selects one of the 7 different NAME-colors.
     */
    public static TextColor getRandomNameColor() {
        return getRandomNameColor(false);
    }

    /**
     * Randomly selects one of the 7 different NAME-colors, and if isConsole is true,
     * returns the closest NamedTextColor
     */
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