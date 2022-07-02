package com.gmail.artemis.the.gr8.playerstats.enums;

import org.jetbrains.annotations.NotNull;

public enum Unit {
    NUMBER (Type.UNTYPED),
    CM (Type.DISTANCE),
    BLOCK (Type.DISTANCE),
    MILE (Type.DISTANCE),
    KM (Type.DISTANCE),
    HP (Type.DAMAGE),
    HEART (Type.DAMAGE),
    TICK (Type.TIME),
    SECOND (Type.TIME),
    MINUTE (Type.TIME),
    HOUR (Type.TIME),
    DAY (Type.TIME),
    WEEK (Type.TIME);


    Unit(Type type) {
    }

    public static @NotNull Type getType(String statName) {
        String name = statName.toLowerCase();
        if (name.contains("one_cm")) {
            return Type.DISTANCE;
        } else if (name.contains("damage")) {
            return Type.DAMAGE;
        } else if (name.contains("time") || name.contains("one_minute")) {
            return Type.TIME;
        } else {
            return Type.UNTYPED;
        }
    }

    public @NotNull String getName() {
        switch (this) {
            case CM -> {
                return "cm";
            }
            case BLOCK -> {
                return "Blocks";
            }
            case MILE -> {
                return "Miles";
            }
            case KM -> {
                return "km";
            }
            case HP -> {
                 return "HP";
            }
            case HEART -> {
                 return "Hearts";
            }
            case TICK -> {
                 return "ticks";
            }
            case SECOND -> {
                 return "seconds";
            }
            case MINUTE -> {
                 return "minutes";
            }
            case DAY -> {
                 return "days";
            }
            case HOUR -> {
                 return "hours";
            }
            case WEEK -> {
                 return "weeks";
            }
            default ->
                throw new NullPointerException("Trying to get the name of an enum constant that does not exist!");
        }
    }

    public static enum Type{
        DAMAGE, //7 statistics
        DISTANCE, //15 statistics
        TIME, //5 statistics
        UNTYPED;

        private Type() {
        }
    }
}
