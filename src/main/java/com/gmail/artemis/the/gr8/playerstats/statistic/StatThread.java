package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageWriter;
import com.gmail.artemis.the.gr8.playerstats.reload.ReloadThread;
import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.google.common.collect.ImmutableList;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.OfflinePlayer;
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
    private static MessageWriter messageWriter;
    private final Main plugin;

    //constructor (called on thread creation)
    public StatThread(BukkitAudiences a, ConfigHandler c, MessageWriter m, Main p, int ID, int threshold, StatRequest s, @Nullable ReloadThread r) {
        this.threshold = threshold;

        request = s;
        reloadThread = r;

        adventure = a;
        config = c;
        messageWriter = m;
        plugin = p;

        this.setName("StatThread-" + request.getCommandSender().getName() + "-" + ID);
        MyLogger.threadCreated(this.getName());
    }

    //what the thread will do once started
    @Override
    public void run() throws IllegalStateException, NullPointerException {
        MyLogger.threadStart(this.getName());

        if (messageWriter == null || plugin == null) {
            throw new IllegalStateException("Not all classes off the plugin are running!");
        }
        if (request == null) {
            throw new NullPointerException("No statistic request was found!");
        }

        if (reloadThread != null && reloadThread.isAlive()) {
            try {
                MyLogger.waitingForOtherThread(this.getName(), reloadThread.getName());
                adventure.sender(request.getCommandSender())
                        .sendMessage(messageWriter
                                .stillReloading(request.isBukkitConsoleSender()));
                reloadThread.join();

            } catch (InterruptedException e) {
                MyLogger.logException(e, "StatThread", "Trying to join" + reloadThread.getName());
                throw new RuntimeException(e);
            }
        }

        Target selection = request.getSelection();
        if (selection == Target.PLAYER) {
            adventure.sender(request.getCommandSender()).sendMessage(
                    messageWriter.formatPlayerStat(getIndividualStat(), request));
        }
        else  {
            if (ThreadManager.getLastRecordedCalcTime() > 2000) {
                adventure.sender(request.getCommandSender()).sendMessage(
                        messageWriter.waitAMoment(ThreadManager.getLastRecordedCalcTime() > 20000, request.isBukkitConsoleSender()));
            }
            try {
                if (selection == Target.TOP) {
                    TextComponent statResult = messageWriter.formatTopStats(getTopStats(), request);

                    adventure.sender(request.getCommandSender()).sendMessage(statResult);
                } else {
                    adventure.sender(request.getCommandSender()).sendMessage(
                            messageWriter.formatServerStat(getServerTotal(), request));
                }
            } catch (ConcurrentModificationException e) {
                if (!request.isConsoleSender()) {
                    adventure.sender(request.getCommandSender()).sendMessage(
                            messageWriter.unknownError(false));
                }
            }
        }
    }

    private LinkedHashMap<String, Integer> getTopStats() throws ConcurrentModificationException {
        return getAllStats().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(config.getTopListMaxSize()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private long getServerTotal() {
        List<Integer> numbers = getAllStats().values().stream().toList();
        return numbers.parallelStream().mapToLong(Integer::longValue).sum();
    }

    //invokes a bunch of worker pool threads to divide and conquer (get the statistics for all players in the list)
    private @NotNull ConcurrentHashMap<String, Integer> getAllStats() throws ConcurrentModificationException {
        long time = System.currentTimeMillis();

        int size = OfflinePlayerHandler.getOfflinePlayerCount() != 0 ? (int) (OfflinePlayerHandler.getOfflinePlayerCount() * 1.05) : 16;
        ConcurrentHashMap<String, Integer> playerStats = new ConcurrentHashMap<>(size);
        ImmutableList<String> playerNames = ImmutableList.copyOf(OfflinePlayerHandler.getOfflinePlayerNames());

        TopStatAction task = new TopStatAction(threshold, playerNames, request, playerStats);
        MyLogger.actionCreated(playerNames.size());
        ForkJoinPool commonPool = ForkJoinPool.commonPool();

        try {
            commonPool.invoke(task);
        } catch (ConcurrentModificationException e) {
            MyLogger.logMsg("The request could not be executed due to a ConcurrentModificationException. " +
                    "This likely happened because Bukkit hasn't fully initialized all player-data yet. Try again and it should be fine!", true);
            throw new ConcurrentModificationException(e.toString());
        }

        MyLogger.actionFinished(2);
        ThreadManager.recordCalcTime(System.currentTimeMillis() - time);
        MyLogger.logTimeTaken("StatThread", "calculated all stats", time);

        return playerStats;
    }

    /** Gets the statistic data for an individual player. If somehow the player
     cannot be found, this returns 0.*/
    private int getIndividualStat() {
        OfflinePlayer player = OfflinePlayerHandler.getOfflinePlayer(request.getPlayerName());
        if (player != null) {
            switch (request.getStatistic().getType()) {
                case UNTYPED -> {
                    return player.getStatistic(request.getStatistic());
                }
                case ENTITY -> {
                    return player.getStatistic(request.getStatistic(), request.getEntity());
                }
                case BLOCK -> {
                    return player.getStatistic(request.getStatistic(), request.getBlock());
                }
                case ITEM -> {
                    return player.getStatistic(request.getStatistic(), request.getItem());
                }
            }
        }
        return 0;
    }
}