package com.gmail.artemis.the.gr8.playerstats.reload;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.TestFileHandler;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatThread;
import com.gmail.artemis.the.gr8.playerstats.utils.MessageFactory;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;

public class ReloadThread extends Thread {

    private final int threshold;

    private static ConfigHandler config;
    private static TestFileHandler testFile;
    private final Main plugin;

    private final StatThread statThread;
    private final CommandSender sender;
    private final boolean firstTimeLoading;

    public ReloadThread(int threshold, ConfigHandler c, TestFileHandler t, Main p, @Nullable StatThread s, @Nullable CommandSender se, boolean firstTime) {
        this.threshold = threshold;
        config = c;
        testFile = t;
        plugin = p;

        statThread = s;
        sender = se;
        firstTimeLoading = firstTime;

        plugin.getLogger().info("ReloadThread created");
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();

        if (!firstTimeLoading) {
            if (statThread != null && statThread.isAlive()) {
                try {
                    plugin.getLogger().info("Waiting for statThread to finish up...");
                    statThread.join();
                } catch (InterruptedException e) {
                    plugin.getLogger().warning(e.toString());
                    throw new RuntimeException(e);
                }
            }
            plugin.getLogger().info("Reloading!");
            if (config.reloadConfig()) {
                OfflinePlayerHandler.updateOfflinePlayerList(getPlayerMap(false));

                testFile.saveTimeTaken(System.currentTimeMillis() - time, 2);
                plugin.getLogger().info("Amount of relevant players: " + OfflinePlayerHandler.getOfflinePlayerCount());
                plugin.logTimeTaken("ReloadThread", "loading offline players", time);
                if (sender != null) {
                    sender.sendMessage(MessageFactory.getPluginPrefix() + ChatColor.GREEN + "Config reloaded!");
                }
            }
        }
        else {
            plugin.getLogger().info("Loading offline players...");
            OfflinePlayerHandler.updateOfflinePlayerList(getPlayerMap(true));

            testFile.saveThreshold(OfflinePlayerHandler.getOfflinePlayerCount(), threshold);
            testFile.saveTimeTaken(System.currentTimeMillis() - time, 1);
            plugin.getLogger().info("Amount of relevant players: " + OfflinePlayerHandler.getOfflinePlayerCount());
            plugin.logTimeTaken("ReloadThread", "loading offline players", time);
            ThreadManager.recordCalcTime(System.currentTimeMillis() - time);
        }
    }

    private ConcurrentHashMap<String, UUID> getPlayerMap(boolean firstTimeLoading) {
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        int size = firstTimeLoading ? offlinePlayers.length : OfflinePlayerHandler.getOfflinePlayerCount();

        ConcurrentHashMap<String, UUID> playerMap = new ConcurrentHashMap<>(size);

        ReloadAction task = new ReloadAction(threshold, offlinePlayers, config.whitelistOnly(), config.excludeBanned(), config.lastPlayedLimit(), playerMap);
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        commonPool.invoke(task);

        ConcurrentHashMap<String, UUID> newPlayerMap = new ConcurrentHashMap<>(playerMap.size());

        /*
        for (int i = 0; i < 11; i++) {
            for (String key : playerMap.keySet()) {
                newPlayerMap.put(key + i, playerMap.get(key));
            }
        }
         */

        newPlayerMap.putAll(playerMap);
        return newPlayerMap;
    }
}
