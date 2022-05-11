package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class StatManager {

    private final Main plugin;
    private final EnumHandler enumHandler;
    private final OfflinePlayerHandler offlinePlayerHandler;
    private final List<String> statNames;
    private final List<String> entityStatNames;
    private final List<String> subStatEntryNames;

    private final String className;

    public StatManager(EnumHandler e, Main p) {
        plugin = p;
        enumHandler = e;
        offlinePlayerHandler = OfflinePlayerHandler.getInstance();

        statNames = Arrays.stream(Statistic.values()).map(
                Statistic::toString).map(String::toLowerCase).toList();
        entityStatNames = Arrays.stream(Statistic.values()).filter(statistic ->
                statistic.getType().equals(Statistic.Type.ENTITY)).map(
                Statistic::toString).map(String::toLowerCase).collect(Collectors.toList());

        subStatEntryNames = new ArrayList<>();
        subStatEntryNames.addAll(enumHandler.getBlockNames());
        subStatEntryNames.addAll(enumHandler.getEntityTypeNames());
        subStatEntryNames.addAll(enumHandler.getItemNames());

        className = "StatManger";
    }

    //returns the integer associated with a certain statistic for a player
    public int getStatistic(String statName, String subStatEntryName, String playerName) throws IllegalArgumentException, NullPointerException {
        OfflinePlayer player = offlinePlayerHandler.getOfflinePlayer(playerName);
        if (player != null) {
            Statistic stat = getStatisticEnum(statName);
            if (stat != null) {
                return getPlayerStat(player, stat, subStatEntryName);
            }
            throw new IllegalArgumentException("Statistic " + statName + " could not be retrieved!");
        }
        throw new IllegalArgumentException("Player object for " + playerName + " could not be retrieved!");
    }

    private int getPlayerStat(@NotNull OfflinePlayer player, @NotNull Statistic stat, String subStatEntryName) throws IllegalArgumentException {
        String methodName = "getPlayerStat";
        long time = System.currentTimeMillis();

        switch (stat.getType()) {
            case UNTYPED -> {
                return player.getStatistic(stat);
            }
            case BLOCK -> {
                Material block = enumHandler.getBlock(subStatEntryName);
                plugin.logTimeTaken(className, methodName, time, 68);
                if (block != null) {
                    return player.getStatistic(stat, block);
                }
                else {
                    throw new IllegalArgumentException(subStatEntryName + " is not a valid block name!");
                }
            }
            case ENTITY -> {
                EntityType entity = enumHandler.getEntityType(subStatEntryName);
                plugin.logTimeTaken(className, methodName, time, 78);
                if (entity != null) {
                    return player.getStatistic(stat, entity);
                }
                else {
                    throw new IllegalArgumentException(subStatEntryName + " is not a valid entity name!");
                }
            }
            case ITEM -> {
                Material item = enumHandler.getItem(subStatEntryName);
                plugin.logTimeTaken(className, methodName, time, 88);
                if (item != null) {
                    return player.getStatistic(stat, item);
                }
                else {
                    throw new IllegalArgumentException(subStatEntryName + " is not a valid item name!");
                }
            }
            default ->
                throw new IllegalArgumentException("This statistic does not seem to be of type:untyped/block/entity/item, I think we should panic");
        }
    }

    public LinkedHashMap<String, Integer> getTopStatistics(String statName, String subStatEntry) throws IllegalArgumentException, NullPointerException {
        String methodName = "getTopStatistic";
        long time = System.currentTimeMillis();

        Statistic stat = getStatisticEnum(statName);
        time = plugin.logTimeTaken(className, methodName, time, 106);

        if (stat != null) {
            if (stat.getType().equals(Statistic.Type.UNTYPED) || isMatchingSubStatEntry(stat, subStatEntry)) {
                HashMap<String, Integer> playerStats = new HashMap<>((int) (offlinePlayerHandler.getOfflinePlayerCount() * 1.05));
                time = plugin.logTimeTaken(className, methodName, time, 111);

                for (String playerName : offlinePlayerHandler.getAllOfflinePlayerNames()) {
                    OfflinePlayer player = offlinePlayerHandler.getOfflinePlayer(playerName);
                    if (player != null) {
                        try {
                            playerStats.put(playerName, getPlayerStat(player, stat, subStatEntry));
                        }
                        catch (IllegalArgumentException ignored) {
                        }
                    }
                }
                time = plugin.logTimeTaken(className, methodName, time, 123);

                LinkedHashMap<String, Integer> topStats = playerStats.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(10).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                time = plugin.logTimeTaken(className, methodName, time, 128);

                plugin.getLogger().info("Top 10: " + topStats);
                plugin.logTimeTaken(className, methodName, time, 131);
                return topStats;
            }
            throw new IllegalArgumentException(subStatEntry + " is not a valid substatistic entry for this statistic!");
        }
        throw new NullPointerException("Statistic " + statName + " could not be retrieved!");
    }

    public LinkedHashMap<String, Integer> getTopStatistics2(String statName, String subStatEntry) {
        String methodName = "getTopStatistics2";
        long time = System.currentTimeMillis();

        Statistic stat = getStatisticEnum(statName);
        time = plugin.logTimeTaken(className, methodName, time, 144);

        if (stat != null) {
            if (stat.getType().equals(Statistic.Type.UNTYPED) || isMatchingSubStatEntry(stat, subStatEntry)) {
                HashMap<String, Integer> playerStats = new HashMap<>((int) (offlinePlayerHandler.getOfflinePlayerCount() * 1.05));
                time = plugin.logTimeTaken(className, methodName, time, 149);

                offlinePlayerHandler.getAllOfflinePlayerNames().forEach(playerName -> {
                    OfflinePlayer player = offlinePlayerHandler.getOfflinePlayer(playerName);
                    if (player != null)
                        try {
                            playerStats.put(playerName, getPlayerStat(player, stat, subStatEntry));
                        }
                        catch (IllegalArgumentException ignored) {

                        }
                });

                time = plugin.logTimeTaken(className, methodName, time, 162);

                LinkedHashMap<String, Integer> topStats = playerStats.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(10).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                time = plugin.logTimeTaken(className, methodName, time, 167);

                plugin.getLogger().info("Top 10: " + topStats);
                plugin.logTimeTaken(className, methodName, time, 170);
                return topStats;
            }
            throw new IllegalArgumentException(subStatEntry + " is not a valid substatistic entry for this statistic!");
        }
        throw new NullPointerException("Statistic " + statName + " could not be retrieved!");
    }

    //checks if string is a valid statistic (param: statName, not case sensitive)
    public boolean isStatistic(String statName) {
        return statNames.contains(statName.toLowerCase());
    }

    //gets the type of the statistic from the string, otherwise returns null (param: statName, not case sensitive)
    public Statistic.Type getStatType(String statName) {
        try {
            return Statistic.valueOf(statName.toUpperCase()).getType();
        }
        catch (IllegalArgumentException e) {
            plugin.getLogger().warning("IllegalArgumentException: " + statName + " is not a valid statistic name!");
            return null;
        }
        catch (NullPointerException e) {
            plugin.getLogger().warning("NullPointerException: please provide a statistic name!");
            return null;
        }
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
    public boolean isMatchingSubStatEntry(String statName, String subStatEntry) {
        Statistic stat = getStatisticEnum(statName);
        return (stat != null && isMatchingSubStatEntry(stat, subStatEntry));
    }

    private boolean isMatchingSubStatEntry(@NotNull Statistic stat, String subStatEntry) {
        switch (stat.getType()) {
            case ENTITY -> {
                return subStatEntry != null && enumHandler.isEntityType(subStatEntry);
            }
            case ITEM -> {
                return subStatEntry != null && enumHandler.isItem(subStatEntry);
            }
            case BLOCK -> {
                return subStatEntry != null && enumHandler.isBlock(subStatEntry);
            }
            case UNTYPED -> {
                return subStatEntry==null;
            }
            default -> {
                return false;
            }
        }
    }

    //returns the statistic enum constant, or null if non-existent (param: statName, not case sensitive)
    private Statistic getStatisticEnum(String statName) {
        try {
            return Statistic.valueOf(statName.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            plugin.getLogger().warning("IllegalArgumentException: " + statName + " is not a valid statistic name!");
            return null;
        }
        catch (NullPointerException e) {
            plugin.getLogger().warning("NullPointerException: please provide a statistic name!");
            return null;
        }
    }
}
