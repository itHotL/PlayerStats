package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.statistic.request.RequestSettings;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatCalculator;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.*;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.LinkedHashMap;

/** The {@link StatFormatter} formats raw numbers into pretty messages.
 This Formatter takes a {@link RequestSettings} object and combines it with the raw number(s)
 returned by the {@link StatCalculator}, and transforms those into a pretty message
 (by default a TextComponent) with all the relevant information in it.
 <br>
 <br>The output is ready to be sent to a Minecraft client or console with the Adventure library.
 To send a Component, you need to get a {@link BukkitAudiences} object. Normally you would
 have to add the library as a dependency, but since the library is included in PlayerStats, you can
 access it directly. Information on how to get and use the BukkitAudiences object can be found on
 <a href="https://docs.adventure.kyori.net/platform/bukkit.html">Adventure's website</a>.
 <br>
 <br>Alternatively, you can also turn your TextComponent into a plain String with
 {@link #TextComponentToString(TextComponent)}. Don't use Adventure's method .content()
 on your formattedValue to do this - because of the way the TextComponent is built by PlayerStats,
 you won't be able to get the full content that way.*/
@Internal
public
interface StatFormatter extends Formatter {

    /** @return a TextComponent with the following parts:
    <br>[player-name]: [number] [stat-name] {sub-stat-name}*/
    TextComponent formatPlayerStat(RequestSettings requestSettings, int playerStat);

    /** @return a TextComponent with the following parts:
    <br>[Total on] [server-name]: [number] [stat-name] [sub-stat-name]*/
    TextComponent formatServerStat(RequestSettings requestSettings, long serverStat);

    /** @return a TextComponent with the following parts:
    <br>[PlayerStats] [Top 10] [stat-name] [sub-stat-name]
    <br> [1.] [player-name] [number]
    <br> [2.] [player-name] [number]
    <br> [3.] etc...*/
    TextComponent formatTopStat(RequestSettings requestSettings, LinkedHashMap<String, Integer> topStats);
}