package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.enums.Query;
import com.gmail.artemis.the.gr8.playerstats.reload.ReloadThread;
import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageFactory;
import com.google.common.collect.ImmutableList;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class StatThread extends Thread {

    private final int threshold;
    private final StatRequest request;
    private final ReloadThread reloadThread;

    private final BukkitAudiences adventure;
    private static ConfigHandler config;
    private final MessageFactory messageFactory;
    private final Main plugin;

    //constructor (called on thread creation)
    public StatThread(int threshold, StatRequest s, @Nullable ReloadThread r, BukkitAudiences b, ConfigHandler c, MessageFactory o, Main p) {
        this.threshold = threshold;
        request = s;
        reloadThread = r;

        adventure = b;
        config = c;
        messageFactory = o;
        plugin = p;
        plugin.getLogger().info("StatThread created!");
    }

    //what the thread will do once started
    @Override
    public void run() throws IllegalStateException, NullPointerException {
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
        Query selection = request.getSelection();

        if (selection == Query.TOP || selection == Query.SERVER) {
            if (ThreadManager.getLastRecordedCalcTime() > 20000) {
                adventure.sender(sender).sendMessage(messageFactory.waitAMoment(true));
            }
            else if (ThreadManager.getLastRecordedCalcTime() > 2000) {
                adventure.sender(sender).sendMessage(messageFactory.waitAMoment(false));
            }

            try {
                if (selection == Query.TOP) {
                    adventure.sender(sender).sendMessage(messageFactory.formatTopStats(
                            getTopStats(), statName, subStatEntry));
                }
                else {
                    adventure.sender(sender).sendMessage(messageFactory.formatServerStat(
                            statName, subStatEntry, getServerTotal()));
                }

            } catch (ConcurrentModificationException e) {
                adventure.sender(sender).sendMessage(messageFactory.unknownError());
            } catch (Exception e) {
                adventure.sender(sender).sendMessage(messageFactory.formatExceptions(e.toString()));
            }
        }

        else if (selection == Query.PLAYER) {
            try {
                long time = System.currentTimeMillis();
                adventure.sender(sender).sendMessage(
                        messageFactory.formatPlayerStat(
                                playerName, statName, subStatEntry, getIndividualStat()));
                plugin.logTimeTaken("StatThread", "calculating individual stat", time);

            } catch (UnsupportedOperationException | NullPointerException e) {
                adventure.sender(sender).sendMessage(messageFactory.formatExceptions(e.getMessage()));
            }
        }
    }

    private LinkedHashMap<String, Integer> getTopStats() throws ConcurrentModificationException, NullPointerException {
        return getAllStats().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(config.getTopListMaxSize()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private int getServerTotal() {
        List<Integer> numbers = getAllStats().values().stream().toList();
        return numbers.parallelStream().mapToInt(Integer::intValue).sum();
    }

    //invokes a bunch of worker pool threads to divide and conquer (get the statistics for all players in the list)
    private @NotNull ConcurrentHashMap<String, Integer> getAllStats() throws ConcurrentModificationException, NullPointerException {
        long time = System.currentTimeMillis();

        ConcurrentHashMap<String, Integer> playerStats = new ConcurrentHashMap<>((int) (OfflinePlayerHandler.getOfflinePlayerCount() * 1.05));
        ImmutableList<String> playerNames = ImmutableList.copyOf(OfflinePlayerHandler.getOfflinePlayerNames());
        TopStatAction task = new TopStatAction(threshold, playerNames,
                request, playerStats);

        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        try {
            commonPool.invoke(task);
        } catch (ConcurrentModificationException e) {
            plugin.getLogger().warning("The request could not be executed due to a ConcurrentModificationException. " +
                    "This likely happened because Bukkit hasn't fully initialized all players yet. Try again and it should be fine!");
            throw new ConcurrentModificationException(e.toString());
        }

        ThreadManager.recordCalcTime(System.currentTimeMillis() - time);
        plugin.logTimeTaken("StatThread", "calculating all stats", time);

        return playerStats;
    }

    //gets the actual statistic data for an individual player
    private int getIndividualStat() throws UnsupportedOperationException, NullPointerException {
        OfflinePlayer player = OfflinePlayerHandler.getOfflinePlayer(request.getPlayerName());
        if (player != null) {
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
                default -> {
                    if (request.getStatType() != null) {
                        throw new UnsupportedOperationException("PlayerStats is not familiar with this statistic type - please check if you are using the latest version of the plugin!");
                    }
                    else {
                        throw new NullPointerException("Trying to calculate a statistic of which the type is null - is this a valid statistic?");
                    }
                }
            }
        }
        throw new NullPointerException("The player you are trying to request either does not exist, or is not on the list for statistic lookups!");
    }
}
