package com.gmail.artemis.the.gr8.playerstats.reload;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatThread;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageFactory;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

public class ReloadThread extends Thread {

    private final int threshold;
    private final int reloadThreadID;

    private final BukkitAudiences adventure;
    private static ConfigHandler config;
    private static MessageFactory messageFactory;
    private final Main plugin;

    private final StatThread statThread;
    private final CommandSender sender;

    public ReloadThread(BukkitAudiences a, ConfigHandler c, MessageFactory m, Main p, int threshold, int ID, @Nullable StatThread s, @Nullable CommandSender se) {
        this.threshold = threshold;
        reloadThreadID = ID;

        adventure = a;
        config = c;
        messageFactory = m;
        plugin = p;

        statThread = s;
        sender = se;

        this.setName("ReloadThread-" + reloadThreadID);
        MyLogger.threadCreated(this.getName());
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        MyLogger.threadStart(this.getName());

        //if reload is triggered by /statreload (aka this thread does not have ID number 1)...
        if (reloadThreadID != 1) {
            if (statThread != null && statThread.isAlive()) {
                try {
                    MyLogger.waitingForOtherThread(this.getName(), statThread.getName());
                    statThread.join();
                } catch (InterruptedException e) {
                    plugin.getLogger().warning(e.toString());
                    throw new RuntimeException(e);
                }
            }
            plugin.getLogger().info("Reloading!");
            if (config.reloadConfig()) {

                try {
                    OfflinePlayerHandler.updateOfflinePlayerList(getPlayerMap());
                }
                catch (ConcurrentModificationException e) {
                    plugin.getLogger().warning("The request could not be fully executed due to a ConcurrentModificationException");
                    if (sender != null) {
                        adventure.sender(sender).sendMessage(messageFactory.partiallyReloaded(sender instanceof ConsoleCommandSender));
                    }
                }

                MyLogger.logTimeTakenDefault("ReloadThread", ("loaded " + OfflinePlayerHandler.getOfflinePlayerCount() + " offline players"), time);
                if (sender != null) {
                    adventure.sender(sender).sendMessage(messageFactory.reloadedConfig(sender instanceof ConsoleCommandSender));
                }
            }
        }
        //during first start-up...
        else {
            OfflinePlayerHandler.updateOfflinePlayerList(getPlayerMap());
            MyLogger.logTimeTakenDefault("ReloadThread", ("loaded " + OfflinePlayerHandler.getOfflinePlayerCount() + " offline players"), time);
            ThreadManager.recordCalcTime(System.currentTimeMillis() - time);
        }
    }

    private ConcurrentHashMap<String, UUID> getPlayerMap() {
        long time = System.currentTimeMillis();
        OfflinePlayer[] offlinePlayers;
        if (config.whitelistOnly()) {
            offlinePlayers = Bukkit.getWhitelistedPlayers().toArray(OfflinePlayer[]::new);
            MyLogger.logTimeTaken("ReloadThread", "getting white-list-only list", time);
        }
        else if (config.excludeBanned()) {
            Set<OfflinePlayer> bannedPlayers = Bukkit.getBannedPlayers();
            offlinePlayers = Arrays.stream(Bukkit.getOfflinePlayers())
                    .parallel()
                    .filter(offlinePlayer -> !bannedPlayers.contains(offlinePlayer)).toArray(OfflinePlayer[]::new);
            MyLogger.logTimeTaken("ReloadThread", "getting excluding-banned-players list", time);
        }
        else {
            offlinePlayers = Bukkit.getOfflinePlayers();
            MyLogger.logTimeTaken("ReloadThread", "getting regular player list", time);
        }

        int size = offlinePlayers != null ? offlinePlayers.length : 16;
        ConcurrentHashMap<String, UUID> playerMap = new ConcurrentHashMap<>(size);

        ReloadAction task = new ReloadAction(threshold, offlinePlayers, config.lastPlayedLimit(), playerMap);
        MyLogger.actionCreated((offlinePlayers != null) ? offlinePlayers.length : 0);
        ForkJoinPool commonPool = ForkJoinPool.commonPool();

        try {
            commonPool.invoke(task);
        } catch (ConcurrentModificationException e) {
            throw new ConcurrentModificationException(e.toString());
        }

        MyLogger.actionFinished(1);
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
