package com.artemis.the.gr8.playerstats.core.msg.msgutils;

import com.artemis.the.gr8.playerstats.core.utils.MyLogger;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A small utility class that helps make enum constant
 * names prettier for output in stat-messages.
 */
public final class StringUtils {

    private static final Pattern lowercaseLetterAfterSpace;

    static {
        lowercaseLetterAfterSpace = Pattern.compile("(?<= )[a-z]");
    }

    private StringUtils() {
    }

    /**
     * Replace "_" with " " and capitalize each first letter of the input.
     *
     * @param input String to prettify, case-insensitive
     */
    public static String prettify(String input) {
        if (input == null) return null;
        //TODO remove excessive logging
        MyLogger.logHighLevelMsg("Prettifying [" + input + "]");

        StringBuilder capitals = new StringBuilder(input.toLowerCase(Locale.ENGLISH));
        capitals.setCharAt(0, Character.toUpperCase(capitals.charAt(0)));

        while (capitals.indexOf("_") != -1) {
            int index = capitals.indexOf("_");
            capitals.setCharAt(index, ' ');
            MyLogger.logHighLevelMsg("Replacing underscores: " + capitals);
        }

        Matcher matcher = lowercaseLetterAfterSpace.matcher(capitals);
        while (matcher.find()) {
            int index = matcher.start();
            capitals.setCharAt(index, Character.toUpperCase(capitals.charAt(index)));
            MyLogger.logHighLevelMsg("Capitalizing names: " + capitals);
        }
        return capitals.toString();
    }
}