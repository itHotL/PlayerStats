package com.gmail.artemis.the.gr8.playerstats.reload;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatThread;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageFactory;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.ConcurrentModificationException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

public class ReloadThread extends Thread {

    private final int threshold;

    private final BukkitAudiences adventure;
    private static ConfigHandler config;
    private static MessageFactory messageFactory;
    private final Main plugin;

    private final StatThread statThread;
    private final CommandSender sender;
    private final boolean firstTimeLoading;

    public ReloadThread(BukkitAudiences a, ConfigHandler c, MessageFactory m, Main p, int threshold, boolean firstTime, @Nullable StatThread s, @Nullable CommandSender se) {
        this.threshold = threshold;
        adventure = a;
        config = c;
        messageFactory = m;
        plugin = p;

        statThread = s;
        sender = se;
        firstTimeLoading = firstTime;
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();

        //if reload is triggered by /statreload...
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
                }
                catch (ConcurrentModificationException e) {
                    plugin.getLogger().warning("The request could not be fully executed due to a ConcurrentModificationException");
                    if (sender != null) {
                        adventure.sender(sender).sendMessage(messageFactory.partiallyReloaded(sender instanceof ConsoleCommandSender));
                    }
                }

                plugin.logTimeTaken("ReloadThread", ("loaded " + OfflinePlayerHandler.getOfflinePlayerCount() + " offline players"), time);
                if (sender != null) {
                    adventure.sender(sender).sendMessage(messageFactory.reloadedConfig(sender instanceof ConsoleCommandSender));
                }
            }
        }
        //during first start-up...
        else {
            OfflinePlayerHandler.updateOfflinePlayerList(getPlayerMap(true));
            plugin.logTimeTaken("ReloadThread", ("loaded " + OfflinePlayerHandler.getOfflinePlayerCount() + " offline players"), time);
            ThreadManager.recordCalcTime(System.currentTimeMillis() - time);
        }
    }

    private ConcurrentHashMap<String, UUID> getPlayerMap(boolean firstTimeLoading) {
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();

        int size;
        if (firstTimeLoading) {
            size = offlinePlayers.length;
        }
        else {
            size = OfflinePlayerHandler.getOfflinePlayerCount() != 0 ? OfflinePlayerHandler.getOfflinePlayerCount() : 16;
        }

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
