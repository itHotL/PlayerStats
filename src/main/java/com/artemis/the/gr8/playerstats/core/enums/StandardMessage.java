package com.artemis.the.gr8.playerstats.core.enums;

/**
 * All standard messages PlayerStats can send as feedback.
 * These are all the messages that can be sent without needing
 * additional parameters.
 */
public enum StandardMessage {
    RELOADED_CONFIG,
    STILL_RELOADING,
    EXCLUDE_FAILED,
    INCLUDE_FAILED,
    MISSING_STAT_NAME,
    MISSING_PLAYER_NAME,
    PLAYER_IS_EXCLUDED,
    WAIT_A_MOMENT,
    WAIT_A_MINUTE,
    REQUEST_ALREADY_RUNNING,
    STILL_ON_SHARE_COOLDOWN,
    RESULTS_ALREADY_SHARED,
    STAT_RESULTS_TOO_OLD,
    UNKNOWN_ERROR
}