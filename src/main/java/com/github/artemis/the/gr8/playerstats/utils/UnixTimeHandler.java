package com.github.artemis.the.gr8.playerstats.utils;

/** A small utility class that calculates with unix time.*/
public final class UnixTimeHandler {

    private UnixTimeHandler() {
    }

    /** Calculates whether a player has played recently enough to fall within the lastPlayedLimit.
    If lastPlayedLimit == 0, this always returns true (since there is no limit).
     @param lastPlayed a long that represents the amount of milliseconds between the unix start point and the time this player last joined
     @param lastPlayedLimit a long that represents the maximum-number-of-days-since-last-joined */
    public static boolean hasPlayedSince(long lastPlayedLimit, long lastPlayed) {
        long maxLastPlayed = System.currentTimeMillis() - lastPlayedLimit * 24 * 60 * 60 * 1000;
        return lastPlayedLimit == 0 || lastPlayed >= maxLastPlayed;
    }
}