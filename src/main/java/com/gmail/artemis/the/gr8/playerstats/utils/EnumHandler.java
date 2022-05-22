package com.gmail.artemis.the.gr8.playerstats.utils;

import com.gmail.artemis.the.gr8.playerstats.Main;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumHandler {

    private final List<String> blockNames;
    private final List<String> entityTypeNames;
    private final List<String> itemNames;
    private final List<String> statNames;
    private final List<String> entityStatNames;
    private final List<String> subStatEntryNames;

    public EnumHandler() {
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

        subStatEntryNames = new ArrayList<>();
        subStatEntryNames.addAll(getBlockNames());
        subStatEntryNames.addAll(getEntityTypeNames());
        subStatEntryNames.addAll(getItemNames());
    }

    //checks whether the provided string is a valid item
    public boolean isItem(String itemName) {
        return itemNames.contains(itemName.toLowerCase());
    }

    //returns corresponding item enum constant (uppercase), otherwise null (param: itemName, not case sensitive)
    public Material getItem(String itemName) {
        Material material = Material.matchMaterial(itemName);
        if (material != null) {
            return material;
        }
        else {
            throw new IllegalArgumentException(itemName + " is not a valid Material!");
        }
    }

    //returns all item names in lowercase
    public List<String> getItemNames() {
        return itemNames;
    }

    //checks whether the provided string is a valid entity
    public boolean isEntityType(String entityName) {
        return entityTypeNames.contains(entityName.toLowerCase());
    }

    //returns EntityType enum constant (uppercase) if the input name is valid
    public EntityType getEntityType(@NotNull String entityName) {
        try {
            return EntityType.valueOf(entityName.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(entityName + " is not a valid EntityType!");
        }
    }

    //returns all entitytype names in lowercase
    public List<String> getEntityTypeNames() {
        return entityTypeNames;
    }

    //checks whether the provided string is a valid block
    public boolean isBlock(String materialName) {
        return blockNames.contains(materialName.toLowerCase());
    }

    //returns corresponding block enum constant (uppercase), otherwise null (param: materialName, not case sensitive)
    public Material getBlock(String materialName) throws IllegalArgumentException {
        Material material = Material.matchMaterial(materialName);
        if (material != null) {
            return material;
        }
        else {
            throw new IllegalArgumentException(materialName + " is not a valid Material!");
        }
    }

    //returns all block names in lowercase
    public List<String> getBlockNames() {
        return blockNames;
    }

    //returns the statistic enum constant, or null if non-existent (param: statName, not case sensitive)
    public Statistic getStatEnum(@NotNull String statName) throws IllegalArgumentException {
        try {
            return Statistic.valueOf(statName.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(statName + " is not a valid statistic!");
        }
    }

    //gets the type of the statistic from the string, otherwise returns null (param: statName, not case sensitive)
    public Statistic.Type getStatType(String statName) {
        try {
            return Statistic.valueOf(statName.toUpperCase()).getType();
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(statName + " is not a valid statistic name!");
        }
    }

    //checks if string is a valid statistic (param: statName, not case sensitive)
    public boolean isStatistic(String statName) {
        return statNames.contains(statName.toLowerCase());
    }

    //returns the names of all general statistics in lowercase
    public List<String> getStatNames() {
        return statNames;
    }

    //returns all statistics that have type entities, in lowercase
    public List<String> getEntityStatNames() {
        return entityStatNames;
    }

    //checks if this statistic is a subStatEntry, meaning it is a block, item or entity (param: statName, not case sensitive)
    public boolean isSubStatEntry(String statName) {
        return subStatEntryNames.contains(statName.toLowerCase());
    }

    //checks whether a subStatEntry is of the type that the statistic requires
    public boolean isValidStatEntry(String statName, String subStatEntry) {
        Statistic stat = getStatEnum(statName);
        return (stat != null && isMatchingSubStatEntry(stat, subStatEntry));
    }

    //returns true if subStatEntry matches the type the stat requires, or if stat is untyped and subStatEntry is null
    private boolean isMatchingSubStatEntry(@NotNull Statistic stat, String subStatEntry) {
        switch (stat.getType()) {
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
                return subStatEntry==null;
            }
            default -> {
                return false;
            }
        }
    }
}
