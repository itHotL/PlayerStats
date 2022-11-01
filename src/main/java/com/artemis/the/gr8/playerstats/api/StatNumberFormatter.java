package com.artemis.the.gr8.playerstats.api;

import com.artemis.the.gr8.playerstats.api.enums.Unit;

public interface StatNumberFormatter {

    String formatDamageNumber(long number, Unit statUnit);

    String formatDistanceNumber(long number, Unit statUnit);

    String formatTimeNumber(long number, Unit biggestTimeUnit, Unit smallestTimeUnit);
}
