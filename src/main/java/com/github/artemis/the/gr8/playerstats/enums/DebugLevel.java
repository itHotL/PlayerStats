package com.github.artemis.the.gr8.playerstats.enums;

/**
 * Represents the debugging level that PlayerStats can use.
 * <br>
 * <br>1 = low (only show unexpected errors)
 * <br>2 = medium (detail all encountered exceptions, log main tasks and show time taken)
 * <br>3 = high (log all tasks and time taken)
 */
public enum DebugLevel {
    LOW, MEDIUM, HIGH
}