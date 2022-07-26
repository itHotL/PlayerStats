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

/** This class deals with Bukkit Enumerators. It holds private lists of all
 block-, item-, entity- and statistic-names, and has one big list of all
 possible sub-statistic-entries (block/item/entity). It can give the names
 of all aforementioned enums, check if something is a valid enum constant,
 and turn a name into its corresponding enum constant. */
public final class EnumHandler {

    private static List<String> blockNames;
    private static List<String> itemNames;
    private static List<String> statNames;
    private static List<String> subStatNames;

    public EnumHandler() {
        prepareLists();
    }

    /** Returns all block-names in lowercase */
    public List<String> getBlockNames() {
        return blockNames;
    }

    /** Returns all item-names in lowercase*/
    public List<String> getItemNames() {
        return itemNames;
    }

    /** Returns all statistic-names in lowercase */
    public List<String> getStatNames() {
        return statNames;
    }

    /** Returns the corresponding Material enum constant for an itemName
     @param itemName String, case-insensitive
     @return Material enum constant, uppercase */
    public static @Nullable Material getItemEnum(String itemName) {
        if (itemName == null) return null;

        Material item = Material.matchMaterial(itemName);
        return (item != null && item.isItem()) ? item : null;
    }

    /** Returns the corresponding EntityType enum constant for an entityName
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

    /** Returns the corresponding Material enum constant for a materialName
     @param materialName String, case-insensitive
     @return Material enum constant, uppercase */
    public static @Nullable Material getBlockEnum(String materialName) {
        if (materialName == null) return null;

        Material block = Material.matchMaterial(materialName);
        return (block != null && block.isBlock()) ? block : null;
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

    /** Checks if string is a valid statistic
     @param statName String, case-insensitive */
    public boolean isStatistic(@NotNull String statName) {
        return statNames.contains(statName.toLowerCase());
    }

    /** Checks whether the given String equals the name of an entity-type statistic. */
    public boolean isEntityStatistic(String statName) {
        return statName.equalsIgnoreCase(Statistic.ENTITY_KILLED_BY.toString()) ||
                statName.equalsIgnoreCase(Statistic.KILL_ENTITY.toString());
    }

    /** Checks if this statistic is a subStatEntry, meaning it is a block, item or entity
     @param statName String, case-insensitive*/
    public boolean isSubStatEntry(@NotNull String statName) {
        return subStatNames.contains(statName.toLowerCase());
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