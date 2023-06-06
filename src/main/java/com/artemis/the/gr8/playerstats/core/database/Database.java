package com.artemis.the.gr8.playerstats.core.database;

import com.artemis.the.gr8.databasemanager.DatabaseManager;
import com.artemis.the.gr8.databasemanager.models.MyPlayer;
import com.artemis.the.gr8.databasemanager.models.MyStatType;
import com.artemis.the.gr8.databasemanager.models.MyStatistic;
import com.artemis.the.gr8.databasemanager.models.MySubStatistic;
import com.artemis.the.gr8.playerstats.core.utils.EnumHandler;
import com.artemis.the.gr8.playerstats.core.utils.MyLogger;
import com.artemis.the.gr8.playerstats.core.utils.OfflinePlayerHandler;
import org.bukkit.Statistic;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Database {

    private static DatabaseManager databaseManager;

    private Database() {
        setUp();
    }

    @Contract("_, _, _ -> new")
    public static @NotNull Database getMySQLDatabase(String URL, String username, String password) {
        databaseManager = DatabaseManager.getMySQLManager(URL, username, password);
        return new Database();
    }

    @Contract("_ -> new")
    public static @NotNull Database getSQLiteDatabase(File pluginFolder) {
        databaseManager = DatabaseManager.getSQLiteManager(pluginFolder);
        return new Database();
    }

    private void setUp() {
        //TODO detect if empty
        updateStatisticEnums();
        updatePlayers();
    }

    private void updateStatisticEnums() {
        long startTime = System.currentTimeMillis();
        CompletableFuture
                .runAsync(() -> databaseManager.updateStatistics(getStats(), getSubStats()))
                .thenRun(() -> MyLogger.logLowLevelTask("Statistics loaded into database", startTime));
    }

    private void updatePlayers() {
        long startTime = System.currentTimeMillis();

        CompletableFuture
                .runAsync(() -> databaseManager.updatePlayers(getPlayers()))
                .thenRun(() -> MyLogger.logLowLevelTask("Players loaded into database", startTime));
    }

    private @NotNull List<MyStatistic> getStats() {
        EnumHandler enumHandler = EnumHandler.getInstance();
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
        EnumHandler enumHandler = EnumHandler.getInstance();
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
        OfflinePlayerHandler offlinePlayerHandler = OfflinePlayerHandler.getInstance();
        List <MyPlayer> players = new ArrayList<>();

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
}