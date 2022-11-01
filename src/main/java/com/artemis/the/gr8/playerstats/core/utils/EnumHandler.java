package com.artemis.the.gr8.playerstats.core.utils;

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

/**
 * This class deals with Bukkit Enumerators. It holds private lists of all
 * block-, item-, entity- and statistic-names, and has one big list of all
 * possible sub-statistic-entries (block/item/entity). It can give the names
 * of all aforementioned enums, check if something is a valid enum constant,
 * and turn a name into its corresponding enum constant.
 */
public final class EnumHandler {

    private static volatile EnumHandler instance;
    private static List<String> blockNames;
    private static List<String> itemNames;
    private static List<String> statNames;
    private static List<String> subStatNames;

    private EnumHandler() {
        prepareLists();
    }

    public static EnumHandler getInstance() {
        EnumHandler localVar = instance;
        if (localVar != null) {
            return localVar;
        }

        synchronized (EnumHandler.class) {
            if (instance == null) {
                instance = new EnumHandler();
            }
            return instance;
        }
    }

    /**
     * Returns all block-names in lowercase.
     *
     * @return the List
     */
    public List<String> getAllBlockNames() {
        return blockNames;
    }

    /**
     * Returns all item-names in lowercase.
     *
     * @return the List
     */
    public List<String> getAllItemNames() {
        return itemNames;
    }

    /**
     * Returns all statistic-names in lowercase.
     *
     * @return the List
     */
    public List<String> getAllStatNames() {
        return statNames;
    }

    /**
     * Returns the corresponding Material enum constant for an itemName.
     *
     * @param itemName String (case-insensitive)
     * @return Material enum constant (uppercase), or null if none
     * can be found
     */
    public @Nullable Material getItemEnum(String itemName) {
        if (itemName == null) return null;

        Material item = Material.matchMaterial(itemName);
        return (item != null && item.isItem()) ? item : null;
    }

    /**
     * Returns the corresponding EntityType enum constant for an entityName.
     *
     * @param entityName String (case-insensitive)
     * @return EntityType enum constant (uppercase), or null if none
     * can be found
     */
    public @Nullable EntityType getEntityEnum(String entityName) {
        try {
            return EntityType.valueOf(entityName.toUpperCase());
        }
        catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    /**
     * Returns the corresponding Material enum constant for a materialName.
     *
     * @param materialName String (case-insensitive)
     * @return Material enum constant (uppercase), or null if none
     * can be found
     */
    public @Nullable Material getBlockEnum(String materialName) {
        if (materialName == null) return null;

        Material block = Material.matchMaterial(materialName);
        return (block != null && block.isBlock()) ? block : null;
    }

    /**
     * Returns the statistic enum constant, or null if that failed.
     *
     * @param statName String (case-insensitive)
     * @return the Statistic enum constant, or null
     */
    public @Nullable Statistic getStatEnum(@NotNull String statName)  {
        try {
            return Statistic.valueOf(statName.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Checks if string is a valid {@link Statistic}.
     *
     * @param statName the String to check (case-insensitive)
     * @return true if this String is a valid Statistic
     */
    public boolean isStatistic(@NotNull String statName) {
        return statNames.contains(statName.toLowerCase());
    }

    /**
     * Checks whether the given String equals the name of a
     * {@link Statistic} of Type.Entity.
     *
     * @param statName the String to check (case-insensitive)
     * @return true if this String is a Statistic of Type.Entity
     */
    public boolean isEntityStatistic(@NotNull String statName) {
        return statName.equalsIgnoreCase(Statistic.ENTITY_KILLED_BY.toString()) ||
                statName.equalsIgnoreCase(Statistic.KILL_ENTITY.toString());
    }

    /**
     * Checks if this statistic is a subStatEntry, meaning it is a block,
     * item or entity.
     *
     * @param statName the String to check (case-insensitive)
     * @return true if this String is a Statistic that is not
     * of Type.Untyped
     */
    public boolean isSubStatEntry(@NotNull String statName) {
        return subStatNames.contains(statName.toLowerCase());
    }

    /**
     * Gets the name of the given Statistic.Type
     *
     * @param statType the Type of the Statistic to check
     * @return "block", "entity", "item", or "sub-statistic" if the
     * provided Type is null.
     */
    public String getSubStatTypeName(Statistic.Type statType) {
        String subStat = "sub-statistic";
        if (statType == null) return subStat;
        switch (statType) {
            case BLOCK -> subStat = "block";
            case ENTITY -> subStat = "entity";
            case ITEM -> subStat = "item";
        }
        return subStat;
    }

    private void prepareLists() {
        List<String> entityNames = Arrays.stream(EntityType.values())
                .map(EntityType::toString)
                .map(String::toLowerCase)
                .filter(entityName -> !entityName.equalsIgnoreCase("unknown"))
                .collect(Collectors.toList());

        blockNames = Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .map(Material::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        itemNames = Arrays.stream(Material.values())
                .filter(Material::isItem)
                .map(Material::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        subStatNames = Stream.of(blockNames, entityNames, itemNames)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

        statNames = Arrays.stream(Statistic.values())
                .map(Statistic::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }
}