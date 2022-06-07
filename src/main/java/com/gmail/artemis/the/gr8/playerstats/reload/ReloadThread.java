package com.gmail.artemis.the.gr8.playerstats.reload;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.TestFileHandler;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatThread;
import com.gmail.artemis.the.gr8.playerstats.utils.MessageFactory;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.ConcurrentModificationException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

public class ReloadThread extends Thread {

    private final int threshold;

    private final BukkitAudiences adventure;
    private static ConfigHandler config;
    private static TestFileHandler testFile;
    private final MessageFactory messageFactory;
    private final Main plugin;

    private final StatThread statThread;
    private final CommandSender sender;
    private final boolean firstTimeLoading;

    public ReloadThread(int threshold, BukkitAudiences b, ConfigHandler c, TestFileHandler t, MessageFactory m, Main p, @Nullable StatThread s, @Nullable CommandSender se, boolean firstTime) {
        this.threshold = threshold;
        adventure = b;
        config = c;
        testFile = t;
        messageFactory = m;
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

                try {
                    OfflinePlayerHandler.updateOfflinePlayerList(getPlayerMap(false));
                } catch (ConcurrentModificationException e) {
                    plugin.getLogger().warning("The request could not be fully executed due to a ConcurrentModificationException");
                    if (sender != null) {
                        adventure.sender(sender).sendMessage(messageFactory.partiallyReloaded());
                    }
                }

                testFile.saveTimeTaken(System.currentTimeMillis() - time, 2);
                plugin.getLogger().info("Amount of relevant players: " + OfflinePlayerHandler.getOfflinePlayerCount());
                plugin.logTimeTaken("ReloadThread", "loading offline players", time);
                if (sender != null) {
                    adventure.sender(sender).sendMessage(messageFactory.reloadedConfig());
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

        try {
            commonPool.invoke(task);
        } catch (ConcurrentModificationException e) {
            throw new ConcurrentModificationException(e.toString());
        }
        return playerMap;
    }

    private ConcurrentHashMap<String, UUID> generateFakeExtraPlayers(ConcurrentHashMap<String, UUID> realPlayers, int loops) {
        ConcurrentHashMap<String, UUID> newPlayerMap = new ConcurrentHashMap<>(realPlayers.size() * loops);
        for (int i = 0; i < loops; i++) {
            for (String key : realPlayers.keySet()) {
                newPlayerMap.put(key + i, realPlayers.get(key));
            }
        }
        return newPlayerMap;
    }
}
