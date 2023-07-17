package com.artemis.the.gr8.playerstats.core.database;

import com.artemis.the.gr8.databasemanager.DatabaseManager;
import com.artemis.the.gr8.databasemanager.models.MyPlayer;
import com.artemis.the.gr8.databasemanager.models.MyStatType;
import com.artemis.the.gr8.databasemanager.models.MyStatistic;
import com.artemis.the.gr8.databasemanager.models.MySubStatistic;
import com.artemis.the.gr8.playerstats.core.utils.EnumHandler;
import com.artemis.the.gr8.playerstats.core.utils.MyLogger;
import com.artemis.the.gr8.playerstats.core.utils.OfflinePlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DatabaseHandler {

    private static DatabaseHandler instance;
    private final DatabaseManager databaseManager;
    private final EnumHandler enumHandler;
    private final OfflinePlayerHandler offlinePlayerHandler;

    private DatabaseHandler(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        enumHandler = EnumHandler.getInstance();
        offlinePlayerHandler = OfflinePlayerHandler.getInstance();
        instance = this;
        setUp();
    }

    public static DatabaseHandler getInstance() {
        return instance;
    }

    @Contract("_, _, _ -> new")
    public static @NotNull DatabaseHandler getMySQLDatabase(String URL, String username, String password) {
        DatabaseManager databaseManager = DatabaseManager.getMySQLManager(URL, username, password);
        return new DatabaseHandler(databaseManager);
    }

    @Contract("_ -> new")
    public static @NotNull DatabaseHandler getSQLiteDatabase(File pluginFolder) {
        DatabaseManager databaseManager = DatabaseManager.getSQLiteManager(pluginFolder);
        return new DatabaseHandler(databaseManager);
    }

    private void setUp() {
        //TODO detect if empty
        updatePlayers();
        updateStatisticEnums();
        updateFirstPlayerInStatsFolder();
    }

    private void updatePlayers() {
        long startTime = System.currentTimeMillis();
        CompletableFuture
                .runAsync(() -> databaseManager.updatePlayers(getPlayers()))
                .thenRun(() -> MyLogger.logLowLevelTask("Players loaded into database", startTime));
    }

    private void updateStatisticEnums() {
        long startTime = System.currentTimeMillis();
        CompletableFuture
                .runAsync(() -> databaseManager.updateStatistics(getStats(), getSubStats()))
                .thenRun(() -> MyLogger.logLowLevelTask("Statistics loaded into database", startTime));
    }

    private void updateFirstPlayerInStatsFolder() {
        File statsFolder = new File(Bukkit.getWorld("world").getWorldFolder() + File.separator + "stats");
        File[] statFiles = statsFolder.listFiles();
        if (statFiles != null) {
            MyLogger.logLowLevelMsg("Found " + statFiles.length + " stat files, first is: " + statFiles[0]);
        } else {
            MyLogger.logLowLevelMsg("Failed to find any stat files");
        }
    }

    public void updateStatsForArtemis() {
        long startTime = System.currentTimeMillis();
        CompletableFuture
                .runAsync(() -> {
                    OfflinePlayer artemis = getArtemis();
                    HashMap<MyStatistic, Integer> customTypeValues = new HashMap<>();

                    enumHandler.getAllStatNames()
                            .forEach(statName -> {
                                Statistic stat = enumHandler.getStatEnum(statName);
                                if (stat != null) {
                                    switch (stat.getType()) {
                                        case UNTYPED -> {
                                            int value = artemis.getStatistic(stat);
                                            if (value != 0) {
                                                customTypeValues.put(new MyStatistic(statName, getType(stat)), value);
                                            }
                                        }
                                        case ENTITY -> {
                                            long entityStartTime = System.currentTimeMillis();
                                            HashMap<MySubStatistic, Integer> entityTypeValues = new HashMap<>();
                                            enumHandler.getAllEntityNames().forEach(entityName -> {
                                                EntityType entityType = enumHandler.getEntityEnum(entityName);
                                                if (entityType != null) {
                                                    int value = artemis.getStatistic(stat, entityType);
                                                    if (value != 0) {
                                                        entityTypeValues.put(new MySubStatistic(entityName, MyStatType.ENTITY), value);
                                                    }
                                                }
                                            });
                                            databaseManager.updateEntityStatForPlayer(
                                                    getDatabaseArtemis(),
                                                    new MyStatistic(statName, MyStatType.ENTITY),
                                                    entityTypeValues);

                                            MyLogger.logLowLevelTask("Updated " + stat + " for Artemis", entityStartTime);
                                        }
                                        case BLOCK -> {
                                            long blockStartTime = System.currentTimeMillis();
                                            HashMap<MySubStatistic, Integer> blockTypeValues = new HashMap<>();
                                            enumHandler.getAllBlockNames().forEach(blockName -> {
                                                Material block = enumHandler.getBlockEnum(blockName);
                                                if (block != null) {
                                                    int value = artemis.getStatistic(stat, block);
                                                    if (value != 0) {
                                                        blockTypeValues.put(new MySubStatistic(blockName, MyStatType.BLOCK), value);
                                                    }
                                                }
                                            });
                                            databaseManager.updateBlockStatForPlayer(
                                                    getDatabaseArtemis(),
                                                    new MyStatistic(statName, MyStatType.BLOCK),
                                                    blockTypeValues);
                                            MyLogger.logLowLevelTask("Updated " + stat + " for Artemis", blockStartTime);
                                        }
                                        case ITEM -> {
                                            long itemStartTime = System.currentTimeMillis();
                                            HashMap<MySubStatistic, Integer> itemTypeValues = new HashMap<>();
                                            enumHandler.getAllItemNames().forEach(itemName -> {
                                                Material item = enumHandler.getItemEnum(itemName);
                                                if (item != null) {
                                                    int value = artemis.getStatistic(stat, item);
                                                    if (value != 0) {
                                                        itemTypeValues.put(new MySubStatistic(itemName, MyStatType.ITEM), value);
                                                    }
                                                }
                                            });
                                            databaseManager.updateItemStatForPlayer(
                                                    getDatabaseArtemis(),
                                                    new MyStatistic(statName, MyStatType.ITEM),
                                                    itemTypeValues);
                                            MyLogger.logLowLevelTask("Updated " + stat + " for Artemis", itemStartTime);
                                        }
                                    }
                                }
                                databaseManager.updateStatsForPlayer(getDatabaseArtemis(), customTypeValues);
                            });

                })
                .thenRun(() -> MyLogger.logLowLevelTask("all stats updated for Artemis", startTime));
    }

    private @NotNull List<MyStatistic> getStats() {
        List<MyStatistic> stats = new ArrayList<>();

        enumHandler.getAllStatNames().forEach(statName -> {
            Statistic stat = enumHandler.getStatEnum(statName);
            if (stat != null) {
                stats.add(new MyStatistic(statName, getType(stat)));
            }
        });
        return stats;
    }

    @Contract(pure = true)
    private MyStatType getType(@NotNull Statistic statistic) {
        return switch (statistic.getType()) {
            case UNTYPED -> MyStatType.CUSTOM;
            case BLOCK -> MyStatType.BLOCK;
            case ITEM -> MyStatType.ITEM;
            case ENTITY -> MyStatType.ENTITY;
        };
    }

    private @NotNull List<MySubStatistic> getSubStats() {
        List<MySubStatistic> subStats = new ArrayList<>();

        enumHandler.getAllBlockNames().forEach(blockName ->
                subStats.add(new MySubStatistic(blockName, MyStatType.BLOCK)));
        enumHandler.getAllItemNames().forEach(itemName ->
                subStats.add(new MySubStatistic(itemName, MyStatType.ITEM)));
        enumHandler.getAllEntityNames().forEach(entityName ->
                subStats.add(new MySubStatistic(entityName, MyStatType.ENTITY)));

        return subStats;
    }

    private @NotNull List<MyPlayer> getPlayers() {
        List<MyPlayer> players = new ArrayList<>();

        offlinePlayerHandler.getIncludedOfflinePlayerNames().forEach(playerName ->
                players.add(new MyPlayer(
                        playerName,
                        offlinePlayerHandler.getIncludedOfflinePlayer(playerName).getUniqueId(),
                        false)));
        offlinePlayerHandler.getExcludedPlayerNames().forEach(playerName ->
                players.add(new MyPlayer(
                        playerName,
                        offlinePlayerHandler.getExcludedOfflinePlayer(playerName).getUniqueId(),
                        true)));

        return players;
    }

    private @NotNull OfflinePlayer getArtemis() {
        if (offlinePlayerHandler.isIncludedPlayer("Artemis_the_gr8")) {
            return offlinePlayerHandler.getIncludedOfflinePlayer("Artemis_the_gr8");
        }
        return offlinePlayerHandler.getExcludedOfflinePlayer("Artemis_the_gr8");
    }

    private @NotNull MyPlayer getDatabaseArtemis() {
        return new MyPlayer(
                "Artemis_the_gr8",
                getArtemis().getUniqueId(),
                offlinePlayerHandler.isExcludedPlayer(getArtemis().getUniqueId()));
    }
}