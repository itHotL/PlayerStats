package com.gmail.artemis.the.gr8.playerstats.utils;

import com.gmail.artemis.the.gr8.playerstats.enums.DebugLevel;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class MyLogger {

    private static final Logger logger;
    private static DebugLevel debugLevel;

    private static final String[] processedPlayers;
    private static final AtomicInteger playersIndex;
    private static ConcurrentHashMap<String, Integer> threadNames;

    static{
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlayerStats");
        logger = (plugin != null) ? plugin.getLogger() : Bukkit.getLogger();
        debugLevel = DebugLevel.LOW;

        processedPlayers = new String[10];
        playersIndex = new AtomicInteger(0);
        threadNames = new ConcurrentHashMap<>();
    }

    private MyLogger() {
    }


    /** Accesses the playersIndex to up it by 1 and return its previous value. */
    private static int nextPlayersIndex() {
        return playersIndex.getAndIncrement();
    }

    /** Returns true if the playersIndex is 10, or any subsequent increment of 10. */
    private static boolean incrementOfTen() {
        return (playersIndex.get() == 10 || (playersIndex.get() > 10 && playersIndex.get() % 10 == 0));
    }

    /** Sets the desired debugging level.
     <p>1 = low (only show unexpected errors)</p>
     <p>2 = medium (detail all encountered exceptions, log main tasks and show time taken)</p>
     <p>3 = high (log all tasks and time taken)</p>
     <p>Default: 1</p>*/
    public static void setDebugLevel(int level) {
        if (level == 2) {
            debugLevel = DebugLevel.MEDIUM;
        }
        else if (level == 3) {
            debugLevel = DebugLevel.HIGH;
        }
        else {
            debugLevel = DebugLevel.LOW;
        }
    }

    /** Log the encountered exception to console, including a printStackTrace if DebugLevel is MEDIUM or HIGH.
     @param exception The encountered exception
     @param caughtBy The name of the class/method that caught the exception
     @param lineNumber The line where the exception is caught */
    public static void logException(@NotNull Exception exception, String caughtBy, int lineNumber) {
        String line = (lineNumber == 0) ? "" : " [" + lineNumber + "]";
        String caught = (debugLevel != DebugLevel.LOW) ? " (" + caughtBy + line + ")" : "";

        logger.info(exception + caught);
        if (debugLevel == DebugLevel.HIGH) {
            exception.printStackTrace();
        }
    }

    /** If DebugLevel is MEDIUM or HIGH, logs when the while loop in MessageFactory, getLanguageKey is being run. */
    public static void replacingUnderscores() {
        if (debugLevel != DebugLevel.LOW) {
            logger.info("Replacing underscores and capitalizing names...");
        }
    }

    /** Output to console that the given thread has been created (but not started yet).*/
    public static void threadCreated(String threadName) {
        if (debugLevel != DebugLevel.LOW) {
            logger.info(threadName + " created!");
        }
    }

    /** Output to console that the given thread has been started. */
    public static void threadStart(String threadName) {
        if (debugLevel == DebugLevel.MEDIUM || debugLevel == DebugLevel.HIGH) {
            logger.info(threadName + " started!");
        }
    }

    /** Output to console that another reloadThread is already running. */
    public static void threadAlreadyRunning(String threadName) {
        logger.info("Another reloadThread is already running! (" + threadName + ")");
    }

    /** Output to console that the executingThread is waiting for otherThread to finish up. */
    public static void waitingForOtherThread(String executingThread, String otherThread) {
        logger.info(executingThread + ": Waiting for " + otherThread + " to finish up...");
    }

    /** If DebugLevel is MEDIUM or HIGH, output to console that an action has started.
     @param taskLength Length of the action (in terms of units-to-process)*/
    public static void actionCreated(int taskLength) {
        if (debugLevel != DebugLevel.LOW) {
            threadNames = new ConcurrentHashMap<>();
            playersIndex.set(0);
            logger.info("Initial Action created for " + taskLength + " Players. Start Index is " + playersIndex.get() + ". Processing...");
        }
    }

    /** Internally save the name of the executing thread for later logging of this action.
    The list of names is reset upon the start of every new action.
     @param threadName Name of the executing thread*/
    public static void subActionCreated(String threadName) {
        if (debugLevel == DebugLevel.HIGH) {
            if (!threadNames.containsKey(threadName)) {
                threadNames.put(threadName, threadNames.size());
            }
        }
    }

    /** Internally save the name of the executing thread and processed player for logging,
     and for the ReloadThread, if DebugLevel is HIGH, output the last 10 processed players once
     there have been 10 names saved in MyLogger. This method is synchronized.
     @param threadName Name of the executing thread
     @param playerName Name of the player that was processed in this action
     @param thread 1 for ReloadThread, 2 for StatThread */
    public static synchronized void actionRunning(String threadName, String playerName, int thread) {
        if (debugLevel != DebugLevel.LOW) {
            if (!threadNames.containsKey(threadName)) {
                threadNames.put(threadName, threadNames.size());
            }
            if (thread == 1 && debugLevel == DebugLevel.HIGH) {
                if (incrementOfTen()) {
                    logger.info(Arrays.asList(processedPlayers).toString());
                }
                processedPlayers[nextPlayersIndex() % 10] = playerName;
            }
            else if (debugLevel == DebugLevel.MEDIUM) {
                nextPlayersIndex();
            }
        }
    }

    /** Output to console that an action has finished.
     <p>For the ReloadThread, if DebugLevel is HIGH, output the left-over processed players.
     For both threads, if DebugLevel is MEDIUM or HIGH, output the names of the threads that were used.</p>
     @param thread 1 for ReloadThread, 2 for StatThread */
    public static void actionFinished(int thread) {
        if (thread == 1 && debugLevel == DebugLevel.HIGH) {
            ArrayList<String> leftOvers = new ArrayList<>(Arrays.asList(processedPlayers).subList(playersIndex.intValue() % 10, 10));
            logger.info(leftOvers.toString());
        }
        if (debugLevel != DebugLevel.LOW) {
            logger.info("Finished Recursive Action! In total " +
                    threadNames.size() + " Threads were used to process " +
                    playersIndex.get() + " Players: " +
                    Collections.list(threadNames.keys()));
        }
    }

    /** Output to console how long a certain task has taken if DebugLevel is MEDIUM or HIGH.
     @param className Name of the class executing the task
     @param methodName Name or description of the task
     @param startTime Timestamp marking the beginning of the task */
    public static void logTimeTaken(String className, String methodName, long startTime) {
        if (debugLevel != DebugLevel.LOW) {
            logger.info(className + " " + methodName + ": " + (System.currentTimeMillis() - startTime) + "ms");
        }
    }

    /** Output to console how long a certain task has taken (regardless of DebugLevel).
     @param className Name of the class executing the task
     @param methodName Name or description of the task
     @param startTime Timestamp marking the beginning of the task */
    public static void logTimeTakenDefault(String className, String methodName, long startTime) {
        logger.info(className + " " + methodName + ":" + (System.currentTimeMillis() - startTime) + "ms");
    }
}
