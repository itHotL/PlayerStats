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
    SECOND (Type.TIME, 1),
    MINUTE (Type.TIME, 60),
    HOUR (Type.TIME, 3600),
    DAY (Type.TIME, 86400),
    WEEK (Type.TIME, 604800);

    private final Type type;
    private final int seconds;

    Unit(Type type) {
        this(type, -1);
    }

    Unit(Type type, int seconds) {
        this.type = type;
        this.seconds = seconds;
    }

    public Type getType() {
        return type;
    }

    /** Returns the given Unit in seconds, or -1 if the Unit is not a TimeUnit.*/
    public int getTimeInSeconds() {
        return this.seconds;
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

    public static @NotNull Unit fromString(String unitName) {
        switch (unitName.toLowerCase()) {
            case "cm" -> {
                return Unit.CM;
            }
            case "m", "block", "blocks" -> {
                return Unit.BLOCK;
            }
            case "mile", "miles" -> {
                return Unit.MILE;
            }
            case "km" -> {
                return Unit.KM;
            }
            case "hp" -> {
                return Unit.HP;
            }
            case "heart", "hearts" -> {
                return Unit.HEART;
            }
            case "week", "weeks" -> {
                return Unit.WEEK;
            }
            case "day", "days" -> {
                return Unit.DAY;
            }
            case "hour", "hours" -> {
                return Unit.HOUR;
            }
            case "minute", "minutes", "min" -> {
                return Unit.MINUTE;
            }
            case "second", "seconds", "sec" -> {
                return Unit.SECOND;
            }
            case "tick", "ticks" -> {
                return Unit.TICK;
            }
            default -> {
                return Unit.NUMBER;
            }
        }
    }

    public static @NotNull Type fromStatistic(Statistic statistic) {
        return fromStatName(statistic.toString());
    }

    /** Returns the Unit.Type of this Statistic, which can be Untyped, Distance, Damage, or Time.
     @param statName the name of the Statistic enum constant in String*/
    public static @NotNull Type fromStatName(String statName) {
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

    public enum Type{
        DAMAGE, //7 statistics
        DISTANCE, //15 statistics
        TIME, //5 statistics
        UNTYPED;

        Type() {
        }
    }
}
