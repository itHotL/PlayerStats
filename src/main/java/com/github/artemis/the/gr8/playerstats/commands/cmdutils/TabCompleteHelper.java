package com.github.artemis.the.gr8.playerstats.commands.cmdutils;

import com.github.artemis.the.gr8.playerstats.utils.EnumHandler;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class TabCompleteHelper {

    private final EnumHandler enumHandler;
    private static List<String> itemBrokenSuggestions;
    private static List<String> entitySuggestions;

    public TabCompleteHelper(EnumHandler enumHandler) {
        this.enumHandler = enumHandler;
        prepareLists();
    }

    public List<String> getAllItemNames() {
        return enumHandler.getItemNames();
    }

    public List<String> getItemBrokenSuggestions() {
        return itemBrokenSuggestions;
    }

    public List<String> getAllBlockNames() {
        return enumHandler.getBlockNames();
    }

    public List<String> getEntitySuggestions() {
        return entitySuggestions;
    }


    private static void prepareLists() {
        //breaking an item means running its durability negative
        itemBrokenSuggestions = Arrays.stream(Material.values())
                .parallel()
                .filter(Material::isItem)
                .filter(item -> item.getMaxDurability() != 0)
                .map(Material::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        //the only statistics dealing with entities are killed_entity and entity_killed_by
        entitySuggestions = Arrays.stream(EntityType.values())
                .parallel()
                .filter(EntityType::isAlive)
                .map(EntityType::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }
}