package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.reload.ReloadThread;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatThread;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageFactory;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;


public class ThreadManager {

    private static final int threshold = 10;

    private final Main plugin;
    private final BukkitAudiences adventure;
    private static ConfigHandler config;
    private static MessageFactory messageFactory;

    private ReloadThread reloadThread;
    private StatThread statThread;
    private static long lastRecordedCalcTime;

    public ThreadManager(BukkitAudiences a, ConfigHandler c, MessageFactory m, Main p) {
        adventure = a;
        config = c;
        messageFactory = m;
        plugin = p;

        startReloadThread(null, true);
    }

    public void startReloadThread(CommandSender sender, boolean firstTimeLoading) {
        reloadThread = new ReloadThread(adventure, config, messageFactory, plugin, threshold, firstTimeLoading, statThread, sender);
        reloadThread.start();
    }

    public void startStatThread(StatRequest request) {
        statThread = new StatThread(adventure, config, messageFactory, plugin, threshold, request, reloadThread);
        statThread.start();
    }

    /** Store the time in milliseconds that the last top-stat-lookup took (or loading the offline-player-list if no look-ups have been done yet). */
    public static void recordCalcTime(long time) {
        lastRecordedCalcTime = time;
    }

    /** Returns the time in milliseconds the last top-stat-lookup took (or loading the offline-player-list if no look-ups have been done yet). */
    public static long getLastRecordedCalcTime() {
        return lastRecordedCalcTime;
    }
}
