package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.enums.StandardMessage;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.msg.OutputManager;
import com.gmail.artemis.the.gr8.playerstats.reload.ReloadThread;
import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.google.common.collect.ImmutableList;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class StatThread extends Thread {

    private static ConfigHandler config;
    private final OutputManager outputManager;
    private final OfflinePlayerHandler offlinePlayerHandler;

    private final ReloadThread reloadThread;
    private final StatRequest request;

    public StatThread(ConfigHandler c, OutputManager m, OfflinePlayerHandler o, int ID, StatRequest s, @Nullable ReloadThread r) {
        config = c;
        outputManager = m;
        offlinePlayerHandler = o;

        reloadThread = r;
        request = s;

        this.setName("StatThread-" + request.getCommandSender().getName() + "-" + ID);
        MyLogger.threadCreated(this.getName());
    }

    @Override
    public void run() throws IllegalStateException, NullPointerException {
        MyLogger.threadStart(this.getName());

        if (request == null) {
            throw new NullPointerException("No statistic request was found!");
        }
        if (reloadThread != null && reloadThread.isAlive()) {
            try {
                MyLogger.waitingForOtherThread(this.getName(), reloadThread.getName());
                outputManager.sendFeedbackMsg(request.getCommandSender(), StandardMessage.STILL_RELOADING);
                reloadThread.join();

            } catch (InterruptedException e) {
                MyLogger.logException(e, "StatThread", "Trying to join " + reloadThread.getName());
                throw new RuntimeException(e);
            }
        }

        long lastCalc = ThreadManager.getLastRecordedCalcTime();
        if (lastCalc > 2000) {
            outputManager.sendFeedbackMsgWaitAMoment(request.getCommandSender(), lastCalc > 20000);
        }

        Target selection = request.getSelection();
        try {
            switch (selection) {
                case PLAYER -> outputManager.sendPlayerStat(request, getIndividualStat());
                case TOP -> outputManager.sendTopStat(request, getTopStats());
                case SERVER -> outputManager.sendServerStat(request, getServerTotal());
            }
        } catch (ConcurrentModificationException e) {
            if (!request.isConsoleSender()) {
                outputManager.sendFeedbackMsg(request.getCommandSender(), StandardMessage.UNKNOWN_ERROR);
            }
        }
    }

    private LinkedHashMap<String, Integer> getTopStats() throws ConcurrentModificationException {
        return getAllStats().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(config.getTopListMaxSize()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private long getServerTotal() {
        List<Integer> numbers = getAllStats().values().parallelStream().toList();
        return numbers.parallelStream().mapToLong(Integer::longValue).sum();
    }

    //invokes a bunch of worker pool threads to divide and conquer (get the statistics for all players in the list)
    private @NotNull ConcurrentHashMap<String, Integer> getAllStats() throws ConcurrentModificationException {
        long time = System.currentTimeMillis();

        int size = offlinePlayerHandler.getOfflinePlayerCount() != 0 ? offlinePlayerHandler.getOfflinePlayerCount() : 16;
        ConcurrentHashMap<String, Integer> playerStats = new ConcurrentHashMap<>(size);
        ImmutableList<String> playerNames = ImmutableList.copyOf(offlinePlayerHandler.getOfflinePlayerNames());

        StatAction task = new StatAction(offlinePlayerHandler, playerNames, request, playerStats);
        MyLogger.actionCreated(playerNames.size());
        ForkJoinPool commonPool = ForkJoinPool.commonPool();

        try {
            commonPool.invoke(task);
        } catch (ConcurrentModificationException e) {
            MyLogger.logMsg("The request could not be executed due to a ConcurrentModificationException. " +
                    "This likely happened because Bukkit hasn't fully initialized all player-data yet. " +
                    "Try again and it should be fine!", true);
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
        OfflinePlayer player = offlinePlayerHandler.getOfflinePlayer(request.getPlayerName());
        if (player != null) {
            return switch (request.getStatistic().getType()) {
                case UNTYPED -> player.getStatistic(request.getStatistic());
                case ENTITY -> player.getStatistic(request.getStatistic(), request.getEntity());
                case BLOCK -> player.getStatistic(request.getStatistic(), request.getBlock());
                case ITEM -> player.getStatistic(request.getStatistic(), request.getItem());
            };
        }
        return 0;
    }
}