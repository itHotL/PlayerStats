package com.gmail.artemis.the.gr8.playerstats.msg.msgutils;

import org.bukkit.map.MinecraftFont;

/** A small utility class that helps calculate how many dots to use to get the numbers of a top-statistic aligned. */
public final class FontUtils {

    private FontUtils() {
    }

    public static int getNumberOfDotsToAlign(String displayText, boolean isConsoleSender, boolean fontIsBold) {
        if (isConsoleSender) {
            return (int) Math.round((130.0 - MinecraftFont.Font.getWidth(displayText))/6) + 7;
        } else if (!fontIsBold) {
            return (int) Math.round((130.0 - MinecraftFont.Font.getWidth(displayText))/2);
        } else {
            return (int) Math.round((130.0 - (MinecraftFont.Font.getWidth(displayText) * 1.5))/2);
        }
    }
}