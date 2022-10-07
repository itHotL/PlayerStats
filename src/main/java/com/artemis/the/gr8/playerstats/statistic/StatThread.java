package com.artemis.the.gr8.playerstats.statistic;

import com.artemis.the.gr8.playerstats.ThreadManager;
import com.artemis.the.gr8.playerstats.msg.OutputManager;
import com.artemis.the.gr8.playerstats.statistic.request.StatRequest;
import com.artemis.the.gr8.playerstats.utils.MyLogger;
import com.artemis.the.gr8.playerstats.enums.StandardMessage;
import com.artemis.the.gr8.playerstats.enums.Target;
import com.artemis.the.gr8.playerstats.reload.ReloadThread;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * The Thread that is in charge of getting and calculating statistics.
 */
public final class StatThread extends Thread {

    private static OutputManager outputManager;
    private static StatCalculator statCalculator;

    private final ReloadThread reloadThread;
    private final StatRequest<?> statRequest;

    public StatThread(OutputManager m, StatCalculator t, int ID, StatRequest<?> s, @Nullable ReloadThread r) {
        outputManager = m;
        statCalculator = t;

        reloadThread = r;
        statRequest = s;

        this.setName("StatThread-" + statRequest.getSettings().getCommandSender().getName() + "-" + ID);
        MyLogger.logHighLevelMsg(this.getName() + " created!");
    }

    @Override
    public void run() throws IllegalStateException, NullPointerException {
        MyLogger.logHighLevelMsg(this.getName() + " started!");

        if (statRequest == null) {
            throw new NullPointerException("No statistic requestSettings was found!");
        }
        if (reloadThread != null && reloadThread.isAlive()) {
            try {
                MyLogger.logLowLevelMsg(this.getName() + ": Waiting for " + reloadThread.getName() + " to finish up...");
                outputManager.sendFeedbackMsg(statRequest.getSettings().getCommandSender(), StandardMessage.STILL_RELOADING);
                reloadThread.join();

            } catch (InterruptedException e) {
                MyLogger.logException(e, "StatThread", "Trying to join " + reloadThread.getName());
                throw new RuntimeException(e);
            }
        }

        long lastCalc = ThreadManager.getLastRecordedCalcTime();
        if (lastCalc > 2000) {
            outputManager.sendFeedbackMsgWaitAMoment(statRequest.getSettings().getCommandSender(), lastCalc > 20000);
        }

        Target selection = statRequest.getSettings().getTarget();
        try {
            TextComponent statResult = switch (selection) {
                case PLAYER -> outputManager.formatAndSavePlayerStat(statRequest.getSettings(), statCalculator.getPlayerStat(statRequest.getSettings()));
                case TOP -> outputManager.formatAndSaveTopStat(statRequest.getSettings(), statCalculator.getTopStats(statRequest.getSettings()));
                case SERVER -> outputManager.formatAndSaveServerStat(statRequest.getSettings(), statCalculator.getServerStat(statRequest.getSettings()));
            };
            outputManager.sendToCommandSender(statRequest.getSettings().getCommandSender(), statResult);
        }
        catch (ConcurrentModificationException e) {
            if (!statRequest.getSettings().isConsoleSender()) {
                outputManager.sendFeedbackMsg(statRequest.getSettings().getCommandSender(), StandardMessage.UNKNOWN_ERROR);
            }
        }
    }
}