package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.LinkedHashMap;

/** Interface that defines the output functionality PlayerStats should have.
 This is meant for an outgoing API - for internal use, more output functionality may exist. */
public interface StatFormatter {

    default String statResultToString(TextComponent statResult) {
        return MiniMessage.miniMessage().serialize(statResult);
    }

    TextComponent formatPlayerStat(StatRequest request, int playerStat, boolean isAPIRequest);

    TextComponent formatServerStat(StatRequest request, long serverStat, boolean isAPIRequest);

    TextComponent formatTopStat(StatRequest request, LinkedHashMap<String, Integer> topStats, boolean isAPIRequest);
}