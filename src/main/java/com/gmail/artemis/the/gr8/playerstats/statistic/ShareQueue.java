package com.gmail.artemis.the.gr8.playerstats.statistic;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

public class ShareQueue {

    private final ConcurrentHashMap<String, TextComponent> statResults;
    private final ConcurrentHashMap<String, Instant> shareTimestamp;

    public ShareQueue() {
        statResults = new ConcurrentHashMap<>();
        shareTimestamp = new ConcurrentHashMap<>();
    }

    public void saveStatResults(String senderName, TextComponent statResult) {
        statResults.put(senderName, statResult);
    }

    public boolean senderCanShare(String senderName) {
        return senderCanShare(senderName, 0);
    }

    /** Returns true if the given sender has a statResult that can be shared,
     if they have not shared a statResult yet, or if they have passed the timeLimit.
     @param senderName name of the commandSender to evaluate
     @param timeLimit the waiting time in seconds during which sharing is not allowed*/
    public boolean senderCanShare(String senderName, long timeLimit) {
        if (timeLimit == 0 || !shareTimestamp.containsKey(senderName)) {
            return statResults.containsKey(senderName);
        } else {
            long seconds = shareTimestamp.get(senderName).until(Instant.now(), ChronoUnit.SECONDS);
            return seconds >= timeLimit;
        }
    }

    /** Removes and returns the last statResults for this sender,
     and stores the timestamp the results were retrieved on.*/
    public @Nullable TextComponent getLastStatResult(String senderName) {
        if (statResults.containsKey(senderName)) {
            shareTimestamp.put(senderName, Instant.now());
            return statResults.remove(senderName);
        } else {
            return null;
        }
    }
}
