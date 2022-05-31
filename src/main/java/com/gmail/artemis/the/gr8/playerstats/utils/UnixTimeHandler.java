package com.gmail.artemis.the.gr8.playerstats.utils;


public class UnixTimeHandler {

    //calculates whether a player has played recently enough to fall within the lastPlayedLimit
    //if lastPlayedLimit == 0, this always returns true (since there is no limit)
    public static boolean hasPlayedSince(long lastPlayedLimit, long lastPlayed) {
        long maxLastPlayed = System.currentTimeMillis() - lastPlayedLimit * 24 * 60 * 60 * 1000;
        return lastPlayedLimit == 0 || lastPlayed >= maxLastPlayed;
    }
}
