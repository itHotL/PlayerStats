package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.Statistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StatManager {

    private final EnumHandler enumHandler;
    private final List<String> statNames;
    private final List<String> entityStatNames;
    private final List<String> subStatEntryNames;

    public StatManager(EnumHandler e) {
        enumHandler = e;

        statNames = Arrays.stream(Statistic.values()).map(
                Statistic::toString).map(String::toLowerCase).toList();
        entityStatNames = Arrays.stream(Statistic.values()).filter(statistic ->
                statistic.getType().equals(Statistic.Type.ENTITY)).map(
                Statistic::toString).map(String::toLowerCase).collect(Collectors.toList());

        subStatEntryNames = new ArrayList<>();
        subStatEntryNames.addAll(enumHandler.getBlockNames());
        subStatEntryNames.addAll(enumHandler.getEntityTypeNames());
        subStatEntryNames.addAll(enumHandler.getItemNames());
    }

    //returns Statistic enum constant (uppercase) if the input name is valid, otherwise null (param: statName in uppercase)
    public int getStatistic(Statistic stat, String playerName) {
        return OfflinePlayerHandler.getOfflinePlayer(playerName).getStatistic(stat);
    }

    public Statistic.Type getStatType(String statName) {
        try {
            return Statistic.valueOf(statName).getType();
        }
        catch (IllegalArgumentException | NullPointerException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    //checks if string is a valid statistic (param: statName, not case sensitive)
    public boolean isStatistic(String statName) {
        return statNames.contains(statName.toLowerCase());
    }

    public boolean isEntityStatistic(String statName) {
        return entityStatNames.contains(statName.toLowerCase());
    }

    public boolean isSubStatistic(String statName) {
        return subStatEntryNames.contains(statName.toLowerCase());
    }

    //returns the names of all general statistics in lowercase
    public List<String> getStatNames() {
        return statNames;
    }

    public List<String> getEntityStatNames() {
        return entityStatNames;
    }

    //returns all substatnames in lowercase
    public List<String> getSubStatEntryNames() {
        return subStatEntryNames;
    }
}
