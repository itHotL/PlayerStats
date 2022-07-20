package com.gmail.artemis.the.gr8.playerstats.msg.msgutils;

import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;

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
            MyLogger.replacingUnderscores();

            int index = capitals.indexOf("_");
            capitals.setCharAt(index + 1, Character.toUpperCase(capitals.charAt(index + 1)));
            capitals.setCharAt(index, ' ');
        }
        return capitals.toString();
    }
}