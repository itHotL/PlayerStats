package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StatManager {

    private final Main plugin;
    private final EnumHandler enumHandler;
    private final List<String> statNames;
    private final List<String> entityStatNames;
    private final List<String> subStatEntryNames;

    public StatManager(EnumHandler e, Main p) {
        plugin = p;
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
    public int getStatistic(String statName, String playerName) throws IllegalArgumentException, NullPointerException {
        return getStatistic(statName, null, playerName);
    }

    public int getStatistic(String statName, String subStatEntryName, String playerName) throws IllegalArgumentException, NullPointerException {
        OfflinePlayer player = OfflinePlayerHandler.getOfflinePlayer(playerName);
        if (player == null) throw new NullPointerException("No player called " + playerName + " was found!");

        Statistic stat = getStatistic(statName);
            if (stat != null) {
                switch (stat.getType()) {
                    case UNTYPED -> {
                        return player.getStatistic(stat);
                    }
                    case BLOCK -> {
                        Material block = enumHandler.getBlock(subStatEntryName);
                        if (block == null) throw new NullPointerException(subStatEntryName + " is not a valid block name!");
                        return player.getStatistic(stat, block);
                    }
                    case ENTITY -> {
                        EntityType entity = enumHandler.getEntityType(subStatEntryName);
                        if (entity == null) throw new NullPointerException(subStatEntryName + " is not a valid entity name!");
                        return player.getStatistic(stat, entity);
                    }
                    case ITEM -> {
                        Material item = enumHandler.getItem(subStatEntryName);
                        if (item == null) throw new NullPointerException(subStatEntryName + " is not a valid item name!");
                        return player.getStatistic(stat, item);
                    }
                }
            }
        throw new NullPointerException(statName + " is not a valid statistic name!");
    }

    private Statistic getStatistic(String statName) {
        try {
            return Statistic.valueOf(statName.toUpperCase());
        }
        catch (IllegalArgumentException | NullPointerException exception) {
            logWarnings(exception);
            return null;
        }
    }

    public Statistic.Type getStatType(String statName) {
        try {
            return Statistic.valueOf(statName.toUpperCase()).getType();
        }
        catch (IllegalArgumentException | NullPointerException exception) {
            logWarnings(exception);
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

    public void logWarnings(Exception exception) {
        if (exception instanceof IllegalArgumentException) {
            plugin.getLogger().warning("IllegalArgumentException - this is probably not a valid statistic name!");
        }
        else if (exception instanceof NullPointerException) {
            plugin.getLogger().warning("NullPointerException - no statistic name was provided");
        }
    }
}
