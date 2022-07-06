package com.gmail.artemis.the.gr8.playerstats.utils;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumHandler {

    private final static List<String> blockNames;
    private final static List<String> entityNames;
    private final static List<String> itemNames;
    private final static List<Material> items;
    private final static List<String> statNames;
    private final static List<String> entitySubStatNames;
    private final static List<String> subStatNames;

    static {
        blockNames = Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .map(Material::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        entityNames = Arrays.stream(EntityType.values())
                .map(EntityType::toString)
                .map(String::toLowerCase)
                .filter(entityName -> !entityName.equalsIgnoreCase("unknown"))
                .collect(Collectors.toList());

        itemNames = Arrays.stream(Material.values())
                .filter(Material::isItem)
                .map(Material::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        items = Arrays.stream(Material.values())
                .filter(Material::isItem)
                .collect(Collectors.toList());

        statNames = Arrays.stream(Statistic.values())
                .map(Statistic::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        entitySubStatNames = Arrays.stream(Statistic.values())
                .filter(statistic -> statistic.getType().equals(Statistic.Type.ENTITY))
                .map(Statistic::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        subStatNames = Stream.of(blockNames, entityNames, itemNames)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private EnumHandler() {
    }

    /** Returns all item names in lowercase */
    public static List<Material> getItems() {
        return items;
    }

    public static List<String> getItemNames() {
        return itemNames;
    }

    /** Returns corresponding item enum constant for an itemName
     @param itemName String, case-insensitive
     @return Material enum constant, uppercase */
    public static @Nullable Material getItemEnum(String itemName) {
        if (itemName == null) return null;

        Material item = Material.matchMaterial(itemName);
        return (item != null && item.isItem()) ? item : null;
    }

    /** Returns all entitytype names in lowercase */
    public static List<String> getEntityNames() {
        return entityNames;
    }

    /** Returns corresponding EntityType enum constant for an entityName
     @param entityName String, case-insensitive
     @return EntityType enum constant, uppercase */
    public static @Nullable EntityType getEntityEnum(String entityName) {
        try {
            return EntityType.valueOf(entityName.toUpperCase());
        }
        catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    /** Returns all block names in lowercase */
    public static List<String> getBlockNames() {
        return blockNames;
    }

    /** Returns corresponding block enum constant for a materialName
     @param materialName String, case-insensitive
     @return Material enum constant, uppercase */
    public static @Nullable Material getBlockEnum(String materialName) {
        if (materialName == null) return null;

        Material block = Material.matchMaterial(materialName);
        return (block != null && block.isBlock()) ? block : null;
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

    /** Returns the statistic enum constant, or null if that failed.
     @param statName String, case-insensitive */
    public static @Nullable Statistic getStatEnum(@NotNull String statName)  {
        try {
            return Statistic.valueOf(statName.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return null;
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
}