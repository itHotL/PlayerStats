package com.gmail.artemis.the.gr8.playerstats.commands.cmdutils;

import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import org.bukkit.Material;

import java.util.List;
import java.util.stream.Collectors;

public class TabCompleteHelper {

    private List<String> itemBrokenSuggestions;

    public TabCompleteHelper() {
        prepareLists();
    }

    private void prepareLists() {
        itemBrokenSuggestions = EnumHandler.getItems()
                .parallelStream()
                .filter(item -> item.getMaxDurability() != 0)
                .map(Material::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    public List<String> getItemBrokenSuggestions() {
        return itemBrokenSuggestions;
    }
}
