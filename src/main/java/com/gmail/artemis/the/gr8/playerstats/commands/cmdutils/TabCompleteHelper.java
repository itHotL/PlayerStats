package com.gmail.artemis.the.gr8.playerstats.commands.cmdutils;

import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompleteHelper {

    private List<String> itemBrokenSuggestions;
    private List<String> entityKilledSuggestions;

    public TabCompleteHelper() {
        prepareLists();
    }

    public List<String> getAllItemNames() {
        return EnumHandler.getItemNames();
    }

    public List<String> getItemBrokenSuggestions() {
        return itemBrokenSuggestions;
    }

    public List<String> getAllBlockNames() {
        return EnumHandler.getBlockNames();
    }

    public List<String> getEntityKilledSuggestions() {
        return entityKilledSuggestions;
    }


    private void prepareLists() {
        itemBrokenSuggestions = Arrays.stream(Material.values())
                .parallel()
                .filter(Material::isItem)
                .filter(item -> item.getMaxDurability() != 0)
                .map(Material::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        entityKilledSuggestions = Arrays.stream(EntityType.values())
                .parallel()
                .filter(EntityType::isAlive)
                .filter(entityType -> entityType != EntityType.ARMOR_STAND)
                .map(EntityType::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }
}
