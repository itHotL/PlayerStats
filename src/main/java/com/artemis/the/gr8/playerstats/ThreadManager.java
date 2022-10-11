package com.artemis.the.gr8.playerstats;

import com.artemis.the.gr8.playerstats.msg.OutputManager;
import com.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.artemis.the.gr8.playerstats.enums.StandardMessage;
import com.artemis.the.gr8.playerstats.reload.ReloadThread;
import com.artemis.the.gr8.playerstats.statistic.RequestProcessor;
import com.artemis.the.gr8.playerstats.statistic.StatThread;
import com.artemis.the.gr8.playerstats.statistic.request.StatRequest;
import com.artemis.the.gr8.playerstats.utils.MyLogger;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * The ThreadManager is in charge of the Threads that PlayerStats
 * can utilize. It keeps track of past and currently active Threads,
 * to ensure a Player cannot start multiple Threads at the same time
 * (thereby limiting them to one stat-lookup at a time). It also
 * passes appropriate references along to the {@link StatThread}
 * or {@link ReloadThread}, to ensure those will never run at the
 * same time.
 */
public final class ThreadManager {

    private final static int threshold = 10;
    private int statThreadID;
    private int reloadThreadID;

    private static ConfigHandler config;
    private static OutputManager outputManager;
    private static RequestProcessor requestProcessor;

    private ReloadThread activatedReloadThread;
    private StatThread activatedStatThread;
    private final HashMap<String, Thread> statThreads;
    private static long lastRecordedCalcTime;

    public ThreadManager(ConfigHandler config, RequestProcessor requestProcessor, OutputManager outputManager) {
        ThreadManager.config = config;
        ThreadManager.outputManager = outputManager;
        ThreadManager.requestProcessor = requestProcessor;

        statThreads = new HashMap<>();
        statThreadID = 0;
        reloadThreadID = 0;
        lastRecordedCalcTime = 0;
    }

    public static int getTaskThreshold() {
        return threshold;
    }

    public void startReloadThread(CommandSender sender) {
        if (activatedReloadThread == null || !activatedReloadThread.isAlive()) {
            reloadThreadID += 1;

            activatedReloadThread = new ReloadThread(config, outputManager, reloadThreadID, activatedStatThread, sender);
            activatedReloadThread.start();
        }
        else {
            MyLogger.logLowLevelMsg("Another reloadThread is already running! (" + activatedReloadThread.getName() + ")");
        }
    }

    public void startStatThread(@NotNull StatRequest<?> request) {
        statThreadID += 1;
        CommandSender sender = request.getSettings().getCommandSender();

        if (config.limitStatRequests() && statThreads.containsKey(sender.getName())) {
            Thread runningThread = statThreads.get(sender.getName());

            if (runningThread.isAlive()) {
                outputManager.sendFeedbackMsg(sender, StandardMessage.REQUEST_ALREADY_RUNNING);
            } else {
                startNewStatThread(request);
            }
        } else {
            startNewStatThread(request);
        }
    }

    /**
     * Store the duration in milliseconds of the last top-stat-lookup
     * (or of loading the offline-player-list if no look-ups have been done yet).
     */
    public static void recordCalcTime(long time) {
        lastRecordedCalcTime = time;
    }

    /**
     * Returns the duration in milliseconds of the last top-stat-lookup
     * (or of loading the offline-player-list if no look-ups have been done yet).
     */
    public static long getLastRecordedCalcTime() {
        return lastRecordedCalcTime;
    }

    private void startNewStatThread(StatRequest<?> request) {
        activatedStatThread = new StatThread(outputManager, requestProcessor, statThreadID, request, activatedReloadThread);
        statThreads.put(request.getSettings().getCommandSender().getName(), activatedStatThread);
        activatedStatThread.start();
    }
}