package com.gmail.artemis.the.gr8.playerstats.enums;

/** All standard messages PlayerStats can send as feedback.
 These are all the messages that can be sent without needing additional parameters.*/
public enum StandardMessage {
    RELOADED_CONFIG,
    STILL_RELOADING,
    MISSING_STAT_NAME,
    MISSING_PLAYER_NAME,
    REQUEST_ALREADY_RUNNING,
    STILL_ON_SHARE_COOLDOWN,
    RESULTS_ALREADY_SHARED,
    STAT_RESULTS_TOO_OLD,
    UNKNOWN_ERROR,
}
