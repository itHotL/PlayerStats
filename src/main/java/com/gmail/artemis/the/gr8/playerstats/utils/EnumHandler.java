package com.gmail.artemis.the.gr8.playerstats.utils;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnumHandler {

    private EnumHandler() {
    }

    //returns corresponding item enum constant, otherwise null (param: itemName, can be lowercase)
    public static Material getItem(String itemName) {
        return Material.matchMaterial(itemName);
    }

    //returns all item names in lowercase
    public static List<String> getItemNames() {
        return Arrays.stream(Material.values()).filter(Material::isItem).map(Material::toString).map(String::toLowerCase).toList();
    }

    //returns EntityType enum constant if the input name is valid, otherwise null (param: entityName in uppercase)
    public static EntityType getEntityType(String entityName) {
        EntityType entityType = null;
        try {
            entityType = EntityType.valueOf(entityName);
        }
        catch (IllegalArgumentException | NullPointerException exception) {
            exception.printStackTrace();
        }
        return entityType;
    }

    //returns all entitytype names in lowercase
    public static List<String> getEntityNames() {
        return Arrays.stream(EntityType.values()).map(EntityType::toString).map(String::toLowerCase).toList();
    }

    //returns corresponding block enum constant, otherwise null (param: materialName, can be lowercase)
    public static Material getBlock(String materialName) {
        return Material.matchMaterial(materialName);
    }

    //returns all block names in lowercase
    public static List<String> getBlockNames() {
        return Arrays.stream(Material.values()).filter(Material::isBlock).map(Material::toString).map(String::toLowerCase).toList();
    }

    //returns Statistic enum constant if the input name is valid, otherwise null (param: statName in uppercase)
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
        return Arrays.stream(Statistic.values()).map(Statistic::toString).map(String::toLowerCase).toList();
    }

    //returns all substatnames in lowercase
    public static List<String> getSubStatNames() {
        List<String> subStatNames = new ArrayList<>();
        subStatNames.addAll(getBlockNames());
        subStatNames.addAll(getEntityNames());
        subStatNames.addAll(getItemNames());
        return subStatNames;
    }
}
