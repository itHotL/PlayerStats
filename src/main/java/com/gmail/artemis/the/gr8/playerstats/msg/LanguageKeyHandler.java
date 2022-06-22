package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;

public class LanguageKeyHandler {

    private final HashMap<Statistic, String> statNameKeys;

    public LanguageKeyHandler() {
        statNameKeys = new HashMap<>();
        generateStatNameKeys();
    }

    public @Nullable String getStatKey(@NotNull String statName) {
        try {
            Statistic stat = EnumHandler.getStatEnum(statName);
            if (stat.getType() == Statistic.Type.UNTYPED) {
                return "stat.minecraft." + statNameKeys.get(stat);
            }
            else {
                return "stat_type.minecraft." + statNameKeys.get(stat);
            }
        }
        catch (IllegalArgumentException e) {
            Bukkit.getLogger().info("PlayerStats, LanguageKeyHandler 33: " + e);
            return null;
        }
    }

    public @Nullable String getEntityKey(@NotNull String entityName) {
        if (entityName.equalsIgnoreCase("UNKNOWN")) {
            return null;
        }
        else {
            try {
                return "entity.minecraft." + EnumHandler.getEntityEnum(entityName).getKey().getKey();
            }
            catch (IllegalArgumentException e) {
                Bukkit.getLogger().info("PlayerStats, LanguageKeyHandler 47: " + e);
                return null;
            }
        }
    }

    public @Nullable String getItemKey(@NotNull String itemName) {
        try {
            Material item = EnumHandler.getItemEnum(itemName);
            if (item.isBlock()) {
                return "block.minecraft." + item.getKey().getKey();
            }
            else {
                return "item.minecraft." + EnumHandler.getItemEnum(itemName).getKey().getKey();
            }
        }
        catch (IllegalArgumentException e) {
            Bukkit.getLogger().info("PlayerStats, LanguageKeyHandler 64: " + e);
            return null;
        }
    }

    public @Nullable String getBlockKey(@NotNull String materialName) {
        String name = materialName;
        if (materialName.toLowerCase().contains("wall_banner")) {
            name = materialName.replace("wall_", "");
        }
        try {
            return "block.minecraft." + EnumHandler.getBlockEnum(name).getKey().getKey();
        }
        catch (IllegalArgumentException e) {
            Bukkit.getLogger().info("PlayerStats, LanguageKeyHandler 78: " + e);
            return null;
        }
    }

    private void generateDefaultKeys() {
        Arrays.stream(Statistic.values()).forEach(statistic -> statNameKeys.put(statistic, statistic.toString().toLowerCase()));
    }

    private void generateStatNameKeys() {
        //get the enum names for all statistics first
        generateDefaultKeys();

        //replace the ones for which the language key is different from the enum name
        statNameKeys.put(Statistic.ARMOR_CLEANED, "clean_armor");
        statNameKeys.put(Statistic.BANNER_CLEANED, "clean_banner");
        statNameKeys.put(Statistic.DROP_COUNT, "drop");
        statNameKeys.put(Statistic.CAKE_SLICES_EATEN, "eat_cake_slice");
        statNameKeys.put(Statistic.ITEM_ENCHANTED, "enchant_item");
        statNameKeys.put(Statistic.CAULDRON_FILLED, "fill_cauldron");
        statNameKeys.put(Statistic.DISPENSER_INSPECTED, "inspect_dispenser");
        statNameKeys.put(Statistic.DROPPER_INSPECTED, "inspect_dropper");
        statNameKeys.put(Statistic.HOPPER_INSPECTED, "inspect_hopper");
        statNameKeys.put(Statistic.BEACON_INTERACTION, "interact_with_beacon");
        statNameKeys.put(Statistic.BREWINGSTAND_INTERACTION, "interact_with_brewingstand");
        statNameKeys.put(Statistic.CRAFTING_TABLE_INTERACTION, "interact_with_crafting_table");
        statNameKeys.put(Statistic.FURNACE_INTERACTION, "interact_with_furnace");
        statNameKeys.put(Statistic.CHEST_OPENED, "open_chest");
        statNameKeys.put(Statistic.ENDERCHEST_OPENED, "open_enderchest");
        statNameKeys.put(Statistic.SHULKER_BOX_OPENED, "open_shulker_box");
        statNameKeys.put(Statistic.NOTEBLOCK_PLAYED, "play_noteblock");
        statNameKeys.put(Statistic.PLAY_ONE_MINUTE, "play_time");
        statNameKeys.put(Statistic.RECORD_PLAYED, "play_record");
        statNameKeys.put(Statistic.FLOWER_POTTED, "pot_flower");
        statNameKeys.put(Statistic.TRAPPED_CHEST_TRIGGERED, "trigger_trapped_chest");
        statNameKeys.put(Statistic.NOTEBLOCK_TUNED, "tune_noteblock");
        statNameKeys.put(Statistic.CAULDRON_USED, "use_cauldron");

        //do the same for the statistics that have a subtype
        statNameKeys.put(Statistic.DROP, "dropped");
        statNameKeys.put(Statistic.PICKUP, "picked_up");
        statNameKeys.put(Statistic.MINE_BLOCK, "mined");
        statNameKeys.put(Statistic.USE_ITEM, "used");
        statNameKeys.put(Statistic.BREAK_ITEM, "broken");
        statNameKeys.put(Statistic.CRAFT_ITEM, "crafted");
        statNameKeys.put(Statistic.KILL_ENTITY, "killed");
        statNameKeys.put(Statistic.ENTITY_KILLED_BY, "killed_by");
    }
}
