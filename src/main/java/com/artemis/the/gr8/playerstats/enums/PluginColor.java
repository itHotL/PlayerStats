package com.artemis.the.gr8.playerstats.enums;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

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
    RED (TextColor.fromHexString("#FF1313"));


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
    public @NotNull TextColor getConsoleColor() {
        return NamedTextColor.nearestTo(color);
    }
}