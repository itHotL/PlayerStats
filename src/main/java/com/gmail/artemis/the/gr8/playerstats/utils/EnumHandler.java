package com.gmail.artemis.the.gr8.playerstats.utils;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumHandler {

    private EnumHandler() {
    }

    //returns corresponding item enum constant (uppercase), otherwise null (param: itemName, not case sensitive)
    public static Material getItem(String itemName) {
        return Material.matchMaterial(itemName);
    }

    //returns all item names in lowercase
    public static List<String> getItemNames() {
        return Arrays.stream(Material.values()).filter(
                Material::isItem).map(Material::toString).map(String::toLowerCase).toList();
    }

    //returns EntityType enum constant (uppercase) if the input name is valid, otherwise null (param: entityName in uppercase)
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
        return Arrays.stream(EntityType.values()).map(
                EntityType::toString).map(String::toLowerCase).toList();
    }

    //returns corresponding block enum constant (uppercase), otherwise null (param: materialName, not case sensitive)
    public static Material getBlock(String materialName) {
        return Material.matchMaterial(materialName);
    }

    //returns all block names in lowercase
    public static List<String> getBlockNames() {
        return Arrays.stream(Material.values()).filter(
                Material::isBlock).map(Material::toString).map(String::toLowerCase).toList();
    }
}
