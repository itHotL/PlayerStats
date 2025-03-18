package com.artemis.the.gr8.playerstats.core.multithreading;

import com.artemis.the.gr8.playerstats.core.msg.OutputManager;
import com.artemis.the.gr8.playerstats.core.statistic.StatRequestManager;
import com.artemis.the.gr8.playerstats.api.StatRequest;
import com.artemis.the.gr8.playerstats.api.StatResult;
import com.artemis.the.gr8.playerstats.core.utils.MyLogger;
import com.artemis.the.gr8.playerstats.core.enums.StandardMessage;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * The Thread that is in charge of getting and calculating statistics.
 */
final class StatThread extends Thread {

    private static OutputManager outputManager;

    private final ReloadThread reloadThread;
    private final StatRequest<?> statRequest;

    public StatThread(OutputManager m, int ID, StatRequest<?> s, @Nullable ReloadThread r) {
        outputManager = m;
        reloadThread = r;
        statRequest = s;

        this.setName("StatThread-" + statRequest.getSettings().getCommandSender().getName() + "-" + ID);
        MyLogger.logHighLevelMsg(this.getName() + " created!");
    }

    @Override
    public void run() throws IllegalStateException {
        MyLogger.logHighLevelMsg(this.getName() + " started!");
        CommandSender statRequester = statRequest.getSettings().getCommandSender();

        if (reloadThread != null && reloadThread.isAlive()) {
            try {
                MyLogger.logLowLevelMsg(this.getName() + ": Waiting for " + reloadThread.getName() + " to finish up...");
                outputManager.sendFeedbackMsg(statRequester, StandardMessage.STILL_RELOADING);
                reloadThread.join();

            } catch (InterruptedException e) {
                MyLogger.logException(e, "StatThread", "Trying to join " + reloadThread.getName());
                throw new RuntimeException(e);
            }
        }

        long lastCalc = ThreadManager.getLastRecordedCalcTime();
        if (lastCalc > 6000) {
            outputManager.sendFeedbackMsg(statRequester, StandardMessage.WAIT_A_MINUTE);
        } else if (lastCalc > 2000) {
            outputManager.sendFeedbackMsg(statRequester, StandardMessage.WAIT_A_MOMENT);
        }

        try {
            StatResult<?> result = StatRequestManager.execute(statRequest);
            outputManager.sendToCommandSender(statRequester, result.formattedComponent());
        }
        catch (ConcurrentModificationException e) {
            if (!statRequest.getSettings().isConsoleSender()) {
                outputManager.sendFeedbackMsg(statRequester, StandardMessage.UNKNOWN_ERROR);
            }
        }
    }
}