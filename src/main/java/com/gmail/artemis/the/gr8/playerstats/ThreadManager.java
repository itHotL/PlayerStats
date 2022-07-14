package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.enums.StandardMessage;
import com.gmail.artemis.the.gr8.playerstats.msg.OutputManager;
import com.gmail.artemis.the.gr8.playerstats.reload.ReloadThread;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatThread;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public final class ThreadManager {

    private static volatile ThreadManager instance;

    private final static int threshold = 10;
    private int statThreadID;
    private int reloadThreadID;

    private static ConfigHandler config;
    private static OutputManager messageSender;
    private final OfflinePlayerHandler offlinePlayerHandler;

    private ReloadThread lastActiveReloadThread;
    private StatThread lastActiveStatThread;
    private final HashMap<String, Thread> statThreads;
    private static long lastRecordedCalcTime;

    private ThreadManager(ConfigHandler c, OutputManager m, OfflinePlayerHandler o) {
        config = c;
        messageSender = m;
        offlinePlayerHandler = o;

        statThreads = new HashMap<>();
        statThreadID = 0;
        reloadThreadID = 0;
        lastRecordedCalcTime = 0;

        startReloadThread(null);
    }

    public static ThreadManager getInstance(ConfigHandler config, OutputManager messageSender, OfflinePlayerHandler offlinePlayerHandler) {
        ThreadManager threadManager = instance;
        if (threadManager != null) {
            return threadManager;
        }
        synchronized (ThreadManager.class) {
            if (instance == null) {
                instance = new ThreadManager(config, messageSender, offlinePlayerHandler);
            }
            return instance;
        }
    }

    public static int getTaskThreshold() {
        return threshold;
    }

    public void startReloadThread(CommandSender sender) {
        if (lastActiveReloadThread == null || !lastActiveReloadThread.isAlive()) {
            reloadThreadID += 1;

            lastActiveReloadThread = new ReloadThread(config, messageSender, offlinePlayerHandler, reloadThreadID, lastActiveStatThread, sender);
            lastActiveReloadThread.start();
        }
        else {
            MyLogger.threadAlreadyRunning(lastActiveReloadThread.getName());
        }
    }

    public void startStatThread(StatRequest request) {
        statThreadID += 1;
        String cmdSender = request.getCommandSender().getName();

        if (config.limitStatRequests() && statThreads.containsKey(cmdSender)) {
            Thread runningThread = statThreads.get(cmdSender);
            if (runningThread.isAlive()) {
                messageSender.sendFeedbackMsg(request.getCommandSender(), StandardMessage.REQUEST_ALREADY_RUNNING);
            } else {
                startNewStatThread(request);
            }
        } else {
            startNewStatThread(request);
        }
    }

    /** Store the duration in milliseconds of the last top-stat-lookup
     (or of loading the offline-player-list if no look-ups have been done yet). */
    public static void recordCalcTime(long time) {
        lastRecordedCalcTime = time;
    }

    /** Returns the duration in milliseconds of the last top-stat-lookup
     (or of loading the offline-player-list if no look-ups have been done yet). */
    public static long getLastRecordedCalcTime() {
        return lastRecordedCalcTime;
    }

    private void startNewStatThread(StatRequest request) {
        lastActiveStatThread = new StatThread(config, messageSender, offlinePlayerHandler, statThreadID, request, lastActiveReloadThread);
        statThreads.put(request.getCommandSender().getName(), lastActiveStatThread);
        lastActiveStatThread.start();
    }
}