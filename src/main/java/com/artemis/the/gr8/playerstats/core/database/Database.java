package com.artemis.the.gr8.playerstats.core.database;

import com.artemis.the.gr8.databasemanager.DatabaseManager;
import com.artemis.the.gr8.databasemanager.datamodels.MyStatType;
import com.artemis.the.gr8.databasemanager.datamodels.MyStatistic;
import com.artemis.the.gr8.databasemanager.datamodels.MySubStatistic;
import com.artemis.the.gr8.playerstats.core.utils.EnumHandler;
import org.bukkit.Statistic;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        databaseManager.updateStatistics(getStats(), getSubStats());
    }

    public boolean isRunning() {
        //TODO do something with databaseManager to see if it's active
        return true;
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

}