package com.gmail.artemis.the.gr8.playerstats.statistic.result;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.TextComponent;

/** Holds the result of a completed stat-lookup. From the StatResult,
 you can get the raw numbers:
 <ul>
 <li> <code>int</code> for playerStat
 <li> <code>long</code> for serverStat
 <li> <code>LinkedHashMap(String, Integer)</code> for topStat
 </ul>
 Besides raw numbers, you can also get a formatted message. This can either
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
 If you get a TextComponent, you can send this directly to a Minecraft client or console
 with the Adventure library. To send a Component, you need to get a {@link BukkitAudiences}
 object, and use that to send the desired Component. Normally you would have to add Adventure
 as a dependency to your project, but since the library is included in PlayerStats, you can
 access it directly. Information on how to get and use the BukkitAudiences object can be found on
 <a href="https://docs.adventure.kyori.net/platform/bukkit.html">Adventure's website</a>.
 <br>
 */
public interface StatResult<T> {

    T getNumericalValue();

    TextComponent getFormattedTextComponent();

    String getFormattedString();
}