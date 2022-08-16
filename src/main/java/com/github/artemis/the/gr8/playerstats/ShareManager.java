package com.github.artemis.the.gr8.playerstats;

import com.github.artemis.the.gr8.playerstats.statistic.result.InternalStatResult;
import com.github.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.github.artemis.the.gr8.playerstats.utils.MyLogger;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * The manager of all Player-prompted statistic-sharing.
 * If sharing is enabled, this class will save the results
 * of past stat-lookups, so the results can be retrieved
 * and shared when a Player clicks the share-button.
 */
public final class ShareManager {

    private static boolean isEnabled;
    private static int waitingTime;

    private static volatile AtomicInteger resultID;
    private static ConcurrentHashMap<Integer, InternalStatResult> statResultQueue;
    private static ConcurrentHashMap<String, Instant> shareTimeStamp;
    private static ArrayBlockingQueue<Integer> sharedResults;

    public ShareManager(ConfigHandler config) {
       updateSettings(config);
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    public static synchronized void updateSettings(ConfigHandler config) {
        isEnabled = config.allowStatSharing() && config.useHoverText();
        waitingTime = config.getStatShareWaitingTime();

        if (isEnabled) {
            sharedResults = new ArrayBlockingQueue<>(500);  //reset the sharedResultsQueue
            if (resultID == null) {  //if we went from disabled to enabled, initialize
                resultID = new AtomicInteger();  //always starts with value 0
                statResultQueue = new ConcurrentHashMap<>();
                shareTimeStamp = new ConcurrentHashMap<>();
            }
        } else {
            //if we went from enabled to disabled, purge the existing data
            if (statResultQueue != null) {
                statResultQueue = null;
                shareTimeStamp = null;
                sharedResults = null;
            }
            if (config.allowStatSharing() && !config.useHoverText()) {
                MyLogger.logWarning("Stat-sharing does not work without hover-text enabled! " +
                        "Enable hover-text, or disable stat-sharing to stop seeing this message.");
            }
        }
    }

    public boolean senderHasPermission(CommandSender sender) {
        return !(sender instanceof ConsoleCommandSender) && sender.hasPermission("playerstats.share");
    }

    public int saveStatResult(String playerName, TextComponent statResult) {
        removeExcessResults(playerName);

        int ID = getNextIDNumber();
        //UUID shareCode = UUID.randomUUID();
        InternalStatResult result = new InternalStatResult(playerName, statResult, ID);
        int shareCode = result.hashCode();
        statResultQueue.put(shareCode, result);
        MyLogger.logMediumLevelMsg("Saving statResults with no. " + ID);
        return shareCode;
    }

    public boolean isOnCoolDown(String playerName) {
        if (waitingTime == 0 || !shareTimeStamp.containsKey(playerName)) {
            return false;
        } else {
            long seconds = SECONDS.between(shareTimeStamp.get(playerName), Instant.now());
            return seconds <= (long) waitingTime * 60;
        }
    }

    public boolean requestAlreadyShared(int shareCode) {
        return sharedResults.contains(shareCode);
    }

    /**
     * Takes a formattedComponent from the internal ConcurrentHashmap,
     * puts the current time in the shareTimeStamp (ConcurrentHashMap),
     * puts the shareCode (int hashCode) in the sharedResults (ArrayBlockingQueue),
     * and returns the formattedComponent. If no formattedComponent was found,
     * returns null.
     */
    public @Nullable InternalStatResult getStatResult(String playerName, int shareCode) {
        if (statResultQueue.containsKey(shareCode)) {
            shareTimeStamp.put(playerName, Instant.now());

            if (!sharedResults.offer(shareCode)) {  //create a new ArrayBlockingQueue if our queue is full
                MyLogger.logMediumLevelMsg("500 stat-results have been shared, " +
                        "creating a new internal queue with the most recent 50 share-code-values and discarding the rest...");
                ArrayBlockingQueue<Integer> newQueue = new ArrayBlockingQueue<>(500);

                synchronized (this) {  //put the last 50 values in the new Queue
                    Integer[] lastValues = sharedResults.toArray(new Integer[500]);
                    Arrays.stream(Arrays.copyOfRange(lastValues, 450, 500))
                            .parallel().iterator()
                            .forEachRemaining(newQueue::offer);

                    sharedResults = newQueue;
                }
                sharedResults.offer(shareCode);
            }
            return statResultQueue.remove(shareCode);
        }
        else {
            return null;
        }
    }

    /**
     * If the given player already has more than x (in this case 25)
     * StatResults saved, remove the oldest one.
     */
    private void removeExcessResults(String playerName) {
        List<InternalStatResult> alreadySavedResults = statResultQueue.values()
                .parallelStream()
                .filter(result -> result.executorName().equalsIgnoreCase(playerName))
                .toList();

        if (alreadySavedResults.size() > 25) {
            int hashCode = alreadySavedResults
                    .parallelStream()
                    .min(Comparator.comparing(InternalStatResult::ID))
                    .orElseThrow().hashCode();
            MyLogger.logMediumLevelMsg("Removing old stat no. " + statResultQueue.get(hashCode).ID() + " for player " + playerName);
            statResultQueue.remove(hashCode);
        }
    }

    private int getNextIDNumber() {
        return resultID.incrementAndGet();
    }
}