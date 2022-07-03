package com.gmail.artemis.the.gr8.playerstats.enums;

import org.bukkit.Statistic;
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

    private final Type type;

    Unit() {
        this(Type.UNTYPED);
    }

    Unit(Type type) {
        this.type = type;
    }

    public Type type() {
        return type;
    }

    public static @NotNull Type getType(Statistic statistic) {
        return getType(statistic.toString());
    }

    /** Returns the Unit.Type of this Statistic, which can be Untyped, Distance, Damage, or Time.
     @param statName the name of the Statistic enum constant in String*/
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

    /** Returns a pretty name belonging to this enum constant. If the Unit is
     NUMBER, it will return an empty String. */
    public @NotNull String getName() throws NullPointerException {
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
            case NUMBER -> {
                return "";
            }
            default ->
                throw new NullPointerException("Trying to get the name of an enum constant that does not exist!");
        }
    }

    public enum Type{
        DAMAGE, //7 statistics
        DISTANCE, //15 statistics
        TIME, //5 statistics
        UNTYPED;

        Type() {
        }
    }
}
