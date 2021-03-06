package com.gmail.artemis.the.gr8.playerstats.reload;

import com.gmail.artemis.the.gr8.playerstats.ShareManager;
import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.enums.DebugLevel;
import com.gmail.artemis.the.gr8.playerstats.enums.StandardMessage;
import com.gmail.artemis.the.gr8.playerstats.msg.OutputManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatThread;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Predicate;

public class ReloadThread extends Thread {

    private static ConfigHandler config;
    private static OutputManager messageSender;
    private final OfflinePlayerHandler offlinePlayerHandler;

    private static ShareManager shareManager;

    private final int reloadThreadID;
    private final StatThread statThread;

    private final CommandSender sender;


    public ReloadThread(ConfigHandler c, OutputManager m, OfflinePlayerHandler o, int ID, @Nullable StatThread s, @Nullable CommandSender se) {
        config = c;
        messageSender = m;
        offlinePlayerHandler = o;

        shareManager = ShareManager.getInstance(c);

        reloadThreadID = ID;
        statThread = s;
        sender = se;

        this.setName("ReloadThread-" + reloadThreadID);
        MyLogger.threadCreated(this.getName());
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        MyLogger.threadStart(this.getName());

        if (statThread != null && statThread.isAlive()) {
            try {
                MyLogger.waitingForOtherThread(this.getName(), statThread.getName());
                statThread.join();
            } catch (InterruptedException e) {
                MyLogger.logException(e, "ReloadThread", "run(), trying to join " + statThread.getName());
                throw new RuntimeException(e);
            }
        }

        if (reloadThreadID != 1 && config.reloadConfig()) {  //during a reload
            MyLogger.logMsg("Reloading!", false);
            reloadEverything();

            if (sender != null) {
                messageSender.sendFeedbackMsg(sender, StandardMessage.RELOADED_CONFIG);
            }
        }
        else {  //during first start-up
            MyLogger.setDebugLevel(config.getDebugLevel());
            offlinePlayerHandler.updateOfflinePlayerList(loadOfflinePlayers());
            ThreadManager.recordCalcTime(System.currentTimeMillis() - time);
        }
    }

    private void reloadEverything() {
        MyLogger.setDebugLevel(config.getDebugLevel());
        messageSender.updateMessageWriters(config);
        offlinePlayerHandler.updateOfflinePlayerList(loadOfflinePlayers());
        shareManager.updateSettings(config);
    }

    private ConcurrentHashMap<String, UUID> loadOfflinePlayers() {
        long time = System.currentTimeMillis();

        OfflinePlayer[] offlinePlayers;
        if (config.whitelistOnly()) {
            offlinePlayers = Bukkit.getWhitelistedPlayers().toArray(OfflinePlayer[]::new);
            MyLogger.logTimeTaken("ReloadThread",
                    "retrieved whitelist", time, DebugLevel.MEDIUM);
        }
        else if (config.excludeBanned()) {
            if (Bukkit.getPluginManager().getPlugin("LiteBans") != null) {
                offlinePlayers = Arrays.stream(Bukkit.getOfflinePlayers())
                        .parallel()
                        .filter(Predicate.not(OfflinePlayer::isBanned))
                        .toArray(OfflinePlayer[]::new);
            } else {
                Set<OfflinePlayer> bannedPlayers = Bukkit.getBannedPlayers();
                offlinePlayers = Arrays.stream(Bukkit.getOfflinePlayers())
                        .parallel()
                        .filter(offlinePlayer -> !bannedPlayers.contains(offlinePlayer)).toArray(OfflinePlayer[]::new);
            }
            MyLogger.logTimeTaken("ReloadThread",
                    "retrieved banlist", time, DebugLevel.MEDIUM);
        }
        else {
            offlinePlayers = Bukkit.getOfflinePlayers();
            MyLogger.logTimeTaken("ReloadThread",
                    "retrieved list of Offline Players", time, DebugLevel.MEDIUM);
        }

        int size = offlinePlayers != null ? offlinePlayers.length : 16;
        ConcurrentHashMap<String, UUID> playerMap = new ConcurrentHashMap<>(size);

        ReloadAction task = new ReloadAction(offlinePlayers, config.getLastPlayedLimit(), playerMap);
        MyLogger.actionCreated((offlinePlayers != null) ? offlinePlayers.length : 0);
        ForkJoinPool.commonPool().invoke(task);
        MyLogger.actionFinished(1);

        MyLogger.logTimeTaken("ReloadThread",
                ("loaded " + playerMap.size() + " offline players"), time);
        return playerMap;
    }
}