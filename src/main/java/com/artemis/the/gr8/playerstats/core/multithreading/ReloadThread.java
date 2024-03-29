package com.artemis.the.gr8.playerstats.core.multithreading;

import com.artemis.the.gr8.playerstats.core.Main;
import com.artemis.the.gr8.playerstats.core.enums.StandardMessage;
import com.artemis.the.gr8.playerstats.core.msg.OutputManager;
import com.artemis.the.gr8.playerstats.core.utils.MyLogger;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

/** The Thread that is in charge of reloading PlayerStats. */
final class ReloadThread extends Thread {

    private final Main main;
    private static OutputManager outputManager;

    private final StatThread statThread;
    private final CommandSender sender;

    public ReloadThread(Main main, OutputManager m, int ID, @Nullable StatThread s, @Nullable CommandSender se) {
        this.main = main;
        outputManager = m;

        statThread = s;
        sender = se;

        this.setName("ReloadThread-" + ID);
        MyLogger.logHighLevelMsg(this.getName() + " created!");
    }

    /**
     * This method will call reload() from Main. If a {@link StatThread}
     * is still running, it will join the statThread and wait for it to finish.
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
        main.reloadPlugin();

        if (sender != null) {
            outputManager.sendFeedbackMsg(sender, StandardMessage.RELOADED_CONFIG);
        }
    }
}