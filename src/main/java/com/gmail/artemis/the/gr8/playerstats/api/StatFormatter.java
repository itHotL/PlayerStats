package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import net.kyori.adventure.text.TextComponent;


import java.util.LinkedHashMap;

/** Interface that defines the output functionality PlayerStats should have.
 This is meant for an outgoing API - for internal use, more output functionality may exist. */
public interface StatFormatter {

    /** Returns the setting for whether TextComponents should be saved internally for later stat-sharing by players.
     Make this method return "false" if you only want to get a fancy stat-result, and don't want to send it
     to players in chat with a clickable "share"-button. */
    boolean saveOutputForSharing();

    String toString(TextComponent component);

    TextComponent formatPlayerStat(StatRequest request, int playerStat);

    TextComponent formatServerStat(StatRequest request, long serverStat);

    TextComponent formatTopStat(StatRequest request, LinkedHashMap<String, Integer> topStats);
}