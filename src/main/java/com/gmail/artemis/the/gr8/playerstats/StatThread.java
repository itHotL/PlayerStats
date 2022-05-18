package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OutputFormatter;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StatThread extends Thread {

    private final StatRequest request;
    private final EnumHandler enumHandler;
    private final OutputFormatter outputFormatter;
    private final Main plugin;
    private String className = "StatThread";

    //constructor (called on thread creation)
    public StatThread(StatRequest s, EnumHandler e, OutputFormatter o, Main p) {
        request = s;

        enumHandler = e;
        outputFormatter = o;
        plugin = p;
        plugin.getLogger().info("StatThread created!");
    }

    //what the thread will do once started
    @Override
    public void run() throws IllegalStateException, NullPointerException {
        plugin.getLogger().info("Name: " + this.getName());
        plugin.getLogger().info("ID: " + this.getId());
        long time = System.currentTimeMillis();

        if (outputFormatter == null || plugin == null) {
            throw new IllegalStateException("Not all classes off the plugin are running!");
        }
        if (request == null) {
            throw new NullPointerException("No statistic request was found!");
        }

        CommandSender sender = request.getCommandSender();
        String playerName = request.getPlayerName();
        String statName = request.getStatName();
        String subStatEntry = request.getSubStatEntry();
        boolean topFlag = request.topFlag();

        if (playerName != null) {
            try {
                sender.sendMessage(
                        outputFormatter.formatPlayerStat(
                                playerName, statName, subStatEntry, getStatistic(
                                        statName, subStatEntry, playerName)));
                plugin.logTimeTaken(className, "run(): individual stat", time, 60);

            } catch (Exception e) {
                sender.sendMessage(outputFormatter.formatExceptions(e.toString()));
            }

        } else if (topFlag) {
            try {
                LinkedHashMap<String, Integer> topStats = getTopStatistics(statName, subStatEntry);
                plugin.logTimeTaken(className, "run(): for each loop", time, 69);

                String top = outputFormatter.formatTopStats(topStats, statName, subStatEntry);
                sender.sendMessage(top);
                plugin.logTimeTaken(className, "run(): format output", time, 73);

            } catch (Exception e) {
                sender.sendMessage(outputFormatter.formatExceptions(e.toString()));
                e.printStackTrace();
            }
        }
    }

    //returns the integer associated with a certain statistic for a player
    private int getStatistic(String statName, String subStatEntryName, String playerName) throws IllegalArgumentException, NullPointerException {
        OfflinePlayer player = OfflinePlayerHandler.getOfflinePlayer(playerName);
        if (player != null) {
            Statistic stat = enumHandler.getStatEnum(statName);
            if (stat != null) {
                return getPlayerStat(player, stat, subStatEntryName);
            }
            throw new IllegalArgumentException("Statistic " + statName + " could not be retrieved!");
        }
        throw new IllegalArgumentException("Player object for " + playerName + " could not be retrieved!");
    }

    private LinkedHashMap<String, Integer> getTopStatistics(String statName, String subStatEntry) {
        Statistic stat = enumHandler.getStatEnum(statName);

        if (stat != null) {
            HashMap<String, Integer> playerStats = new HashMap<>((int) (OfflinePlayerHandler.getOfflinePlayerCount() * 1.05));
            OfflinePlayerHandler.getAllOfflinePlayerNames().forEach(playerName -> {
                OfflinePlayer player = OfflinePlayerHandler.getOfflinePlayer(playerName);
                if (player != null)
                    try {
                        int statistic = getPlayerStat(player, stat, subStatEntry);
                        if (statistic > 0) {
                            playerStats.put(playerName, statistic);
                        }
                    } catch (IllegalArgumentException ignored) {
                    }
            });
            return playerStats.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(10).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        throw new NullPointerException("Statistic " + statName + " could not be retrieved!");
    }

    private int getPlayerStat(@NotNull OfflinePlayer player, @NotNull Statistic stat, String subStatEntryName) throws IllegalArgumentException {
        switch (stat.getType()) {
            case UNTYPED -> {
                return player.getStatistic(stat);
            }
            case BLOCK -> {
                Material block = enumHandler.getBlock(subStatEntryName);
                if (block != null) {
                    return player.getStatistic(stat, block);
                }
                else {
                    throw new IllegalArgumentException(subStatEntryName + " is not a valid block name!");
                }
            }
            case ENTITY -> {
                EntityType entity = enumHandler.getEntityType(subStatEntryName);
                if (entity != null) {
                    return player.getStatistic(stat, entity);
                }
                else {
                    throw new IllegalArgumentException(subStatEntryName + " is not a valid entity name!");
                }
            }
            case ITEM -> {
                Material item = enumHandler.getItem(subStatEntryName);
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
}
