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
    private final static List<String> entityNames;
    private final static List<String> itemNames;
    private final static List<String> statNames;
    private final static List<String> entitySubStatNames;
    private final static List<String> subStatNames;

    static{
        blockNames = Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .map(Material::toString)
                .map(String::toLowerCase)
                .toList();

        entityNames = Arrays.stream(EntityType.values())
                .map(EntityType::toString)
                .map(String::toLowerCase)
                .filter(entityName -> !entityName.equalsIgnoreCase("unknown"))
                .toList();

        itemNames = Arrays.stream(Material.values())
                .filter(Material::isItem)
                .map(Material::toString)
                .map(String::toLowerCase)
                .toList();

        statNames = Arrays.stream(Statistic.values())
                .map(Statistic::toString)
                .map(String::toLowerCase)
                .toList();

        entitySubStatNames = Arrays.stream(Statistic.values())
                .filter(statistic -> statistic.getType().equals(Statistic.Type.ENTITY))
                .map(Statistic::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        subStatNames = Stream.of(blockNames, entityNames, itemNames)
                .flatMap(Collection::stream)
                .toList();
    }

    private EnumHandler() {
    }

    /** Checks whether the provided string is a valid item
     @param itemName String, case-insensitive */
    public static boolean isItem(@NotNull String itemName) {
        return itemNames.contains(itemName.toLowerCase());
    }

    /** Returns all item names in lowercase */
    public static List<String> getItemNames() {
        return itemNames;
    }

    /** Returns corresponding item enum constant for an itemName
     @param itemName String, case-insensitive
     @return Material enum constant, uppercase */
    public static @NotNull Material getItemEnum(String itemName) throws IllegalArgumentException {
        Material material = Material.matchMaterial(itemName);
        if (material != null) {
            return material;
        }
        else {
            throw new IllegalArgumentException(itemName + " is not a valid Material!");
        }
    }

    /** Checks whether the provided string is a valid entity */
    public static boolean isEntity(@NotNull String entityName) {
        return entityNames.contains(entityName.toLowerCase());
    }

    /** Returns all entitytype names in lowercase */
    public static List<String> getEntityNames() {
        return entityNames;
    }

    /** Returns corresponding EntityType enum constant for an entityName
     @param entityName String, case-insensitive
     @return EntityType enum constant, uppercase */
    public static EntityType getEntityEnum(@NotNull String entityName) throws IllegalArgumentException {
        try {
            return EntityType.valueOf(entityName.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(entityName + " is not a valid entity! ", e.getCause());
        }
    }

    /** Checks whether the provided string is a valid block
     @param materialName String, case-insensitive */
    public static boolean isBlock(@NotNull String materialName) {
        return blockNames.contains(materialName.toLowerCase());
    }

    /** Returns all block names in lowercase */
    public static List<String> getBlockNames() {
        return blockNames;
    }

    /** Returns corresponding block enum constant for a materialName
     @param materialName String, case-insensitive
     @return Material enum constant, uppercase */
    public static @NotNull Material getBlockEnum(String materialName) throws IllegalArgumentException {
        Material material = Material.matchMaterial(materialName);
        if (material != null) {
            return material;
        }
        else {
            throw new IllegalArgumentException(materialName + " is not a valid Material!");
        }
    }

    /** Checks if string is a valid statistic
     @param statName String, case-insensitive */
    public static boolean isStatistic(@NotNull String statName) {
        return statNames.contains(statName.toLowerCase());
    }

    /** Returns the names of all general statistics in lowercase */
    public static List<String> getStatNames() {
        return statNames;
    }

    /** Returns the statistic enum constant, otherwise throws exception
     @param statName String, case-insensitive */
    public static Statistic getStatEnum(@NotNull String statName) throws IllegalArgumentException {
        try {
            return Statistic.valueOf(statName.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(statName + " is not a valid statistic!");
        }
    }

    /** Gets the type of the statistic from the string
     @param statName String, case-insensitive
     @return Statistic.Type */
    public static Statistic.@NotNull Type getStatType(@NotNull String statName) throws IllegalArgumentException {
        try {
            return Statistic.valueOf(statName.toUpperCase()).getType();
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(statName + " is not a valid statistic name!");
        }
    }

    /** Checks if this statistic is a subStatEntry, meaning it is a block, item or entity
     @param statName String, case-insensitive*/
    public static boolean isSubStatEntry(@NotNull String statName) {
        return subStatNames.contains(statName.toLowerCase());
    }

    /** Returns all statistics that have type entities, in lowercase */
    public static List<String> getEntitySubStatNames() {
        return entitySubStatNames;
    }

    /** Checks whether a subStatEntry is of the type that the statistic requires
     @param statType the Type of the Statistic to check
     @param subStatEntry a String representing the subStatEntry (entity, item or block)*/
    public static boolean isValidStatEntry(Statistic.Type statType, String subStatEntry) {
        return (statType != null) && isMatchingSubStatEntry(statType, subStatEntry);
    }

    /** Returns true if subStatEntry matches the type the stat requires, or if stat is untyped and subStatEntry is null
     @param statType the Type of the Statistic to check
     @param subStatEntry a String representing the subStatEntry (entity, item or block) */
    private static boolean isMatchingSubStatEntry(@NotNull Statistic.Type statType, String subStatEntry) {
        switch (statType) {
            case ENTITY -> {
                return subStatEntry != null && isEntity(subStatEntry);
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
