package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.models.StatResult;
import net.kyori.adventure.text.TextComponent;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ShareManager {

    private static volatile ShareManager instance;

    private boolean isEnabled;
    private int waitingTime;

    private volatile AtomicInteger resultID;  //always starts with value 0
    private ConcurrentHashMap<UUID, StatResult> statResults = null;
    private ConcurrentHashMap<String, Instant> shareTimeStamp = null;

    private ShareManager(ConfigHandler config) {
        isEnabled = config.enableStatSharing();
        waitingTime = config.getStatShareWaitingTime();

        if (isEnabled) {
            resultID = new AtomicInteger();
            statResults = new ConcurrentHashMap<>();
            shareTimeStamp = new ConcurrentHashMap<>();
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

        //if we went from disabled to enabled, initialize the HashMaps
        if (isEnabled && statResults == null) {
            statResults = new ConcurrentHashMap<>();
            shareTimeStamp = new ConcurrentHashMap<>();
        }
        //if we went from enabled to disabled, purge the existing data
        else if (!isEnabled && statResults != null) {
            statResults = null;
            shareTimeStamp = null;
        }
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public UUID saveStatResult(String playerName, TextComponent statResult) {
        removeExcessResults(playerName);

        int ID = getNextIDNumber();
        UUID identifier = UUID.randomUUID();

        statResults.put(identifier, new StatResult(playerName, statResult, ID, identifier));
        return identifier;
    }

    public @Nullable TextComponent getStatResult(String playerName, UUID identifier) {
        if (statResults.containsKey(identifier) && playerCanShare(playerName)) {
            shareTimeStamp.put(playerName, Instant.now());
            return statResults.remove(identifier).statResult();
        } else {
            return null;
        }
    }

    private boolean playerCanShare(String playerName) {
        if (waitingTime == 0 || !shareTimeStamp.containsKey(playerName)) {
            return true;
        } else {
            long seconds = shareTimeStamp.get(playerName).until(Instant.now(), ChronoUnit.SECONDS);
            return seconds >= waitingTime;
        }
    }

    /** If the given player already has more than x (in this case 25) StatResults saved,
      remove the oldest one.*/
    private void removeExcessResults(String playerName) {
        List<StatResult> alreadySavedResults = statResults.values()
                .parallelStream()
                .filter(result -> result.playerName().equalsIgnoreCase(playerName))
                .toList();

        if (alreadySavedResults.size() > 25) {
            UUID uuid = alreadySavedResults
                    .parallelStream()
                    .min(Comparator.comparing(StatResult::ID))
                    .orElseThrow().uuid();
            statResults.remove(uuid);
        }
    }

    private int getNextIDNumber() {
        return resultID.incrementAndGet();
    }
}