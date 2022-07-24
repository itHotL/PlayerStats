package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import net.kyori.adventure.text.TextComponent;

import java.util.LinkedHashMap;

/** The {@link StatFormatter} defines what the output of any given statistic look-up should be.
 <br></br>
 <p>The Formatter takes a {@link StatRequest} and the result of {@link StatCalculator} calculations, and transforms the
 request object and raw numbers into a pretty message (TextComponent) with all the relevant information in it.
 This output is ready to be sent to a Player or Console, or can be turned into a String representation if necessary.*/
public interface StatFormatter {

    TextComponent formatPlayerStat(StatRequest request, int playerStat);

    TextComponent formatServerStat(StatRequest request, long serverStat);

    TextComponent formatTopStat(StatRequest request, LinkedHashMap<String, Integer> topStats);
}