package com.artemis.the.gr8.playerstats.reload;

import com.artemis.the.gr8.playerstats.Main;
import com.artemis.the.gr8.playerstats.ShareManager;
import com.artemis.the.gr8.playerstats.enums.StandardMessage;
import com.artemis.the.gr8.playerstats.msg.OutputManager;
import com.artemis.the.gr8.playerstats.msg.msgutils.LanguageKeyHandler;
import com.artemis.the.gr8.playerstats.statistic.StatCalculator;
import com.artemis.the.gr8.playerstats.statistic.StatThread;
import com.artemis.the.gr8.playerstats.utils.MyLogger;
import com.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.artemis.the.gr8.playerstats.enums.DebugLevel;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

/** The Thread that is in charge of reloading PlayerStats. */
public final class ReloadThread extends Thread {

    private static ConfigHandler config;
    private static OutputManager outputManager;

    private final StatThread statThread;
    private final CommandSender sender;

    public ReloadThread(ConfigHandler c, OutputManager m, int ID, @Nullable StatThread s, @Nullable CommandSender se) {
        config = c;
        outputManager = m;

        statThread = s;
        sender = se;

        this.setName("ReloadThread-" + ID);
        MyLogger.logHighLevelMsg(this.getName() + " created!");
    }

    /**
     * This method will perform a series of tasks. If a {@link StatThread}
     * is still running, it will join the statThread and wait for it to finish.
     * Then, it will reload the config, update the {@link LanguageKeyHandler},
     * the {@link OfflinePlayerHandler}, the {@link DebugLevel}, update
     * the share-settings in {@link ShareManager} and topListSize-settings
     * in {@link StatCalculator}, and update the MessageBuilders in the
     * {@link OutputManager}.
     */
    @Override
    public void run() {
        MyLogger.logHighLevelMsg(this.getName() + " started!");

        if (statThread != null && statThread.isAlive()) {
            try {
                MyLogger.logLowLevelMsg(this.getName() + ": Waiting for " + statThread.getName() + " to finish up...");
                statThread.join();
            } catch (InterruptedException e) {
                MyLogger.logException(e, "ReloadThread", "run(), trying to join " + statThread.getName());
                throw new RuntimeException(e);
            }
        }

        MyLogger.logLowLevelMsg("Reloading!");
        reloadEverything();

        if (sender != null) {
            outputManager.sendFeedbackMsg(sender, StandardMessage.RELOADED_CONFIG);
        }
    }

    private void reloadEverything() {
        config.reload();
        MyLogger.setDebugLevel(config.getDebugLevel());
        Main.getLanguageKeyHandler().reload();
        Main.getOfflinePlayerHandler().reload();

        OutputManager.updateMessageBuilders();
        ShareManager.updateSettings(config);
    }
}