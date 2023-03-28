package com.artemis.the.gr8.playerstats.core.database;

import com.artemis.the.gr8.database.DatabaseManager;
import com.artemis.the.gr8.database.models.MyStatType;
import com.artemis.the.gr8.database.models.MyStatistic;
import com.artemis.the.gr8.playerstats.core.Main;
import com.artemis.the.gr8.playerstats.core.utils.EnumHandler;
import org.bukkit.Statistic;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private DatabaseManager databaseManager;

    public Database() {
        connect();
    }

    private void connect() {
        File pluginFolder = Main.getPluginInstance().getDataFolder();
        databaseManager = DatabaseManager.getSQLiteManager(pluginFolder);
//        databaseManager = DatabaseManager.getMySQLManager("jdbc:mysql://localhost:3306/minecraftstatdb", "myuser", "myuser");
        databaseManager.setUp(getStats(), null);
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
    private MyStatType getType(Statistic statistic) {
        return switch (statistic.getType()) {
            case UNTYPED -> MyStatType.CUSTOM;
            case BLOCK -> MyStatType.BLOCK;
            case ITEM -> MyStatType.ITEM;
            case ENTITY -> MyStatType.ENTITY;
        };
    }
}
