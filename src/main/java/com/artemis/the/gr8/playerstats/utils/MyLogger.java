package com.artemis.the.gr8.playerstats.utils;

import com.artemis.the.gr8.playerstats.enums.DebugLevel;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * The PlayerStats Logger
 */
public final class MyLogger {

    private static final Logger logger;
    private static DebugLevel debugLevel;

    private static ConcurrentHashMap<String, Integer> threadNames;

    static {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlayerStats");
        logger = (plugin != null) ? plugin.getLogger() : Bukkit.getLogger();
        debugLevel = DebugLevel.LOW;
        threadNames = new ConcurrentHashMap<>();
    }

    private MyLogger() {
    }

    /**
     * Sets the desired debugging level.
     * <br>1 = low (only show unexpected errors)
     * <br>2 = medium (detail all encountered exceptions, log main tasks and show time taken)
     * <br>3 = high (log all tasks and time taken)
     * <br>Default: 1
     */
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

    public static void logLowLevelMsg(String content) {
        logger.info(content);
    }

    public static void logLowLevelTask(String taskName, long startTime) {
        printTime(taskName, startTime);
    }

    public static void logMediumLevelMsg(String content) {
        if (debugLevel != DebugLevel.LOW) {
            logger.info(content);
        }
    }

    public static void logMediumLevelTask(String taskName, long startTime) {
        if (debugLevel != DebugLevel.LOW) {
            printTime(taskName, startTime);
        }
    }

    public static void logHighLevelMsg(String content) {
        if (debugLevel == DebugLevel.HIGH) {
            logger.info(content);
        }
    }

    public static void logWarning(String content) {
        logger.warning(content);
    }

    /**
     * Log the encountered exception as a warning to console,
     * with some information about which class/method caught it
     * and with a printStackTrace if DebugLevel is HIGH.
     *
     * @param exception The encountered exception
     * @param caughtBy The name of the class that caught the exception
     * @param additionalInfo e.g. the method-name or line where the
     *                       exception is caught
     */
    public static void logException(@NotNull Exception exception, String caughtBy, @Nullable String additionalInfo) {
        String extraInfo = (additionalInfo != null) ? " [" + additionalInfo + "]" : "";
        String info =  " (" + caughtBy + extraInfo + ")";

        logger.warning(exception + info);
        if (debugLevel == DebugLevel.HIGH) {
            exception.printStackTrace();
        }
    }

    /**
     * If DebugLevel is MEDIUM or HIGH, output to console that an
     * action has started.
     *
     * @param taskLength Length of the action (in terms of
     *                   units-to-process)
     */
    public static void actionCreated(int taskLength) {
        if (debugLevel != DebugLevel.LOW) {
            threadNames = new ConcurrentHashMap<>();
            logger.info("Initial Action created for " + taskLength + " Players. Processing...");
        }
    }

    /**
     * Internally save the name of the executing thread for later
     * logging of this action. The list of names is reset upon the
     * start of every new action.
     *
     * @param threadName Name of the executing thread
     */
    public static void subActionCreated(String threadName) {
        if (debugLevel == DebugLevel.HIGH) {
            if (!threadNames.containsKey(threadName)) {
                threadNames.put(threadName, threadNames.size());
            }
        }
    }

    /**
     * Internally save the name of the executing thread for logging.
     *
     * @param threadName Name of the executing thread
     */
    public static void actionRunning(String threadName) {
        if (debugLevel != DebugLevel.LOW) {
            if (!threadNames.containsKey(threadName)) {
                threadNames.put(threadName, threadNames.size());
            }
        }
    }

    /**
     * Output to console that an action has finished if DebugLevel is
     * MEDIUM or higher. If DebugLevel is HIGH, also output the names
     * of the threads that were used.
     */
    public static void actionFinished() {
        if (debugLevel != DebugLevel.LOW) {
            logger.info("Finished Recursive Action! In total " +
                    threadNames.size() + " Threads were used");
        }
        if (debugLevel == DebugLevel.HIGH) {
            logger.info(Collections.list(threadNames.keys()).toString());
        }
    }

    /**
     * Output to console how long a certain task has taken.
     *
     * @param taskName name of the task that has been executed
     * @param startTime Timestamp marking the beginning of the task
     */
    private static void printTime(String taskName, long startTime) {
        logger.info(taskName + " (" + (System.currentTimeMillis() - startTime) + "ms)");
    }
}