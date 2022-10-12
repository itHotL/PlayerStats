package com.artemis.the.gr8.playerstats.statistic;

import com.artemis.the.gr8.playerstats.api.StatFormatter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.TextComponent;

/**
 * Holds the result of a completed stat-lookup. The <code>Type</code> parameter
 * <code>T</code> of this StatResult represents the data type of the stored number:
 * <ul>
 * <li> <code>Integer</code> for playerStat
 * <li> <code>Long</code> for serverStat
 * <li> <code>LinkedHashMap(String, Integer)</code> for topStat
 * </ul>
 * You can get these raw numbers with {@link #getNumericalValue()}. Additionally,
 * you can get a formatted message that contains the following information:
 * <ul>
 * <li> for playerStat:
 * <br> [player-name]: [formatted-number] [stat-name] [sub-stat-name]
 * <li> for serverStat:
 * <br> [Total on] [server-name]: [formatted-number] [stat-name] [sub-stat-name]
 * <li> for topStat:
 * <br> [PlayerStats] [Top x] [stat-name] [sub-stat-name]
 * <br> [1.] [player-name] [.....] [formatted-number]
 * <br> [2.] [player-name] [.....] [formatted-number]
 * <br> [3.] etc...
 * </ul>
 * <p>
 * By default, the resulting message is a {@link TextComponent}, which can be
 * sent directly to a Minecraft client or console with the Adventure library.
 * To send a Component, you need to get a {@link BukkitAudiences} object,
 * and use that to send the desired Component. Normally you would have to add
 * Adventure as a dependency to your project, but since the library is included
 * in PlayerStats, you can access it through the PlayerStatsImpl. Information
 * on how to get and use the BukkitAudiences object can be found on
 * <a href="https://docs.adventure.kyori.net/platform/bukkit.html">Adventure's website</a>.
 *
 * <p>You can also use the provided {@link #formattedString ()} method to get the
 * same information in String-format. Don't use Adventure's <code>#content()</code>
 * or <code>#toString()</code> methods on the Components - those won't get the actual
 * message. And finally, if you want the results to be formatted differently,
 * you can get an instance of the {@link StatFormatter}.
 */
public record StatResult<T>(T value, TextComponent formattedComponent, String formattedString) {

    /**
     * Gets the raw number for the completed stat-lookup this {@link StatResult} stores.
     *
     * @return {@code Integer} for playerStat, {@code Long} for serverStat, and {@code LinkedHashMap<String, Integer>}
     * for topStat
     */
    T getNumericalValue() {
        return value;
    }

    /**
     * Gets the formatted message for the completed stat-lookup this StatResult stores.
     *
     * @return a {@code TextComponent} message containing the formatted number. This message follows the same
     * style/color/language settings that are specified in the PlayerStats config. See class description for more
     * information.
     * @see StatResult
     */
    TextComponent getFormattedTextComponent() {
        return formattedComponent;
    }

    /**
     * Gets the formatted message for the completed stat-lookup this StatResult stores.
     *
     * @return a String message containing the formatted number. This message follows the same style and color settings
     * that are specified in the PlayerStats config, but it is not translatable (it is always plain English). See class
     * description for more information.
     * @see StatResult
     */
    @Override
    public String formattedString() {
        return formattedString;
    }
}