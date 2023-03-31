package com.artemis.the.gr8.playerstats.core.multithreading;

import com.artemis.the.gr8.playerstats.core.Main;
import com.artemis.the.gr8.playerstats.core.msg.OutputManager;
import com.artemis.the.gr8.playerstats.core.config.ConfigHandler;
import com.artemis.the.gr8.playerstats.core.enums.StandardMessage;
import com.artemis.the.gr8.playerstats.api.StatRequest;
import com.artemis.the.gr8.playerstats.core.utils.MyLogger;
import com.artemis.the.gr8.playerstats.core.utils.OfflinePlayerHandler;
import com.google.common.collect.ImmutableList;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

    private final Main main;
    private final ConfigHandler config;
    private static OutputManager outputManager;

    private ReloadThread activatedReloadThread;
    private StatThread activatedStatThread;
    private final HashMap<String, Thread> statThreads;
    private static long lastRecordedCalcTime;

    public ThreadManager(Main main) {
        this.main = main;
        this.config = ConfigHandler.getInstance();
        outputManager = OutputManager.getInstance();

        statThreads = new HashMap<>();
        statThreadID = 0;
        reloadThreadID = 0;
        lastRecordedCalcTime = 0;
    }

    static int getTaskThreshold() {
        return threshold;
    }

    public static @NotNull StatAction getStatAction(StatRequest.Settings requestSettings) {
        OfflinePlayerHandler offlinePlayerHandler = OfflinePlayerHandler.getInstance();

        ImmutableList<String> relevantPlayerNames = ImmutableList.copyOf(offlinePlayerHandler.getIncludedOfflinePlayerNames());
        ConcurrentHashMap<String, Integer> resultingStatNumbers = new ConcurrentHashMap<>(relevantPlayerNames.size());
        StatAction task = new StatAction(relevantPlayerNames, requestSettings, resultingStatNumbers);

        MyLogger.actionCreated(relevantPlayerNames.size());
        return task;
    }

    public static @NotNull PlayerLoadAction getPlayerLoadAction(OfflinePlayer[] playersToLoad, ConcurrentHashMap<String, UUID> mapToFill) {
        PlayerLoadAction task = new PlayerLoadAction(playersToLoad, mapToFill);
        MyLogger.actionCreated(playersToLoad != null ? playersToLoad.length : 0);
        return task;
    }

    public void startReloadThread(CommandSender sender) {
        if (activatedReloadThread == null || !activatedReloadThread.isAlive()) {
            reloadThreadID += 1;

            activatedReloadThread = new ReloadThread(main, outputManager, reloadThreadID, activatedStatThread, sender);
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
        activatedStatThread = new StatThread(outputManager, statThreadID, request, activatedReloadThread);
        statThreads.put(request.getSettings().getCommandSender().getName(), activatedStatThread);
        activatedStatThread.start();
    }
}