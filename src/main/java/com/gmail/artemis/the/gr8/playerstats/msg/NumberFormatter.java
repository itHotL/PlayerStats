package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import org.bukkit.Statistic;

import java.text.DecimalFormat;

public class NumberFormatter {

    private final DecimalFormat format;

    public NumberFormatter() {
        format = new DecimalFormat();
        format.setGroupingUsed(true);
        format.setGroupingSize(3);
    }

    //TODO deal with unit name after number (add "blocks", etc in appropriate places)
    public String format(String statName, long number) {
        if (EnumHandler.isDistanceStatistic(statName)) {
            return formatDistance(number);  //language-key: "soundCategory.block": "Blocks",
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

    /** The unit of damage-based statistics is 1/10th of a health point (half a heart) by default.
     That would be 1/20th of a heart. This method turns the number into hearts. */
    private String formatDamage(long number) {  //7 statistics
        return format.format(number / 20);
    }

    /** The unit of distance-based statistics is cm by default. This method turns it into blocks. */
    private String formatDistance(long number) {  //15 statistics
        return format.format(number / 100);
    }

    /** The unit of time-based statistics is ticks by default.*/
    private String formatTime(long number) {  //5 statistics
        if (number == 0) {
            return "-";
        }
        StringBuilder output = new StringBuilder();
        long leftover = number / 20;

        if (leftover >= 86400) {
            long days = leftover / 60 / 60 / 24;
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
            long hours = leftover / 60 / 60;
            output.append(hours).append("H ");
            leftover = leftover % (60 * 60);
        }
        if (leftover >= 60) {
            long minutes = leftover / 60;
            output.append(minutes).append("M ");
            leftover = leftover % 60;
        }
        if (leftover > 0) {
            output.append(leftover).append("S");
        }
        return output.toString();
    }
}
