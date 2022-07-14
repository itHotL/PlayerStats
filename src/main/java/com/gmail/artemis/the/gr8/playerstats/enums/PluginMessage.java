package com.gmail.artemis.the.gr8.playerstats.enums;

public enum PluginMessage {
    RELOADED_CONFIG,
    STILL_RELOADING,
    WAIT_A_MOMENT,  //param: long
    MISSING_STAT_NAME,
    MISSING_SUB_STAT_NAME, //param: Statistic.Type
    MISSING_PLAYER_NAME,
    WRONG_SUB_STAT_TYPE, //param: Statistic.Type, String
    REQUEST_ALREADY_RUNNING,
    STILL_ON_SHARE_COOLDOWN,
    RESULTS_ALREADY_SHARED,
    STAT_RESULTS_TOO_OLD,
    UNKNOWN_ERROR,
    USAGE_EXAMPLES,
    HELP_MSG,
    FORMAT_PLAYER_STAT,
    FORMAT_SERVER_STAT,
    FORMAT_TOP_STAT
}
