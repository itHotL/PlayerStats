package com.artemis.the.gr8.playerstats.core.database;

import com.artemis.the.gr8.databasemanager.DatabaseManager;
import com.artemis.the.gr8.databasemanager.models.MyPlayer;
import com.artemis.the.gr8.databasemanager.models.MyStatType;
import com.artemis.the.gr8.databasemanager.models.MyStatistic;
import com.artemis.the.gr8.databasemanager.models.MySubStatistic;
import com.artemis.the.gr8.playerstats.core.utils.EnumHandler;
import com.artemis.the.gr8.playerstats.core.utils.MyLogger;
import com.artemis.the.gr8.playerstats.core.utils.OfflinePlayerHandler;
import com.artemis.the.gr8.statfilereader.StatFileReader;
import com.artemis.the.gr8.statfilereader.model.Stats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
//        updateStatisticEnums();
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

    public void updateStatsForArtemis(boolean useSpigot) {
        if (useSpigot) {
            getStatsFromSpigot();
        } else {
            String artemisUUID = getArtemis().getUniqueId().toString();
            File statsFile = new File(Bukkit.getWorld("world").getWorldFolder() +
                    File.separator + "stats" +
                    File.separator + artemisUUID + ".json");
            StatFileReader reader = new StatFileReader();
            getStatsFromFile(reader.readFile(statsFile.getPath()), getDatabaseArtemis());
        }
    }

    public void updateStatsForOther(boolean useSpigot) {
        File statsFolder = new File(Bukkit.getWorld("world").getWorldFolder() + File.separator + "stats");
        File[] statFiles = statsFolder.listFiles();
        if (statFiles != null && statFiles.length > 1) {
            File otherStatFile = Arrays.stream(statFiles)
                    .filter(file -> !file.getPath().contains(getArtemis().getUniqueId().toString()))
                    .toList()
                    .get(0);
            StatFileReader reader = new StatFileReader();
            String filePath = otherStatFile.getPath();
            getStatsFromFile(reader.readFile(filePath), getDatabasePlayer(filePath));
        }
    }

    private void getStatsFromFile(Stats fileContents, MyPlayer player) {
        long startTime = System.currentTimeMillis();
        CompletableFuture
                .runAsync(() -> {
                    long subStartTime = System.currentTimeMillis();
                    HashMap<MyStatistic, Integer> customStats = new HashMap<>();
                    fileContents.custom.forEach((string, value) ->
                            customStats.put(new MyStatistic(string, MyStatType.CUSTOM), value));

                    databaseManager.updateStatsForPlayer(player, customStats);
                    MyLogger.logLowLevelTask("Updated custom", subStartTime);

                    subStartTime = System.currentTimeMillis();
                    HashMap<MySubStatistic, Integer> mined = new HashMap<>();
                    fileContents.mined.forEach((string, value) ->
                            mined.put(new MySubStatistic(string, MyStatType.BLOCK), value));

                    databaseManager.updateStatWithSubStatsForPlayer(player, new MyStatistic("mined", MyStatType.BLOCK), mined);
                    MyLogger.logLowLevelTask("Updated mined", subStartTime);

                    subStartTime = System.currentTimeMillis();
                    HashMap<MySubStatistic, Integer> broken = new HashMap<>();
                    fileContents.broken.forEach((string, value) ->
                            broken.put(new MySubStatistic(string, MyStatType.ITEM), value));

                    databaseManager.updateStatWithSubStatsForPlayer(player, new MyStatistic("broken", MyStatType.ITEM), broken);
                    MyLogger.logLowLevelTask("Updated broken", subStartTime);

                    subStartTime = System.currentTimeMillis();
                    HashMap<MySubStatistic, Integer> crafted = new HashMap<>();
                    fileContents.crafted.forEach((string, value) ->
                            crafted.put(new MySubStatistic(string, MyStatType.ITEM), value));

                    databaseManager.updateStatWithSubStatsForPlayer(player, new MyStatistic("crafted", MyStatType.ITEM), crafted);
                    MyLogger.logLowLevelTask("Updated crafted", subStartTime);

                    subStartTime = System.currentTimeMillis();
                    HashMap<MySubStatistic, Integer> used = new HashMap<>();
                    fileContents.used.forEach((string, value) ->
                            used.put(new MySubStatistic(string, MyStatType.ITEM), value));

                    databaseManager.updateStatWithSubStatsForPlayer(player, new MyStatistic("used", MyStatType.ITEM), used);
                    MyLogger.logLowLevelTask("Updated used", subStartTime);

                    subStartTime = System.currentTimeMillis();
                    HashMap<MySubStatistic, Integer> picked_up = new HashMap<>();
                    fileContents.picked_up.forEach((string, value) ->
                            picked_up.put(new MySubStatistic(string, MyStatType.ITEM), value));

                    databaseManager.updateStatWithSubStatsForPlayer(player, new MyStatistic("picked_up", MyStatType.ITEM), picked_up);
                    MyLogger.logLowLevelTask("Updated picked_up", subStartTime);

                    subStartTime = System.currentTimeMillis();
                    HashMap<MySubStatistic, Integer> dropped = new HashMap<>();
                    fileContents.dropped.forEach((string, value) ->
                            dropped.put(new MySubStatistic(string, MyStatType.ITEM), value));

                    databaseManager.updateStatWithSubStatsForPlayer(player, new MyStatistic("dropped", MyStatType.ITEM), dropped);
                    MyLogger.logLowLevelTask("Updated dropped", subStartTime);

                    subStartTime = System.currentTimeMillis();
                    HashMap<MySubStatistic, Integer> killed = new HashMap<>();
                    fileContents.killed.forEach((string, value) ->
                            killed.put(new MySubStatistic(string, MyStatType.ENTITY), value));

                    databaseManager.updateStatWithSubStatsForPlayer(player, new MyStatistic("killed", MyStatType.ENTITY), killed);
                    MyLogger.logLowLevelTask("Updated killed", subStartTime);

                    subStartTime = System.currentTimeMillis();
                    HashMap<MySubStatistic, Integer> killed_by = new HashMap<>();
                    fileContents.killed_by.forEach((string, value) ->
                            killed_by.put(new MySubStatistic(string, MyStatType.ENTITY), value));

                    databaseManager.updateStatWithSubStatsForPlayer(player, new MyStatistic("killed_by", MyStatType.ENTITY), killed_by);
                    MyLogger.logLowLevelTask("Updated killed_by", subStartTime);

                    MyLogger.logLowLevelTask("all stats updated for " + player.playerName(), startTime);
                })
                .whenComplete((result, error) -> error.printStackTrace());
    }

    private void getStatsFromSpigot() {
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
                                            databaseManager.updateStatWithSubStatsForPlayer(
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
                                            databaseManager.updateStatWithSubStatsForPlayer(
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
                                            databaseManager.updateStatWithSubStatsForPlayer(
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

    private MyPlayer getDatabasePlayer(String filePath) {
        Pattern uuidPattern = Pattern.compile("(?<=\\\\)[\\d\\w-]+(?=\\.json)");
        Matcher matcher = uuidPattern.matcher(filePath);
        if (matcher.find()) {
            String uuid = matcher.group();
            UUID playerUUID = UUID.fromString(uuid);
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
            return new MyPlayer(
                    player.getName(),
                    playerUUID,
                    offlinePlayerHandler.isExcludedPlayer(playerUUID)
            );
        }
        return null;
    }
}