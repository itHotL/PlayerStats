package com.artemis.the.gr8.playerstats.msg.msgutils;

import com.artemis.the.gr8.playerstats.enums.Unit;

import java.text.DecimalFormat;

/**
 * A utility class that formats statistic numbers into something more readable.
 * It transforms numbers of {@link Unit.Type} Time, Damage, and Distance into numbers
 * that are easier to understand (for example: from ticks to hours) and adds commas
 * to break up large numbers.
 */
public final class NumberFormatter {

    private final DecimalFormat format;

    public NumberFormatter() {
        format = new DecimalFormat();
        format.setGroupingUsed(true);
        format.setGroupingSize(3);
    }

    /**
     * Turns the input number into a more readable format depending on its type
     * (number-of-times, time-, damage- or distance-based) according to the
     * corresponding config settings, and adds commas in groups of 3.
     */
    public String formatNumber(long number) {
        return format.format(number);
    }

    /**
     * The unit of damage-based statistics is half a heart by default.
     * This method turns the number into hearts.
     */
    public String formatDamageNumber(long number, Unit statUnit) {  //7 statistics
        if (statUnit == Unit.HEART) {
            return format.format(Math.round(number / 2.0));
        } else {
            return format.format(number);
        }
    }

    /**
     * The unit of distance-based statistics is cm by default.
     * This method turns it into blocks by default,
     * and turns it into km or leaves it as cm otherwise,
     * depending on the config settings.
     */
    public String formatDistanceNumber(long number, Unit statUnit) {  //15 statistics
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

    /** The unit of time-based statistics is ticks by default.
     *
     * @return a String with the form "1D 2H 3M 4S"
     * (depending on the Unit range selected)
     */
    public String formatTimeNumber(long number, Unit biggestUnit, Unit smallestUnit) {  //5 statistics
        if (number <= 0) {
            return "-";
        }
        if (biggestUnit == Unit.TICK && smallestUnit == Unit.TICK || biggestUnit == Unit.NUMBER || smallestUnit == Unit.NUMBER) {
            return format.format(number);
        }

        Unit currUnit = biggestUnit;
        int leftoverSeconds = (int) Math.round(number / 20.0);
        StringBuilder output = new StringBuilder();

        while(currUnit != null){
            //Define amount of units
            int amount = 0;

            //Current unit is equal to smallest unit, in this case round the remainder
            if(currUnit == smallestUnit){
                amount = (int) Math.round(leftoverSeconds / currUnit.getSeconds());
            }

            //Leftover amount of seconds is greater than current unit, we can extract x units
            else if(leftoverSeconds >= currUnit.getSeconds()){
                amount = (int) Math.floor(leftoverSeconds / currUnit.getSeconds());
            }

            //We did not have enough leftover to fill a unit
            else{
                if(output.toString().length() != 0){
                    output.append(" 0").append(currUnit.getShortLabel());
                }
                currUnit = currUnit.getSmallerUnit(1);
                continue;
            }

            //Calculate new leftover
            leftoverSeconds = leftoverSeconds - (int)(amount * currUnit.getSeconds());

            //Append new values
            if(output.toString().length() != 0){
                output.append(" ");
            }
            output.append(amount).append(currUnit.getShortLabel());

            //Check if we need to break
            if(currUnit == smallestUnit || leftoverSeconds == 0){
                break;
            }

            //Lower current unit by 1
            currUnit = currUnit.getSmallerUnit(1);
        }

        return output.toString();
    }
}