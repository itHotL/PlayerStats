package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

import java.util.*;
import java.util.stream.Collectors;

public class StatManager {

    private final Main plugin;
    private final EnumHandler enumHandler;
    private final OfflinePlayerHandler offlinePlayerHandler;
    private final List<String> statNames;
    private final List<String> entityStatNames;
    private final List<String> subStatEntryNames;

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
    }

    public int getStatistic(String statName, String playerName) throws IllegalArgumentException, NullPointerException {
        return getStatistic(statName, null, playerName);
    }

    //returns the integer associated with a certain statistic for a player
    public int getStatistic(String statName, String subStatEntryName, String playerName) throws IllegalArgumentException, NullPointerException {
        long time = System.currentTimeMillis();

        OfflinePlayer player = offlinePlayerHandler.getOfflinePlayer(playerName);

        time = plugin.logTimeTaken("StatManager", time, 49);
        if (player == null) throw new NullPointerException("No player called " + playerName + " was found!");

        Statistic stat = getStatistic(statName);
        time = plugin.logTimeTaken("StatManager", time, 53);

            if (stat != null) {
                switch (stat.getType()) {
                    case UNTYPED -> {
                        time = plugin.logTimeTaken("StatManager", time, 58);
                        return player.getStatistic(stat);
                    }
                    case BLOCK -> {
                        time = plugin.logTimeTaken("StatManager", time, 62);
                        Material block = enumHandler.getBlock(subStatEntryName);
                        if (block == null) throw new NullPointerException(subStatEntryName + " is not a valid block name!");
                        return player.getStatistic(stat, block);
                    }
                    case ENTITY -> {
                        time = plugin.logTimeTaken("StatManager", time, 68);
                        EntityType entity = enumHandler.getEntityType(subStatEntryName);
                        if (entity == null) throw new NullPointerException(subStatEntryName + " is not a valid entity name!");
                        return player.getStatistic(stat, entity);
                    }
                    case ITEM -> {
                        time = plugin.logTimeTaken("StatManager", time, 74);
                        Material item = enumHandler.getItem(subStatEntryName);
                        if (item == null) throw new NullPointerException(subStatEntryName + " is not a valid item name!");
                        return player.getStatistic(stat, item);
                    }
                }
            }
        throw new NullPointerException(statName + " is not a valid statistic name!");
    }

    public LinkedHashMap<String, Integer> getTopStatistics(String statistic) {
        return getTopStatistics(statistic, null);
    }

    public LinkedHashMap<String, Integer> getTopStatistics(String statName, String subStatEntry) {
        long time = System.currentTimeMillis();
        HashMap<String, Integer> playerStats = new HashMap<>((int) (offlinePlayerHandler.getOfflinePlayerCount() * 1.05));
        time = plugin.logTimeTaken("StatManager", time, 91);

        Statistic stat = getStatistic(statName);
        time = plugin.logTimeTaken("StatManager", time, 94);

        if (stat != null) {
            switch (stat.getType()) {
                case UNTYPED -> {
                    time = plugin.logTimeTaken("StatManager", time, 99);
                    for (String playerName : offlinePlayerHandler.getAllOfflinePlayerNames()) {
                        OfflinePlayer player = offlinePlayerHandler.getOfflinePlayer(playerName);
                        if (player != null) {
                            playerStats.put(playerName, player.getStatistic(stat));
                        }
                    }
                }
            }

            time = plugin.logTimeTaken("StatManager", time, 109);
            LinkedHashMap<String, Integer> topStats = playerStats.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(10).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            time = plugin.logTimeTaken("StatManager", time, 113);

            plugin.getLogger().info("Top 10: " + topStats);
            time = plugin.logTimeTaken("StatManager", time, 116);

            HashMap<String, Integer> playerStats2 = new HashMap<>((int) (offlinePlayerHandler.getOfflinePlayerCount() * 1.05));
            offlinePlayerHandler.getAllOfflinePlayerNames().stream().forEach(playerName -> {
                OfflinePlayer player = offlinePlayerHandler.getOfflinePlayer(playerName);
                if (player != null) playerStats2.put(playerName, player.getStatistic(stat));
            });

            time = plugin.logTimeTaken("StatManager", time, 124);
            LinkedHashMap<String, Integer> topStats2 = playerStats2.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(10).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            time = plugin.logTimeTaken("StatManager", time, 129);
            plugin.getLogger().info("Top 10: " + topStats2);
            time = plugin.logTimeTaken("StatManager", time, 131);
            return topStats;
        }
        return null;
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
        catch (IllegalArgumentException | NullPointerException exception) {
            plugin.logStatRelatedExceptions(exception);
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
        Statistic.Type type = getStatType(statName);
        if (type != null && subStatEntry != null) {
            switch (type) {
                case ENTITY -> {
                    return enumHandler.isEntityType(subStatEntry);
                }
                case ITEM -> {
                    return enumHandler.isItem(subStatEntry);
                }
                case BLOCK -> {
                    return enumHandler.isBlock(subStatEntry);
                }
                case UNTYPED -> {
                    return false;
                }
            }
        }
        return false;
    }

    //returns the statistic enum constant, or null if non-existent (param: statName, not case sensitive)
    private Statistic getStatistic(String statName) {
        try {
            return Statistic.valueOf(statName.toUpperCase());
        }
        catch (IllegalArgumentException | NullPointerException exception) {
            plugin.logStatRelatedExceptions(exception);
            return null;
        }
    }
}
