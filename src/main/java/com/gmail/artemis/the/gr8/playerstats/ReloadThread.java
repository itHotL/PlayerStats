package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatThread;
import com.gmail.artemis.the.gr8.playerstats.utils.MessageFactory;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class ReloadThread extends Thread {

    private final ConfigHandler config;
    private final OfflinePlayerHandler offlinePlayerHandler;
    private final Main plugin;

    private final StatThread statThread;
    private final CommandSender sender;
    private final boolean firstTimeLoading;

    public ReloadThread(ConfigHandler c, OfflinePlayerHandler o, Main p, @Nullable StatThread s, @Nullable CommandSender se, boolean firstTime) {
        config = c;
        offlinePlayerHandler = o;
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
                offlinePlayerHandler.updateOfflinePlayerList();

                plugin.getLogger().info("Amount of relevant players: " + offlinePlayerHandler.getOfflinePlayerCount());
                plugin.logTimeTaken("ReloadThread", "loading offline players", time);
                if (sender != null) {
                    sender.sendMessage(MessageFactory.getPluginPrefix() + ChatColor.GREEN + "Config reloaded!");
                }
            }
        }
        else {
            plugin.getLogger().info("Loading offline players...");
            offlinePlayerHandler.updateOfflinePlayerList();
            plugin.getLogger().info("Amount of relevant players: " + offlinePlayerHandler.getOfflinePlayerCount());
            plugin.logTimeTaken("ReloadThread", "loading offline players", time);
            ThreadManager.recordCalcTime(System.currentTimeMillis() - time);
        }
    }
}
