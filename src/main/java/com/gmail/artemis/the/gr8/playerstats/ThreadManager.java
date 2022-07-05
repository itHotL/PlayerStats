package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageWriter;
import com.gmail.artemis.the.gr8.playerstats.reload.ReloadThread;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatThread;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;


public class ThreadManager {

    private final int threshold = 10;
    private int statThreadID;
    private int reloadThreadID;

    private final Main plugin;
    private final BukkitAudiences adventure;
    private static ConfigHandler config;
    private static MessageWriter messageWriter;

    private ReloadThread reloadThread;
    private StatThread statThread;
    private static long lastRecordedCalcTime;

    public ThreadManager(BukkitAudiences a, ConfigHandler c, MessageWriter m, Main p) {
        adventure = a;
        config = c;
        messageWriter = m;
        plugin = p;

        statThreadID = 0;
        reloadThreadID = 0;
        lastRecordedCalcTime = 0;
        startReloadThread(null);
    }

    public void startReloadThread(CommandSender sender) {
        if (reloadThread == null || !reloadThread.isAlive()) {
            reloadThreadID += 1;

            reloadThread = new ReloadThread(adventure, config, messageWriter, threshold, reloadThreadID, statThread, sender);
            reloadThread.start();
        }
        else {
            MyLogger.threadAlreadyRunning(reloadThread.getName());
        }
    }

    public void startStatThread(StatRequest request) {
        statThreadID += 1;

        statThread = new StatThread(adventure, config, messageWriter, plugin, statThreadID, threshold, request, reloadThread);
        statThread.start();
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
}