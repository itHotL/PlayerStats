package com.github.artemis.the.gr8.playerstats.msg;

import com.github.artemis.the.gr8.playerstats.statistic.request.RequestSettings;
import com.github.artemis.the.gr8.playerstats.statistic.StatCalculator;
import net.kyori.adventure.text.*;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.LinkedHashMap;

/** The {@link InternalFormatter} formats raw numbers into pretty messages.
 * This ApiFormatter takes a {@link RequestSettings} object and combines it with the raw data
 * returned by the {@link StatCalculator}, and transforms those into a pretty message
 * with all the relevant information in it.
 */
@Internal
public interface InternalFormatter {

    /** @return a TextComponent with the following parts:
     * <br>[player-name]: [number] [stat-name] {sub-stat-name}
     */
    TextComponent formatAndSavePlayerStat(RequestSettings requestSettings, int playerStat);

    /** @return a TextComponent with the following parts:
     * <br>[Total on] [server-name]: [number] [stat-name] [sub-stat-name]
     */
    TextComponent formatAndSaveServerStat(RequestSettings requestSettings, long serverStat);

    /** @return a TextComponent with the following parts:
     * <br>[PlayerStats] [Top 10] [stat-name] [sub-stat-name]
     * <br> [1.] [player-name] [number]
     * <br> [2.] [player-name] [number]
     * <br> [3.] etc...
     */
    TextComponent formatAndSaveTopStat(RequestSettings requestSettings, LinkedHashMap<String, Integer> topStats);
}