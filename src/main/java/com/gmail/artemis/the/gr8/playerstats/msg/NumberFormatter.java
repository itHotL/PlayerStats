package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.enums.Unit;

import java.text.DecimalFormat;

public class NumberFormatter {

    private final DecimalFormat format;

    public NumberFormatter() {
        format = new DecimalFormat();
        format.setGroupingUsed(true);
        format.setGroupingSize(3);
    }

    /** Turns the input number into a more readable format depending on its type
     (number-of-times, time-, damage- or distance-based) according to the
     corresponding config settings, and adds commas in groups of 3.*/
    public String format(long number, Unit statUnit) {
        return format(number, statUnit, null);
    }

    public String format(long number, Unit statUnit, Unit smallTimeUnit) {
        if (smallTimeUnit == null) {
            switch (statUnit.getType()) {
                case DISTANCE -> {
                    return formatDistance(number, statUnit);
                }
                case DAMAGE -> {
                    return formatDamage(number, statUnit);
                }
                default -> {
                    return format.format(number);
                }
            }
        } else {
            return formatTime(number, statUnit, smallTimeUnit);
        }
    }

    /** The unit of damage-based statistics is half a heart by default.
     This method turns the number into hearts. */
    private String formatDamage(long number, Unit statUnit) {  //7 statistics
        if (statUnit == Unit.HEART) {
            return format.format(Math.round(number / 2.0));
        } else {
            return format.format(number);
        }
    }

    /** The unit of distance-based statistics is cm by default. This method turns it into blocks by default,
     and turns it into km or leaves it as cm otherwise, depending on the config settings. */
    private String formatDistance(long number, Unit statUnit) {  //15 statistics
        switch (statUnit) {
            case CM -> {
                return format.format(number);
            }
            case MILE -> {
                return format.format(Math.round(number / 160934.4));  //to get from CM to Miles
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
    private String formatTime(long number, Unit bigUnit, Unit smallUnit) {  //5 statistics
        if (number == 0) {
            return "-";
        }
        if (bigUnit == Unit.TICK && smallUnit == Unit.TICK || bigUnit == Unit.NUMBER || smallUnit == Unit.NUMBER) {
            return format.format(number);
        }

        StringBuilder output = new StringBuilder();
        double max = bigUnit.getSeconds();
        double min = smallUnit.getSeconds();
        double leftover = number / 20.0;

        if (isInRange(max, min, 86400) && leftover >= 86400) {
            double days = leftover / 60 / 60 / 24;
            leftover = leftover % (60 * 60 * 24);
            if (smallUnit == Unit.DAY && leftover >= 43200) {
                days++;
                return output.append(format.format(Math.round(days)))
                        .append("d").toString();
            }
            output.append(format.format(Math.round(days)))
                    .append("d ");
        }
        if (isInRange(max, min, 3600) && leftover >= 3600) {
            double hours = leftover / 60 / 60;
            leftover = leftover % (60 * 60);
            if (smallUnit == Unit.HOUR && leftover >= 1800) {
                hours++;
                return output.append(format.format(Math.round(hours)))
                        .append("h").toString();
            }
            output.append(format.format(Math.round(hours)))
                    .append("h ");
        }
        if (isInRange(max, min, 60) && leftover >= 60) {
            double minutes = leftover / 60;
            leftover = leftover % 60;
            if (smallUnit == Unit.MINUTE && leftover >= 30) {
                minutes++;
                return output.append(format.format(Math.round(minutes)))
                        .append("m").toString();
            }
            output.append(format.format(Math.round(minutes)))
                    .append("m ");
        }
        if (isInRange(max, min, 1) && leftover > 0) {
            output.append(format.format(Math.round(leftover)))
                    .append("s");
        }
        return output.toString();
    }

    private boolean isInRange(double bigUnit, double smallUnit, double unitToEvaluate) {
        return bigUnit >= unitToEvaluate && unitToEvaluate >= smallUnit;
    }
}