package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageWriter;
import com.gmail.artemis.the.gr8.playerstats.reload.ReloadThread;
import com.gmail.artemis.the.gr8.playerstats.statistic.ShareQueue;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatThread;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class ThreadManager {

    private final int threshold = 10;
    private int statThreadID;
    private int reloadThreadID;

    private final BukkitAudiences adventure;
    private static ConfigHandler config;
    private static MessageWriter messageWriter;
    private final ShareQueue shareQueue;

    private ReloadThread lastActiveReloadThread;
    private StatThread lastActiveStatThread;
    private final HashMap<String, Thread> statThreads;
    private static long lastRecordedCalcTime;

    public ThreadManager(BukkitAudiences a, ConfigHandler c, MessageWriter m, @Nullable ShareQueue s) {
        adventure = a;
        config = c;
        messageWriter = m;
        shareQueue = s;

        statThreads = new HashMap<>();
        statThreadID = 0;
        reloadThreadID = 0;
        lastRecordedCalcTime = 0;
        startReloadThread(null);
    }

    public void startReloadThread(CommandSender sender) {
        if (lastActiveReloadThread == null || !lastActiveReloadThread.isAlive()) {
            reloadThreadID += 1;

            lastActiveReloadThread = new ReloadThread(adventure, config, messageWriter, threshold, reloadThreadID, lastActiveStatThread, sender);
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
                adventure.sender(request.getCommandSender()).sendMessage(messageWriter.requestAlreadyRunning(request.isBukkitConsoleSender()));
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
        lastActiveStatThread = new StatThread(adventure, config, messageWriter, statThreadID, threshold, request, lastActiveReloadThread);
        statThreads.put(request.getCommandSender().getName(), lastActiveStatThread);
        lastActiveStatThread.start();
    }
}