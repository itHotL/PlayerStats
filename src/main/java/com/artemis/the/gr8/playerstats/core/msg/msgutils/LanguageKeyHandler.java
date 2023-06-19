package com.artemis.the.gr8.playerstats.core.msg.msgutils;

import com.artemis.the.gr8.playerstats.core.Main;
import com.artemis.the.gr8.playerstats.core.utils.EnumHandler;
import com.artemis.the.gr8.playerstats.core.utils.YamlFileHandler;
import com.artemis.the.gr8.playerstats.api.enums.Unit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * A utility class that provides language keys to be
 * put in a TranslatableComponent.
 */
public final class LanguageKeyHandler extends YamlFileHandler {

    private static volatile LanguageKeyHandler instance;
    private static HashMap<Statistic, String> statisticKeys;
    private final Pattern subStatKey;

    private LanguageKeyHandler() {
        super("language.yml");
        statisticKeys = generateStatisticKeys();
        subStatKey = Pattern.compile("(item|entity|block)\\.minecraft\\.");
        Main.registerReloadable(this);
    }

    public static LanguageKeyHandler getInstance() {
        LanguageKeyHandler localVar = instance;
        if (localVar != null) {
            return localVar;
        }

        synchronized (LanguageKeyHandler.class) {
            if (instance == null) {
                instance = new LanguageKeyHandler();
            }
            return instance;
        }
    }

    @Contract(pure = true)
    public @NotNull String getKeyForBlockUnit() {
        return "soundCategory.block";
    }

    @Contract(pure = true)
    public static boolean isEntityKey(@NotNull String key) {
        return key.contains("entity.minecraft");
    }

    /**
     * Checks if a given Key is the language key "stat_type.minecraft.killed".
     *
     * @param statKey the Key to check
     * @return true if this Key is key for kill-entity
     */
    @Contract(pure = true)
    public static boolean isNormalKeyForKillEntity(@NotNull String statKey) {
        return statKey.equalsIgnoreCase("stat_type.minecraft.killed");
    }

    /**
     * Checks if a given Key is the language key for "commands.kill.success.single",
     * which results in "Killed %s".
     * @param statKey the Key to check
     * @return true if this Key is key for commands.kill.success.single
     */
    @Contract(pure = true)
    public static boolean isCustomKeyForKillEntity(@NotNull String statKey) {
        return statKey.equalsIgnoreCase("commands.kill.success.single");
    }

    /**
     * Returns a language key to replace the default "stat_type.minecraft.killed" key.
     *
     * @return the key "commands.kill.success.single", which results in "Killed %s"
     */
    @Contract(pure = true)
    public static @NotNull String getCustomKeyForKillEntity() {
        return "commands.kill.success.single";
    }

    /**
     * Checks if a given Key is the language key "stat_type.minecraft.killed_by".
     *
     * @param statKey the Key to check
     * @return true if this Key is a key for entity-killed-by
     */
    @Contract(pure = true)
    public static boolean isNormalKeyForEntityKilledBy(@NotNull String statKey) {
        return statKey.equalsIgnoreCase("stat_type.minecraft.killed_by");
    }

    /**
     * Checks if a given Key is the language key "subtitles.entity.generic.death".
     * @param statKey the Key to check
     * @return true if this Key is key for subtitles.entity.generic.death
     */
    @Contract(pure = true)
    public static boolean isCustomKeyForEntityKilledBy(@NotNull String statKey) {
        return statKey.equalsIgnoreCase("subtitles.entity.generic.death");
    }

    /**
     * Checks if a given Key is the language key "book.byAuthor"
     * (which results in "by %s").
     *
     * @param statKey the Key to Check
     * @return true if this Key is the key for book.byAuthor
     */
    @Contract(pure = true)
    public static boolean isCustomKeyForEntityKilledByArg(@NotNull String statKey) {
        return statKey.equalsIgnoreCase("book.byAuthor");
    }

    /**
     * Returns a language key to replace the default stat_type.minecraft.killed_by key.
     *
     * @return the key "subtitles.entity.generic.death", which results in "Dying"
     * (meant to be followed by {@link #getCustomKeyForEntityKilledByArg()})
     */
    @Contract(pure = true)
    public static @NotNull String getCustomKeyForEntityKilledBy() {
        return "subtitles.entity.generic.death";
    }

    /**
     * Returns a language key to complete the alternative key for statistic.entity_killed_by.
     *
     * @return the key "book.byAuthor", which results in "by %". If used after
     * {@link #getCustomKeyForEntityKilledBy()}, you will get "Dying" "by %s"
     */
    @Contract(pure = true)
    public static @NotNull String getCustomKeyForEntityKilledByArg() {
        return "book.byAuthor";
    }

    public String convertLanguageKeyToDisplayName(String key) {
        if (key == null) return null;
        if (isStatKey(key)) {
            return getStatKeyTranslationFromFile(key);
        }
        else if (key.equalsIgnoreCase(getKeyForBlockUnit())) {
            return Unit.BLOCK.getLabel();
        }

        Matcher matcher = subStatKey.matcher(key);
        if (matcher.find()) {
            String rawName = matcher.replaceFirst("");
            return StringUtils.prettify(rawName);
        }
        return key;
    }

    private boolean isStatKey(@NotNull String key) {
        return (key.contains("stat") ||
                isCustomKeyForKillEntity(key) ||
                isCustomKeyForEntityKilledBy(key) ||
                isCustomKeyForEntityKilledByArg(key));
    }

    private String getStatKeyTranslationFromFile(String statKey) {
        String realKey = convertToNormalStatKey(statKey);
        if (realKey == null) {
            return "";
        }
        return super.getFileConfiguration().getString(realKey);
    }

    private static @Nullable String convertToNormalStatKey(String statKey) {
        if (isCustomKeyForKillEntity(statKey)) {
            return "stat_type.minecraft.killed";
        } else if (isCustomKeyForEntityKilledBy(statKey)) {
            return "stat_type.minecraft.killed_by";
        } else if (isCustomKeyForEntityKilledByArg(statKey)) {
            return null;
        } else {
            return statKey;
        }
    }

    /**
     * @param statistic the Statistic to get the Key for
     * @return the official Key from the NameSpacedKey for this Statistic,
     * or return null if no enum constant can be retrieved.
     */
    public @NotNull String getStatKey(@NotNull Statistic statistic) {
        if (statistic.getType() == Statistic.Type.UNTYPED) {
            return "stat.minecraft." + statisticKeys.get(statistic);
        }
        else {
            return "stat_type.minecraft." + statisticKeys.get(statistic);
        }
    }

    /**
     * @param entity the EntityType to get the Key for
     * @return the official Key from the NameSpacedKey for this EntityType,
     * or return null if no enum constant can be retrieved or EntityType is UNKNOWN.
     */
    public @Nullable String getEntityKey(EntityType entity) {
        if (entity == null || entity == EntityType.UNKNOWN) return null;
        else {
            return "entity.minecraft." + entity.getKey().getKey();
        }
    }

    /**
     * @param item the Material to get the Key for
     * @return the official Key from the NameSpacedKey for this item Material,
     * or return null if no enum constant can be retrieved.
     */
    public @Nullable String getItemKey(Material item) {
        if (item == null) return null;
        else if (item.isBlock()) {
            return getBlockKey(item);
        }
        else {
            return "item.minecraft." + item.getKey().getKey();
        }
    }

    /**
     * @param block the Material to get the Key for
     * @return the official Key from the NameSpacedKey for the block Material provided,
     * or return null if no enum constant can be retrieved.
     */
    public @Nullable String getBlockKey(Material block) {
        if (block == null) return null;
        else if (block.toString().toLowerCase(Locale.ENGLISH).contains("wall_banner")) {  //replace wall_banner with regular banner, since there is no key for wall banners
            String blockName = block.toString().toLowerCase(Locale.ENGLISH).replace("wall_", "");
            Material newBlock = EnumHandler.getInstance().getBlockEnum(blockName);
            return (newBlock != null) ? "block.minecraft." + newBlock.getKey().getKey() : null;
        }
        else {
            return "block.minecraft." + block.getKey().getKey();
        }
    }

    /**
     * @param unit the Unit to get the Key for
     * @return "soundCategory.block" for Unit.Block, null otherwise
     */
    public @Nullable String getUnitKey(Unit unit) {
        if (unit == Unit.BLOCK) {
            return "soundCategory.block";
        } else {
            return null;
        }
    }

    private @NotNull HashMap<Statistic, String> generateStatisticKeys() {
        //get the enum names for all statistics first
        HashMap<Statistic, String> statNames = new HashMap<>(Statistic.values().length);
        Arrays.stream(Statistic.values()).forEach(statistic -> statNames.put(statistic, statistic.toString().toLowerCase(Locale.ENGLISH)));

        //replace the ones for which the language key is different from the enum name
        statNames.put(Statistic.ARMOR_CLEANED, "clean_armor");
        statNames.put(Statistic.BANNER_CLEANED, "clean_banner");
        statNames.put(Statistic.DROP_COUNT, "drop");
        statNames.put(Statistic.CAKE_SLICES_EATEN, "eat_cake_slice");
        statNames.put(Statistic.ITEM_ENCHANTED, "enchant_item");
        statNames.put(Statistic.CAULDRON_FILLED, "fill_cauldron");
        statNames.put(Statistic.DISPENSER_INSPECTED, "inspect_dispenser");
        statNames.put(Statistic.DROPPER_INSPECTED, "inspect_dropper");
        statNames.put(Statistic.HOPPER_INSPECTED, "inspect_hopper");
        statNames.put(Statistic.BEACON_INTERACTION, "interact_with_beacon");
        statNames.put(Statistic.BREWINGSTAND_INTERACTION, "interact_with_brewingstand");
        statNames.put(Statistic.CRAFTING_TABLE_INTERACTION, "interact_with_crafting_table");
        statNames.put(Statistic.FURNACE_INTERACTION, "interact_with_furnace");
        statNames.put(Statistic.CHEST_OPENED, "open_chest");
        statNames.put(Statistic.ENDERCHEST_OPENED, "open_enderchest");
        statNames.put(Statistic.SHULKER_BOX_OPENED, "open_shulker_box");
        statNames.put(Statistic.NOTEBLOCK_PLAYED, "play_noteblock");
        statNames.put(Statistic.PLAY_ONE_MINUTE, "play_time");
        statNames.put(Statistic.RECORD_PLAYED, "play_record");
        statNames.put(Statistic.FLOWER_POTTED, "pot_flower");
        statNames.put(Statistic.TRAPPED_CHEST_TRIGGERED, "trigger_trapped_chest");
        statNames.put(Statistic.NOTEBLOCK_TUNED, "tune_noteblock");
        statNames.put(Statistic.CAULDRON_USED, "use_cauldron");

        //do the same for the statistics that have a subtype
        statNames.put(Statistic.DROP, "dropped");
        statNames.put(Statistic.PICKUP, "picked_up");
        statNames.put(Statistic.MINE_BLOCK, "mined");
        statNames.put(Statistic.USE_ITEM, "used");
        statNames.put(Statistic.BREAK_ITEM, "broken");
        statNames.put(Statistic.CRAFT_ITEM, "crafted");
        statNames.put(Statistic.KILL_ENTITY, "killed");
        statNames.put(Statistic.ENTITY_KILLED_BY, "killed_by");

        return statNames;
    }
}