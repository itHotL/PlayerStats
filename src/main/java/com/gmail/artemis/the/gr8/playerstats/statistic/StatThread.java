package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.api.StatFormatter;
import com.gmail.artemis.the.gr8.playerstats.enums.StandardMessage;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.StatRequestCore;
import com.gmail.artemis.the.gr8.playerstats.msg.OutputManager;
import com.gmail.artemis.the.gr8.playerstats.reload.ReloadThread;
import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/** The Thread that is in charge of getting and calculating statistics.*/
public final class StatThread extends Thread {

    private static OutputManager outputManager;
    private static StatManager statManager;

    private final ReloadThread reloadThread;
    private final StatRequestCore statRequestCore;

    public StatThread(OutputManager m, StatManager t, int ID, StatRequestCore s, @Nullable ReloadThread r) {
        outputManager = m;
        statManager = t;

        reloadThread = r;
        statRequestCore = s;

        this.setName("StatThread-" + statRequestCore.getCommandSender().getName() + "-" + ID);
        MyLogger.threadCreated(this.getName());
    }

    @Override
    public void run() throws IllegalStateException, NullPointerException {
        MyLogger.threadStart(this.getName());

        if (statRequestCore == null) {
            throw new NullPointerException("No statistic statRequest was found!");
        }
        if (reloadThread != null && reloadThread.isAlive()) {
            try {
                MyLogger.waitingForOtherThread(this.getName(), reloadThread.getName());
                outputManager.sendFeedbackMsg(statRequestCore.getCommandSender(), StandardMessage.STILL_RELOADING);
                reloadThread.join();

            } catch (InterruptedException e) {
                MyLogger.logException(e, "StatThread", "Trying to join " + reloadThread.getName());
                throw new RuntimeException(e);
            }
        }

        long lastCalc = ThreadManager.getLastRecordedCalcTime();
        if (lastCalc > 2000) {
            outputManager.sendFeedbackMsgWaitAMoment(statRequestCore.getCommandSender(), lastCalc > 20000);
        }

        Target selection = statRequestCore.getTarget();
        try {
            TextComponent statResult = switch (selection) {
                case PLAYER -> outputManager.formatPlayerStat(statRequestCore, statManager.getPlayerStat(statRequestCore));
                case TOP -> outputManager.formatTopStat(statRequestCore, statManager.getTopStats(statRequestCore));
                case SERVER -> outputManager.formatServerStat(statRequestCore, statManager.getServerStat(statRequestCore));
            };
            if (statRequestCore.isAPIRequest()) {
                String msg = StatFormatter.statResultComponentToString(statResult);
                statRequestCore.getCommandSender().sendMessage(msg);
            }
            else {
                outputManager.sendToCommandSender(statRequestCore.getCommandSender(), statResult);
            }
        }
        catch (ConcurrentModificationException e) {
            if (!statRequestCore.isConsoleSender()) {
                outputManager.sendFeedbackMsg(statRequestCore.getCommandSender(), StandardMessage.UNKNOWN_ERROR);
            }
        }
    }
}