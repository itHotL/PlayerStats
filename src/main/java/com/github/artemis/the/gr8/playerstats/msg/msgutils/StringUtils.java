package com.github.artemis.the.gr8.playerstats.msg.msgutils;

import com.github.artemis.the.gr8.playerstats.utils.MyLogger;

/** A small utility class that helps make enum constant names prettier for output in stat-messages.*/
public final class StringUtils {

    private StringUtils() {
    }

    /** Replace "_" with " " and capitalize each first letter of the input.
     @param input String to prettify, case-insensitive*/
    public static String prettify(String input) {
        if (input == null) return null;
        StringBuilder capitals = new StringBuilder(input.toLowerCase());
        capitals.setCharAt(0, Character.toUpperCase(capitals.charAt(0)));
        while (capitals.indexOf("_") != -1) {
            MyLogger.logHighLevelMsg("Replacing underscores and capitalizing names...");

            int index = capitals.indexOf("_");
            capitals.setCharAt(index + 1, Character.toUpperCase(capitals.charAt(index + 1)));
            capitals.setCharAt(index, ' ');
        }
        return capitals.toString();
    }
}