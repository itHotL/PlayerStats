package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.enums.StandardMessage;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.msg.OutputManager;
import com.gmail.artemis.the.gr8.playerstats.reload.ReloadThread;
import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/** The Thread that is in charge of getting and calculating statistics.*/
public class StatThread extends Thread {

    private static OutputManager outputManager;
    private static StatManager statManager;

    private final ReloadThread reloadThread;
    private final StatRequest request;

    public StatThread(OutputManager m, StatManager t, int ID, StatRequest s, @Nullable ReloadThread r) {
        outputManager = m;
        statManager = t;

        reloadThread = r;
        request = s;

        this.setName("StatThread-" + request.getCommandSender().getName() + "-" + ID);
        MyLogger.threadCreated(this.getName());
    }

    @Override
    public void run() throws IllegalStateException, NullPointerException {
        MyLogger.threadStart(this.getName());

        if (request == null) {
            throw new NullPointerException("No statistic request was found!");
        }
        if (reloadThread != null && reloadThread.isAlive()) {
            try {
                MyLogger.waitingForOtherThread(this.getName(), reloadThread.getName());
                outputManager.sendFeedbackMsg(request.getCommandSender(), StandardMessage.STILL_RELOADING);
                reloadThread.join();

            } catch (InterruptedException e) {
                MyLogger.logException(e, "StatThread", "Trying to join " + reloadThread.getName());
                throw new RuntimeException(e);
            }
        }

        long lastCalc = ThreadManager.getLastRecordedCalcTime();
        if (lastCalc > 2000) {
            outputManager.sendFeedbackMsgWaitAMoment(request.getCommandSender(), lastCalc > 20000);
        }

        Target selection = request.getSelection();
        try {
            TextComponent statResult = switch (selection) {
                case PLAYER -> outputManager.formatPlayerStat(request, statManager.getPlayerStat(request));
                case TOP -> outputManager.formatTopStat(request, statManager.getTopStats(request));
                case SERVER -> outputManager.formatServerStat(request, statManager.getServerStat(request));
            };
            if (request.isAPIRequest()) {
                String msg = LegacyComponentSerializer.builder().hexColors().build().serialize(statResult);
                request.getCommandSender().sendMessage(msg);

                String msg2 = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build().serialize(statResult);
                request.getCommandSender().sendMessage(msg2);
            }
            else {
                outputManager.sendToCommandSender(request.getCommandSender(), statResult);
            }
        }
        catch (ConcurrentModificationException e) {
            if (!request.isConsoleSender()) {
                outputManager.sendFeedbackMsg(request.getCommandSender(), StandardMessage.UNKNOWN_ERROR);
            }
        }
    }
}