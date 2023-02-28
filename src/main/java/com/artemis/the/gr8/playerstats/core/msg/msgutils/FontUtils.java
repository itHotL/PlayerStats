package com.artemis.the.gr8.playerstats.core.msg.msgutils;

import org.bukkit.map.MinecraftFont;

/**
 * A small utility class that helps calculate how many dots
 * to use to get the numbers of a top-statistic aligned.
 */
public final class FontUtils {

    private FontUtils() {
    }

    public static int getNumberOfDotsToAlign(String displayText) {
        return (int) Math.round((130.0 - MinecraftFont.Font.getWidth(displayText))/2);
    }

    public static int getNumberOfDotsToAlignForConsole(String displayText) {
        return (int) Math.round((130.0 - MinecraftFont.Font.getWidth(displayText))/6) + 7;
    }

    public static int getNumberOfDotsToAlignForBoldText(String displayText) {
        return (int) Math.round((130.0 - (MinecraftFont.Font.getWidth(displayText) * 1.5))/2);
    }
}