package com.artemis.the.gr8.playerstats.msg.msgutils;

import com.artemis.the.gr8.playerstats.utils.MyLogger;

/**
 * A small utility class that helps make enum constant
 * names prettier for output in stat-messages.
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Replace "_" with " " and capitalize each first letter of the input.
     *
     * @param input String to prettify, case-insensitive
     */
    public static String prettify(String input) {
        if (input == null) return null;

        StringBuilder capitals = new StringBuilder(input.toLowerCase());
        capitals.setCharAt(0, Character.toUpperCase(capitals.charAt(0)));

        while (capitals.indexOf("_") != -1) {
            MyLogger.logHighLevelMsg("Replacing underscores...");
            int index = capitals.indexOf("_");
            capitals.setCharAt(index, ' ');
        }

        while (capitals.indexOf(" ") != -1) {
            MyLogger.logHighLevelMsg("Capitalizing names...");
            int index = capitals.indexOf(" ") + 1;
            capitals.setCharAt(index, Character.toUpperCase(capitals.charAt(index)));
        }
        return capitals.toString();
    }
}