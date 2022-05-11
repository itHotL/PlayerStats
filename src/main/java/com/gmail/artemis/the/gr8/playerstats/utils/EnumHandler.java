package com.gmail.artemis.the.gr8.playerstats.utils;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class EnumHandler {

    private final List<String> blockNames;
    private final List<String> entityTypeNames;
    private final List<String> itemNames;


    public EnumHandler() {
        blockNames = Arrays.stream(Material.values()).filter(
                Material::isBlock).map(Material::toString).map(String::toLowerCase).toList();
        entityTypeNames = Arrays.stream(EntityType.values()).map(
                EntityType::toString).map(String::toLowerCase).toList();
        itemNames = Arrays.stream(Material.values()).filter(
                Material::isItem).map(Material::toString).map(String::toLowerCase).toList();
    }

    public boolean isItem(String itemName) {
        return itemNames.contains(itemName.toLowerCase());
    }

    //returns corresponding item enum constant (uppercase), otherwise null (param: itemName, not case sensitive)
    @Nullable
    public Material getItem(String itemName) {
        return Material.matchMaterial(itemName);
    }

    //returns all item names in lowercase
    public List<String> getItemNames() {
        return itemNames;
    }

    public boolean isEntityType(String entityName) {
        return entityTypeNames.contains(entityName.toLowerCase());
    }

    //returns EntityType enum constant (uppercase) if the input name is valid, otherwise null (param: entityName, not case sensitive)
    @Nullable
    public EntityType getEntityType(String entityName) {
        EntityType entityType;
        try {
            entityType = EntityType.valueOf(entityName.toUpperCase());
        }
        catch (IllegalArgumentException | NullPointerException exception) {
            return null;
        }
        return entityType;
    }

    //returns all entitytype names in lowercase
    public List<String> getEntityTypeNames() {
        return entityTypeNames;
    }

    public boolean isBlock(String materialName) {
        return blockNames.contains(materialName.toLowerCase());
    }

    //returns corresponding block enum constant (uppercase), otherwise null (param: materialName, not case sensitive)
    @Nullable
    public Material getBlock(String materialName) {
        return Material.matchMaterial(materialName);
    }

    //returns all block names in lowercase
    public List<String> getBlockNames() {
        return blockNames;
    }

}
