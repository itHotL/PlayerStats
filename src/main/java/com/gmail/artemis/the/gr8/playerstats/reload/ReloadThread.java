package com.gmail.artemis.the.gr8.playerstats.reload;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.enums.DebugLevel;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageWriter;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatThread;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
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
    private static MessageWriter messageWriter;

    private final StatThread statThread;
    private final CommandSender sender;

    public ReloadThread(BukkitAudiences a, ConfigHandler c, MessageWriter m, int threshold, int ID, @Nullable StatThread s, @Nullable CommandSender se) {
        this.threshold = threshold;
        reloadThreadID = ID;

        adventure = a;
        config = c;
        messageWriter = m;

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
                statThread.stopThread();
                return;

                /*
                try {
                    MyLogger.waitingForOtherThread(this.getName(), statThread.getName());
                    statThread.join();
                } catch (InterruptedException e) {
                    MyLogger.logException(e, "ReloadThread", "run(), trying to join" + statThread.getName());
                    throw new RuntimeException(e);
                }
                */
            }
            MyLogger.logMsg("Reloading!", false);
            if (config.reloadConfig()) {
                boolean isBukkitConsole = sender instanceof ConsoleCommandSender && Bukkit.getName().equalsIgnoreCase("CraftBukkit");

                try {
                    OfflinePlayerHandler.updateOfflinePlayerList(getPlayerMap());
                }
                catch (ConcurrentModificationException e) {
                    MyLogger.logException(e, "ReloadThread", "run(), trying to update OfflinePlayerList during a reload");
                    if (sender != null) {
                        adventure.sender(sender).sendMessage(messageWriter.partiallyReloaded(isBukkitConsole));
                    }
                }

                MyLogger.logTimeTaken("ReloadThread", ("loaded " + OfflinePlayerHandler.getOfflinePlayerCount() + " offline players"), time);
                if (sender != null) {
                    adventure.sender(sender).sendMessage(messageWriter.reloadedConfig(isBukkitConsole));
                }
            }
        }
        //during first start-up...
        else {
            try {
                OfflinePlayerHandler.updateOfflinePlayerList(getPlayerMap());
                ThreadManager.recordCalcTime(System.currentTimeMillis() - time);
                MyLogger.logTimeTaken("ReloadThread",
                        ("loaded " + OfflinePlayerHandler.getOfflinePlayerCount() + " offline players"), time);
            }
            catch (ConcurrentModificationException e) {
                MyLogger.logException(e, "ReloadThread", "run(), trying to update OfflinePlayerList during first start-up");
            }
        }
    }

    private @NotNull ConcurrentHashMap<String, UUID> getPlayerMap() throws ConcurrentModificationException {
        long time = System.currentTimeMillis();

        OfflinePlayer[] offlinePlayers;
        if (config.whitelistOnly()) {
            offlinePlayers = Bukkit.getWhitelistedPlayers().toArray(OfflinePlayer[]::new);
            MyLogger.logTimeTaken("ReloadThread", "retrieved whitelist", time, DebugLevel.MEDIUM);
        }
        else if (config.excludeBanned()) {
            Set<OfflinePlayer> bannedPlayers = Bukkit.getBannedPlayers();
            offlinePlayers = Arrays.stream(Bukkit.getOfflinePlayers())
                    .parallel()
                    .filter(offlinePlayer -> !bannedPlayers.contains(offlinePlayer)).toArray(OfflinePlayer[]::new);
            MyLogger.logTimeTaken("ReloadThread", "retrieved banlist", time, DebugLevel.MEDIUM);
        }
        else {
            offlinePlayers = Bukkit.getOfflinePlayers();
            MyLogger.logTimeTaken("ReloadThread", "retrieved list of Offline Players", time, DebugLevel.MEDIUM);
        }

        int size = offlinePlayers != null ? offlinePlayers.length : 16;
        ConcurrentHashMap<String, UUID> playerMap = new ConcurrentHashMap<>(size);

        ReloadAction task = new ReloadAction(threshold, offlinePlayers, config.getLastPlayedLimit(), playerMap);
        MyLogger.actionCreated((offlinePlayers != null) ? offlinePlayers.length : 0);

        ForkJoinPool.commonPool().invoke(task);
        MyLogger.actionFinished(1);

        return generateFakeExtraPlayers(playerMap, 10);
    }

    //generate fake extra players for PlayerStats, by looping over the real offlinePlayers multiple times
    private @NotNull ConcurrentHashMap<String, UUID> generateFakeExtraPlayers(@NotNull ConcurrentHashMap<String, UUID> realPlayers, int loops) {
        if (loops == 0 || loops == 1) return realPlayers;

        ConcurrentHashMap<String, UUID> newPlayerMap = new ConcurrentHashMap<>(realPlayers.size() * loops);
        for (int i = 0; i < loops; i++) {
            for (String key : realPlayers.keySet()) {
                newPlayerMap.put(key + i, realPlayers.get(key));
            }
        }
        return newPlayerMap;
    }
}