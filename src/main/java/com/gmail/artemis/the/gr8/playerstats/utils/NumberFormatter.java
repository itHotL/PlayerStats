package com.gmail.artemis.the.gr8.playerstats.utils;

import java.text.DecimalFormat;

public class NumberFormatter {

    private static final DecimalFormat format;

    static{
        format = new DecimalFormat();
        format.setGroupingUsed(true);
        format.setGroupingSize(3);
    }
    private NumberFormatter(){
    }

    public static String format(long number) {
        return format.format(number);
    }
}