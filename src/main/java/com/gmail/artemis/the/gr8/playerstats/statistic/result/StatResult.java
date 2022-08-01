package com.gmail.artemis.the.gr8.playerstats.statistic.result;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.TextComponent;

/** Holds the result of a completed stat-lookup. The <code>Type</code> parameter
 <code>T</code> of the StatResult represents the data type of the stored number:
 From the StatResult,
 you can get the numbers in a formatted message, or the raw numbers by themselves:
 <ul>
 <li> <code>Integer</code> for playerStat
 <li> <code>Long</code> for serverStat
 <li> <code>LinkedHashMap(String, Integer)</code> for topStat
 </ul>
 You can get the raw numbers with {@link #getNumericalValue()}. Additionally,
 you can get a formatted message that includes formatted numbers. This can either
 be a String or a {@link TextComponent}, and contains the following information:
 <ul>
 <li> for playerStat:
 <br> [player-name]: [formatted-number] [stat-name] [sub-stat-name]
 <li> for serverStat:
 <br> [Total on] [server-name]: [formatted-number] [stat-name] [sub-stat-name]
 <li> for topStat:
 <br> [PlayerStats] [Top x] [stat-name] [sub-stat-name]
 <br> [1.] [player-name] [.....] [formatted-number]
 <br> [2.] [player-name] [.....] [formatted-number]
 <br> [3.] etc...
 </ul>
 The TextComponents can be sent directly to a Minecraft client or console with the
 Adventure library. To send a Component, you need to get a {@link BukkitAudiences} object,
 and use that to send the desired Component. Normally you would have to add Adventure
 as a dependency to your project, but since the library is included in PlayerStats, you can
 access it directly. Information on how to get and use the BukkitAudiences object can be
 found on <a href="https://docs.adventure.kyori.net/platform/bukkit.html">Adventure's website</a>.
 */
public interface StatResult<T> {

    /** Gets the raw number for the completed stat-lookup this {@link StatResult} stores.

     @return {@code Integer} for playerStat, {@code Long} for serverStat,
     and {@code LinkedHashMap<String, Integer>} for topStat*/
    T getNumericalValue();

    /** Gets the formatted message for the completed stat-lookup this {@link StatResult} stores.

     @return a {@code TextComponent} message containing the formatted number.
     This message follows the same style/color/language settings that are specified in the
     PlayerStats config. See class description for more information. */
    TextComponent getFormattedTextComponent();

    /** Gets the formatted message for the completed stat-lookup this {@link StatResult} stores.

     @return a String message containing the formatted number. This message follows
     the same style and color settings that are specified in the PlayerStats config,
     but it is not translatable (it is always plain English). See class description
     for more information.*/
    String getFormattedString();
}