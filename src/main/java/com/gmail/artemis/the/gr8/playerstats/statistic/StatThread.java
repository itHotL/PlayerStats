package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.ReloadThread;
import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.MessageFactory;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StatThread extends Thread {

    private final StatRequest request;
    private final ReloadThread reloadThread;

    private final BukkitAudiences adventure;
    private final ConfigHandler config;
    private final OfflinePlayerHandler offlinePlayerHandler;
    private final MessageFactory messageFactory;
    private final Main plugin;

    //constructor (called on thread creation)
    public StatThread(StatRequest s, @Nullable ReloadThread r, BukkitAudiences b, ConfigHandler c, OfflinePlayerHandler of, MessageFactory o, Main p) {
        request = s;
        reloadThread = r;

        adventure = b;
        config = c;
        offlinePlayerHandler = of;
        messageFactory = o;
        plugin = p;
        plugin.getLogger().info("StatThread created!");
    }

    //what the thread will do once started
    @Override
    public void run() throws IllegalStateException, NullPointerException {
        long time = System.currentTimeMillis();

        if (messageFactory == null || plugin == null) {
            throw new IllegalStateException("Not all classes off the plugin are running!");
        }
        if (request == null) {
            throw new NullPointerException("No statistic request was found!");
        }
        if (reloadThread != null && reloadThread.isAlive()) {
            try {
                plugin.getLogger().info("Waiting for reloadThread to finish up...");
                adventure.sender(request.getCommandSender()).sendMessage(messageFactory.stillReloading());
                reloadThread.join();
            } catch (InterruptedException e) {
                plugin.getLogger().warning(e.toString());
                throw new RuntimeException(e);
            }
        }

        CommandSender sender = request.getCommandSender();
        String playerName = request.getPlayerName();
        String statName = request.getStatName();
        String subStatEntry = request.getSubStatEntry();
        boolean topFlag = request.topFlag();

        if (playerName != null) {
            try {
                adventure.sender(sender).sendMessage(
                        messageFactory.formatPlayerStat(
                                playerName, statName, subStatEntry, getStatistic(
                                        statName, subStatEntry, playerName)));
                plugin.logTimeTaken("StatThread", "calculating individual stat", time);

            } catch (Exception e) {
                sender.sendMessage(messageFactory.formatExceptions(e.toString()));
                e.printStackTrace();
            }

        } else if (topFlag) {
            if (ThreadManager.getLastRecordedCalcTime() > 30000) {
                adventure.sender(sender).sendMessage(messageFactory.waitAMoment(true));
            }
            else if (ThreadManager.getLastRecordedCalcTime() > 2000) {
                adventure.sender(sender).sendMessage(messageFactory.waitAMoment(false));
            }

            try {
                adventure.sender(sender).sendMessage(messageFactory.formatTopStats(
                        getTopStatistics(statName, subStatEntry), statName, subStatEntry));

                plugin.logTimeTaken("StatThread", "calculating top stat", time);
                ThreadManager.recordCalcTime(System.currentTimeMillis() - time);

            } catch (Exception e) {
                sender.sendMessage(messageFactory.formatExceptions(e.toString()));
                e.printStackTrace();
            }
        }
    }

    //returns the integer associated with a certain statistic for a player
    private int getStatistic(String statName, String subStatEntryName, String playerName) throws IllegalArgumentException, NullPointerException {
        try {
            Statistic stat = EnumHandler.getStatEnum(statName);
            OfflinePlayer player = offlinePlayerHandler.getOfflinePlayer(playerName);
            return getPlayerStat(player, stat, subStatEntryName);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

    private LinkedHashMap<String, Integer> getTopStatistics(String statName, String subStatEntry) {
        try {
            Statistic stat = EnumHandler.getStatEnum(statName);
            HashMap<String, Integer> playerStats = new HashMap<>((int) (getOfflinePlayerCount() * 1.05));
            offlinePlayerHandler.getOfflinePlayerNames().forEach(playerName -> {
                OfflinePlayer player = offlinePlayerHandler.getOfflinePlayer(playerName);
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
                    .limit(config.getTopListMaxSize()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

    //gets the actual statistic data for a given player
    private int getPlayerStat(@NotNull OfflinePlayer player, @NotNull Statistic stat, String subStatEntryName) throws IllegalArgumentException {
        switch (stat.getType()) {
            case UNTYPED -> {
                return player.getStatistic(stat);
            }
            case BLOCK -> {
                try {
                    return player.getStatistic(stat, EnumHandler.getBlock(subStatEntryName));
                }
                catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(e.toString());
                }
            }
            case ENTITY -> {
                try {
                    return player.getStatistic(stat, EnumHandler.getEntityType(subStatEntryName));
                }
                catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(e.toString());
                }
            }
            case ITEM -> {
                try {
                    return player.getStatistic(stat, EnumHandler.getItem(subStatEntryName));
                }
                catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(e.toString());
                }
            }
            default ->
                    throw new IllegalArgumentException("This statistic does not seem to be of type:untyped/block/entity/item, I think we should panic");
        }
    }

    //returns the amount of offline players, attempts to update the list if none are found, and otherwise throws an error
    private int getOfflinePlayerCount() {
        try {
            return offlinePlayerHandler.getOfflinePlayerCount();
        }
        catch (NullPointerException e) {
            throw new RuntimeException("No offline players were found to calculate statistics for!");
        }
    }
}
