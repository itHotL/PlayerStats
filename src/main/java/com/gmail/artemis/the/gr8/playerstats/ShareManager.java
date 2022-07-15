package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.enums.DebugLevel;
import com.gmail.artemis.the.gr8.playerstats.models.StatResult;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import net.kyori.adventure.text.TextComponent;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static java.time.temporal.ChronoUnit.SECONDS;

public final class ShareManager {

    private static volatile ShareManager instance;

    private static boolean isEnabled;
    private static int waitingTime;

    private volatile AtomicInteger resultID;
    private ConcurrentHashMap<UUID, StatResult> statResultQueue;
    private ConcurrentHashMap<String, Instant> shareTimeStamp;
    private ArrayBlockingQueue<UUID> sharedResults;

    private ShareManager(ConfigHandler config) {
        isEnabled = config.enableStatSharing();
        waitingTime = config.getStatShareWaitingTime();

        if (isEnabled) {
            resultID = new AtomicInteger();  //always starts with value 0
            statResultQueue = new ConcurrentHashMap<>();
            shareTimeStamp = new ConcurrentHashMap<>();
            sharedResults = new ArrayBlockingQueue<>(500);
        }
    }

    public static ShareManager getInstance(ConfigHandler config) {
        ShareManager shareManager = instance;
        if (shareManager != null) {
            return shareManager;
        }
        synchronized (ShareManager.class) {
            if (instance == null) {
                instance = new ShareManager(config);
            }
            return instance;
        }
    }

    public synchronized void updateSettings(ConfigHandler config) {
        isEnabled = config.enableStatSharing();
        waitingTime = config.getStatShareWaitingTime();


        if (isEnabled) { //reset the sharedResultsQueue
            sharedResults = new ArrayBlockingQueue<>(500);
            if (statResultQueue == null) {  //if we went from disabled to enabled, initialize the HashMaps
                statResultQueue = new ConcurrentHashMap<>();
                shareTimeStamp = new ConcurrentHashMap<>();
            }
        }
        //if we went from enabled to disabled, purge the existing data
        else if (statResultQueue != null) {
            statResultQueue = null;
            shareTimeStamp = null;
            sharedResults = null;
        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public UUID saveStatResult(String playerName, TextComponent statResult) {
        removeExcessResults(playerName);

        int ID = getNextIDNumber();
        UUID shareCode = UUID.randomUUID();

        statResultQueue.put(shareCode, new StatResult(playerName, statResult, ID, shareCode));
        MyLogger.logMsg("Saving statResults with no. " + ID, DebugLevel.MEDIUM);
        return shareCode;
    }

    /** Takes a statResult from the internal ConcurrentHashmap,
     puts the current time in the shareTimeStamp (ConcurrentHashMap),
     puts the shareCode (UUID) in the sharedResults (ArrayBlockingQueue),
     and returns the statResult. If no statResult was found, returns null.*/
    public @Nullable TextComponent getStatResult(String playerName, UUID shareCode) {
        if (statResultQueue.containsKey(shareCode)) {
            shareTimeStamp.put(playerName, Instant.now());

            if (!sharedResults.offer(shareCode)) {  //create a new ArrayBlockingQueue if our queue is full
                MyLogger.logMsg("500 stat-results have been shared, " +
                        "creating a new internal queue with the most recent 50 share-code-values and discarding the rest...", DebugLevel.MEDIUM);
                ArrayBlockingQueue<UUID> newQueue = new ArrayBlockingQueue<>(500);

                synchronized (this) {  //put the last 50 values in the new Queue
                    UUID[] lastValues = sharedResults.toArray(new UUID[0]);
                    Arrays.stream(Arrays.copyOfRange(lastValues, 450, 500))
                            .parallel().iterator()
                            .forEachRemaining(newQueue::offer);

                    sharedResults = newQueue;
                }
                sharedResults.offer(shareCode);
            }
            StatResult result = statResultQueue.remove(shareCode);
            MyLogger.logMsg("StatResult record exists: " + (result != null));
            MyLogger.logMsg("Its TextComponent: " + result.statResult());
            MyLogger.logMsg("Its ID: " + result.ID());
            MyLogger.logMsg("Its uuid: " + result.uuid());
            return result.statResult();
        } else {
            return null;
        }
    }

    public boolean isOnCoolDown(String playerName) {
        if (waitingTime == 0 || !shareTimeStamp.containsKey(playerName)) {
            return false;
        } else {
            long seconds = SECONDS.between(shareTimeStamp.get(playerName), Instant.now());
            return seconds <= (long) waitingTime * 60;
        }
    }

    public boolean requestAlreadyShared(UUID shareCode) {
        return sharedResults.contains(shareCode);
    }

    /** If the given player already has more than x (in this case 25) StatResults saved,
      remove the oldest one.*/
    private void removeExcessResults(String playerName) {
        List<StatResult> alreadySavedResults = statResultQueue.values()
                .parallelStream()
                .filter(result -> result.playerName().equalsIgnoreCase(playerName))
                .toList();

        if (alreadySavedResults.size() > 25) {
            UUID uuid = alreadySavedResults
                    .parallelStream()
                    .min(Comparator.comparing(StatResult::ID))
                    .orElseThrow().uuid();
            MyLogger.logMsg("Removing old stat no. " + statResultQueue.get(uuid).ID() + " for player " + playerName, DebugLevel.MEDIUM);
            statResultQueue.remove(uuid);
        }
    }

    private int getNextIDNumber() {
        return resultID.incrementAndGet();
    }
}