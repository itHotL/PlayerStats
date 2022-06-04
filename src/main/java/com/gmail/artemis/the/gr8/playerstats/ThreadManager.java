package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.reload.ReloadThread;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatThread;
import com.gmail.artemis.the.gr8.playerstats.utils.MessageFactory;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;


public class ThreadManager {

    private final Main plugin;
    private final BukkitAudiences adventure;
    private static ConfigHandler config;
    private final MessageFactory messageFactory;

    private ReloadThread reloadThread;
    private StatThread statThread;
    private static long lastRecordedCalcTime;

    public ThreadManager(Main p, BukkitAudiences b, ConfigHandler c, MessageFactory m) {
        plugin = p;
        adventure = b;
        config = c;
        messageFactory = m;

        startReloadThread(null, true);
    }

    public void startReloadThread(CommandSender sender, boolean firstTimeLoading) {
        reloadThread = new ReloadThread(config, plugin, statThread, sender, firstTimeLoading);
        reloadThread.start();
    }

    public void startStatThread(StatRequest request) {
        statThread = new StatThread(request, reloadThread, adventure, config, messageFactory, plugin);
        statThread.start();
    }

    //store the time in milliseconds that the last top-stat-lookup took (or loading the offline-player-list if no look-ups have been done yet)
    public static void recordCalcTime(long time) {
        lastRecordedCalcTime = time;
    }

    //returns the time in milliseconds the last top-stat-lookup took (or loading the offline-player-list if no look-ups have been done yet)
    public static long getLastRecordedCalcTime() {
        return lastRecordedCalcTime;
    }
}
