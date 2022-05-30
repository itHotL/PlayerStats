package com.gmail.artemis.the.gr8.playerstats.utils;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumHandler {

    private final static List<String> blockNames;
    private final static List<String> entityTypeNames;
    private final static List<String> itemNames;
    private final static List<String> statNames;
    private final static List<String> entityStatNames;
    private final static List<String> subStatEntryNames;

    static{
        blockNames = Arrays.stream(Material.values()).filter(
                Material::isBlock).map(Material::toString).map(String::toLowerCase).toList();
        entityTypeNames = Arrays.stream(EntityType.values()).map(
                EntityType::toString).map(String::toLowerCase).toList();
        itemNames = Arrays.stream(Material.values()).filter(
                Material::isItem).map(Material::toString).map(String::toLowerCase).toList();
        statNames = Arrays.stream(Statistic.values()).map(
                Statistic::toString).map(String::toLowerCase).toList();
        entityStatNames = Arrays.stream(Statistic.values()).filter(statistic ->
                statistic.getType().equals(Statistic.Type.ENTITY)).map(
                Statistic::toString).map(String::toLowerCase).collect(Collectors.toList());

        subStatEntryNames = Stream.of(blockNames, entityTypeNames, itemNames).flatMap(Collection::stream).toList();
    }

    private EnumHandler() {
    }

    //checks whether the provided string is a valid item
    public static boolean isItem(String itemName) {
        return itemNames.contains(itemName.toLowerCase());
    }

    //returns corresponding item enum constant (uppercase), otherwise throws exception (param: itemName, case insensitive)
    public static Material getItem(String itemName) throws IllegalArgumentException {
        Material material = Material.matchMaterial(itemName);
        if (material != null) {
            return material;
        }
        else {
            throw new IllegalArgumentException(itemName + " is not a valid Material!");
        }
    }

    //returns all item names in lowercase
    public static List<String> getItemNames() {
        return itemNames;
    }

    //checks whether the provided string is a valid entity
    public static boolean isEntityType(String entityName) {
        return entityTypeNames.contains(entityName.toLowerCase());
    }

    //returns EntityType enum constant (uppercase) if the input name is valid, otherwise throws exception (param: entityName, case insensitive)
    public static EntityType getEntityType(@NotNull String entityName) throws IllegalArgumentException {
        try {
            return EntityType.valueOf(entityName.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(entityName + " is not a valid EntityType! ", e.getCause());
        }
    }

    //returns all entitytype names in lowercase
    public static List<String> getEntityTypeNames() {
        return entityTypeNames;
    }

    //checks whether the provided string is a valid block
    public static boolean isBlock(String materialName) {
        return blockNames.contains(materialName.toLowerCase());
    }

    //returns corresponding block enum constant (uppercase), otherwise throws exception (param: materialName, case insensitive)
    public static Material getBlock(String materialName) throws IllegalArgumentException {
        Material material = Material.matchMaterial(materialName);
        if (material != null) {
            return material;
        }
        else {
            throw new IllegalArgumentException(materialName + " is not a valid Material!");
        }
    }

    //returns all block names in lowercase
    public static List<String> getBlockNames() {
        return blockNames;
    }

    //returns the statistic enum constant, otherwise throws exception (param: statName, case insensitive)
    public static Statistic getStatEnum(@NotNull String statName) throws IllegalArgumentException {
        try {
            return Statistic.valueOf(statName.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(statName + " is not a valid statistic!");
        }
    }

    //gets the type of the statistic from the string, otherwise throws exception (param: statName, case insensitive)
    public static Statistic.Type getStatType(@NotNull String statName) throws IllegalArgumentException {
        try {
            return Statistic.valueOf(statName.toUpperCase()).getType();
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(statName + " is not a valid statistic name!");
        }
    }

    //checks if string is a valid statistic (param: statName, not case sensitive)
    public static boolean isStatistic(String statName) {
        return statNames.contains(statName.toLowerCase());
    }

    //returns the names of all general statistics in lowercase
    public static List<String> getStatNames() {
        return statNames;
    }

    //returns all statistics that have type entities, in lowercase
    public static List<String> getEntityStatNames() {
        return entityStatNames;
    }

    //checks if this statistic is a subStatEntry, meaning it is a block, item or entity (param: statName, not case sensitive)
    public static boolean isSubStatEntry(String statName) {
        return subStatEntryNames.contains(statName.toLowerCase());
    }

    //checks whether a subStatEntry is of the type that the statistic requires
    public static boolean isValidStatEntry(@NotNull String statName, String subStatEntry) {
        try {
            return isValidStatEntry(getStatType(statName), subStatEntry);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isValidStatEntry(Statistic.Type statType, String subStatEntry) {
        return (statType != null) && isMatchingSubStatEntry(statType, subStatEntry);
    }

    //returns true if subStatEntry matches the type the stat requires, or if stat is untyped and subStatEntry is null
    private static boolean isMatchingSubStatEntry(@NotNull Statistic.Type statType, String subStatEntry) {
        switch (statType) {
            case ENTITY -> {
                return subStatEntry != null && isEntityType(subStatEntry);
            }
            case ITEM -> {
                return subStatEntry != null && isItem(subStatEntry);
            }
            case BLOCK -> {
                return subStatEntry != null && isBlock(subStatEntry);
            }
            case UNTYPED -> {
                return subStatEntry == null;
            }
            default -> {
                return false;
            }
        }
    }
}
