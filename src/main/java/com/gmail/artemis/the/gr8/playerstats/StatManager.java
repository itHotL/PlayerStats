package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import org.bukkit.Statistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StatManager {

    //returns Statistic enum constant (uppercase) if the input name is valid, otherwise null (param: statName in uppercase)
    public static Statistic getStatistic(String statName) {
        Statistic stat = null;
        try {
            stat = Statistic.valueOf(statName);
        }
        catch (IllegalArgumentException | NullPointerException exception) {
            exception.printStackTrace();
        }
        return stat;
    }

    //returns the names of all general statistics in lowercase
    public static List<String> getStatNames() {
        return Arrays.stream(Statistic.values()).map(
                Statistic::toString).map(String::toLowerCase).toList();
    }

    public static List<String> getEntityStatNames() {
        return Arrays.stream(Statistic.values()).filter(statistic ->
                statistic.getType().equals(Statistic.Type.ENTITY)).map(
                Statistic::toString).map(String::toLowerCase).collect(Collectors.toList());
    }

    //returns all substatnames in lowercase
    public static List<String> getValidSubStatEntries() {
        List<String> subStatNames = new ArrayList<>();
        subStatNames.addAll(EnumHandler.getBlockNames());
        subStatNames.addAll(EnumHandler.getEntityNames());
        subStatNames.addAll(EnumHandler.getItemNames());
        return subStatNames;
    }
}
