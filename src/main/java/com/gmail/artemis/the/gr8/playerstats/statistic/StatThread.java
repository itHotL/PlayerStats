package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.TestFileHandler;
import com.gmail.artemis.the.gr8.playerstats.reload.ReloadThread;
import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.MessageFactory;
import com.google.common.collect.ImmutableList;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class StatThread extends Thread {

    private final int threshold;
    private final StatRequest request;
    private final ReloadThread reloadThread;

    private final BukkitAudiences adventure;
    private static ConfigHandler config;
    private static TestFileHandler testFile;
    private final MessageFactory messageFactory;
    private final Main plugin;

    //constructor (called on thread creation)
    public StatThread(int threshold, StatRequest s, @Nullable ReloadThread r, BukkitAudiences b, ConfigHandler c, TestFileHandler t, MessageFactory o, Main p) {
        this.threshold = threshold;
        request = s;
        reloadThread = r;

        adventure = b;
        config = c;
        testFile = t;
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

        if (topFlag) {
            if (ThreadManager.getLastRecordedCalcTime() > 30000) {
                adventure.sender(sender).sendMessage(messageFactory.waitAMoment(true));
            }
            else if (ThreadManager.getLastRecordedCalcTime() > 2000) {
                adventure.sender(sender).sendMessage(messageFactory.waitAMoment(false));
            }

            try {
                adventure.sender(sender).sendMessage(messageFactory.formatTopStats(
                        getTopStatistics(), statName, subStatEntry));

                testFile.saveTimeTaken(System.currentTimeMillis() - time, 3);
                testFile.logRunCount(false);
                plugin.logTimeTaken("StatThread", "calculating top stat", time);
                ThreadManager.recordCalcTime(System.currentTimeMillis() - time);

            } catch (ConcurrentModificationException e) {
                testFile.logRunCount(true);
                adventure.sender(sender).sendMessage(messageFactory.unknownError());
            } catch (Exception e) {
                sender.sendMessage(messageFactory.formatExceptions(e.toString()));
                e.printStackTrace();
            }
        }

        else if (playerName != null) {
            try {
                adventure.sender(sender).sendMessage(
                        messageFactory.formatPlayerStat(
                                playerName, statName, subStatEntry, getStatistic()));
                plugin.logTimeTaken("StatThread", "calculating individual stat", time);

            } catch (Exception e) {
                sender.sendMessage(messageFactory.formatExceptions(e.toString()));
                e.printStackTrace();
            }
        }
    }

    //returns the integer associated with a certain statistic for a player
    private int getStatistic() throws IllegalArgumentException, NullPointerException {
        try {
            return getPlayerStat(OfflinePlayerHandler.getOfflinePlayer(request.getPlayerName()));
        }
        catch (Exception e) {
            Bukkit.getLogger().warning(e.toString());
            throw new IllegalArgumentException(e.toString());
        }
    }

    //invokes a bunch of worker pool threads to divide and conquer (get the statistics for all players in the list)
    private LinkedHashMap<String, Integer> getTopStatistics() throws ConcurrentModificationException {
        ConcurrentHashMap<String, Integer> playerStats = new ConcurrentHashMap<>((int) (getOfflinePlayerCount() * 1.05));
        //ConcurrentLinkedDeque<String> playerNames = new ConcurrentLinkedDeque<>(OfflinePlayerHandler.getOfflinePlayerNames());
        //String[] playerNames = OfflinePlayerHandler.getOfflinePlayerNames().toArray(new String[0]);
        ImmutableList<String> playerNames = ImmutableList.copyOf(OfflinePlayerHandler.getOfflinePlayerNames());
        TopStatAction task = new TopStatAction(threshold, playerNames,
                request, playerStats);

        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        try {
            commonPool.invoke(task);
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
            try {
                if (!task.cancel(true)) {
                    plugin.getLogger().severe("Tried to cancel task, but failed. You might need to shut down the server and reboot");
                    throw new ConcurrentModificationException(e.toString());
                } else {
                    plugin.getLogger().warning("Canceling task because of a ConcurrentModificationException...");
                }
            } catch (ConcurrentModificationException ex) {
                ex.printStackTrace();
                throw new ConcurrentModificationException(ex.toString());
            }
        }

        return playerStats.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(config.getTopListMaxSize()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    //gets the actual statistic data for an individual player
    private int getPlayerStat(@NotNull OfflinePlayer player) throws IllegalArgumentException {
        try {
            switch (request.getStatType()) {
                case UNTYPED -> {
                    return player.getStatistic(request.getStatEnum());
                }
                case ENTITY -> {
                    return player.getStatistic(request.getStatEnum(), request.getEntity());
                }
                case BLOCK -> {
                    return player.getStatistic(request.getStatEnum(), request.getBlock());
                }
                case ITEM -> {
                    return player.getStatistic(request.getStatEnum(), request.getItem());
                }
                default ->
                    throw new Exception("This statistic does not seem to be of type:untyped/block/entity/item, I strongly suggest we panic");
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning(e.toString());
            throw new IllegalArgumentException(e.toString());
        }
    }

    //returns the amount of offline players, attempts to update the list if none are found, and otherwise throws an error
    private int getOfflinePlayerCount() {
        try {
            return OfflinePlayerHandler.getOfflinePlayerCount();
        }
        catch (NullPointerException e) {
            throw new RuntimeException("No offline players were found to calculate statistics for!");
        }
    }
}
