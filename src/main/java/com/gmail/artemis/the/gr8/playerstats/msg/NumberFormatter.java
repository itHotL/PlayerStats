package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.enums.Unit;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;

import java.text.DecimalFormat;

public class NumberFormatter {

    private static ConfigHandler config;
    private final DecimalFormat format;

    public NumberFormatter(ConfigHandler c) {
        config = c;

        format = new DecimalFormat();
        format.setGroupingUsed(true);
        format.setGroupingSize(3);
    }

    public String format(String statName, long number) {
        if (EnumHandler.isDistanceStatistic(statName)) {
            return formatDistance(number);
        }
        else if (EnumHandler.isDamageStatistic(statName)) {
            return formatDamage(number);
        }
        else if (EnumHandler.isTimeStatistic(statName)) {
            return formatTime(number);
        }
        else {
            return format.format(number);
        }
    }

    /** The unit of damage-based statistics is half a heart by default.
     This method turns the number into hearts. */
    private String formatDamage(long number) {  //7 statistics
        return format.format(Math.round(number / 2.0));
    }

    //TODO add cm and km
    /** The unit of distance-based statistics is cm by default. This method turns it into blocks by default,
     and turns it into km or leaves it as cm otherwise, depending on the config settings. */
    private String formatDistance(long number) {  //15 statistics
        Unit unit = config.getDistanceUnit();
        switch (unit) {
            case CM -> {
                return format.format(number);
            }
            case KM -> {
                return format.format(Math.round(number / 100000.0));  //divide by 100 to get M, divide by 1000 to get KM
            }
            default -> {
                return format.format(Math.round(number / 100.0));
            }
        }
    }

    /** The unit of time-based statistics is ticks by default.*/
    private String formatTime(long number) {  //5 statistics
        if (number == 0) {
            return "-";
        }
        StringBuilder output = new StringBuilder();
        double leftover = number / 20.0;

        if (leftover >= 86400) {
            double days = leftover / 60 / 60 / 24;
            if (days > 999) {
                output.append(format.format(days));
            }
            else {
                output.append(days);
            }
            output.append("D ");
            leftover = leftover % (60 * 60 * 24);
        }
        if (leftover >= 3600) {
            double hours = leftover / 60 / 60;
            output.append(hours).append("H ");
            leftover = leftover % (60 * 60);
        }
        if (leftover >= 60) {
            double minutes = leftover / 60;
            output.append(minutes).append("M ");
            leftover = leftover % 60;
        }
        if (leftover > 0) {
            output.append(leftover).append("S");
        }
        return output.toString();
    }
}
