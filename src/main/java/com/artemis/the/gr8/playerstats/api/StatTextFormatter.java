package com.artemis.the.gr8.playerstats.api;

import com.artemis.the.gr8.playerstats.api.enums.Unit;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Statistic;
import org.jetbrains.annotations.Nullable;

/**
 * Formats messages meant for usage outside PlayerStats.
 * <p> For more information about the default formatting
 * PlayerStats uses, see the class description of
 * StatResult.

 * @see StatResult
 */
public interface StatTextFormatter {

    /**
     * Turns a TextComponent into its String representation. This method is equipped
     * to turn all PlayerStats' formatted statResults into String, using a custom
     * Serializer.
     *
     * @param component the Component to turn into String
     * @return a String representation of this TextComponent, without hover/click events,
     * but with color, style and formatting. TranslatableComponents will be turned into
     * plain English.
     */
    String textComponentToString(TextComponent component);

    /**
     * Gets the default prefix PlayerStats uses.
     * @return [PlayerStats]
     */
    TextComponent getPluginPrefix();

    /**
     * Gets the special rainbow version of PlayerStats' prefix.
     * @return [PlayerStats] in rainbow color
     * s*/
    TextComponent getRainbowPluginPrefix();

    /**
     * Gets the version of the prefix that is surrounded by underscores.
     * This is meant to be used as a title above a message or statistic display.
     * @return ________ [PlayerStats] ________
     */
    TextComponent getPluginPrefixAsTitle();

    /**
     * Gets the special rainbow version of the title-prefix.
     *
     * @return ________ [PlayerStats] ________ in rainbow colors
     */
    TextComponent getRainbowPluginPrefixAsTitle();

    /**
     * Gets a formatted message that displays the name of this Statistic as it is
     * displayed by PlayerStats. If this Statistic is not of Type.Untyped,
     * include the name of the relevant sub-statistic (block, item or entity).
     *
     * @param statistic the Statistic enum constant to display the name of
     * @param subStatName where necessary, the name of the Material or EntityType
     * to include, acquired by doing #toString() on the Material/EntityType in question
     * @return [stat-name] [sub-stat-name]
     */
    TextComponent getStatTitle(Statistic statistic, @Nullable String subStatName);

    /**
     * Gets a formatted message that displays the name of this Statistic as it is
     * displayed by PlayerStats. If this Statistic is not of Type.Untyped,
     * include the name of the relevant sub-statistic (block, item or entity).
     *
     * @param statistic the Statistic enum constant to display the name of
     * @param unit the Unit to display
     * @return [stat-name] [unit]
     */
    TextComponent getStatTitle(Statistic statistic, Unit unit);
    /**
     * Gets a formatted message that displays the name of this Statistic as it is
     * displayed by PlayerStats in a top-stat-message. If this Statistic is not
     * of Type.Untyped, include the name of the relevant sub-statistic
     * (block, item or entity).
     *
     * @param statistic the Statistic enum constant for this message
     * @param subStatName the name of the Material or EntityType to include,
     * acquired by doing #toString() on the Material/EntityType in question
     * @param topStatSize the size of the top-list this title is for
     * @return Top [topStatSize] [stat-name] [sub-stat-name]
     */
    TextComponent getTopStatTitle(int topStatSize, Statistic statistic, @Nullable String subStatName);

    /**
     * Gets a formatted message that displays the name of this Statistic as it is
     * displayed by PlayerStats in a top-stat-message, with the specified
     * Unit in the name.
     *
     * @param topStatSize the size of the top-list this title is for
     * @param statistic the Statistic enum constant for this message
     * @param unit the Unit to display
     * @return Top [topStatSize] [stat-name] [unit-name]
     */
    TextComponent getTopStatTitle(int topStatSize, Statistic statistic, Unit unit);

    /**
     * Formats the input into a single top-statistic line. The stat-number
     * is formatted into the most suitable {@link Unit} based on the provided
     * Statistic. For Type.Time, the resulting formatted number will have as
     * many additional smaller units as are specified in the config,
     * unless <code>formatTopStatLineForTypeTime()</code> is used.
     *
     * @param positionInTopList the rank-number in this list of the Player
     * @param playerName the name of the Player on this line
     * @param statNumber the result of Player#getStatistic()
     * @param statistic the Statistic enum constant for this message
     * @return a single line from a top-x statistic:
      * <br> [positionInTopList]. [player-name] ......... [stat-number]
      */
    TextComponent formatTopStatLine(int positionInTopList, String playerName, long statNumber, Statistic statistic);

    /**
     * Formats the input into a single top-statistic line. The stat-number
     * is formatted into the provided {@link Unit}. For Type.Time, the
     * resulting formatted number will have as many additional smaller
     * units as are specified in the config, unless
     * <code>formatTopStatLineForTypeTime()</code> is used.
     *
     * @param positionInTopList the rank-number in this list of the Player
     * @param playerName the name of the Player on this line
     * @param statNumber the result of Player#getStatistic()
     * @param unit the Unit to format the <code>statNumber</code> with
     * @return a single line from a top-x statistic:
      * <br> [positionInTopList]. [player-name] ......... [stat-number]
      * */
    TextComponent formatTopStatLine(int positionInTopList, String playerName, long statNumber, Unit unit);

    /**
     * Formats the input into a single top-statistic line for a time-based
     * statistic with the Unit-range that is between <code>bigUnit</code>
     * and <code>smallUnit</code> (both inclusive).
     *
     * @param positionInTopList the rank-number in this list of the Player
     * @param playerName the name of the Player on this line
     * @param statNumber the result of Player#getStatistic()
     * @param bigUnit the biggest Unit to use of {@link Unit.Type#TIME}
     * @param smallUnit the smallest Unit to use of {@link Unit.Type#TIME}
     * @return a single line from a stop-x statistic:
     * <br>[positionInTopList]. [player-name] ......... [1D 2H 3M 4S]
     */
    TextComponent formatTopStatLineForTypeTime(int positionInTopList, String playerName, long statNumber, Unit bigUnit, Unit smallUnit);

    /**
     * Formats the input into a server statistic message. The stat-number
     * is formatted into the most suitable {@link Unit} based on the provided
     * Statistic. For Type.Time, the resulting formatted number will have as
     * many additional smaller units as are specified in the config,
     * unless <code>formatServerStatForTypeTime()</code> is used.
     *
     * @param statNumber the result of all Player#getStatistic() values combined
     * @param statistic te Statistic enum constant for this message
     * @return [Total on this server]: [stat-number] [stat-name]
     */
    TextComponent formatServerStat(long statNumber, Statistic statistic);

    /**
     * Formats the input into a server statistic message for a statistic
     * that has a sub-statistic (block, item or entity).
     *
     * @param statistic the Statistic enum constant for this message
     * @param statNumber the result of all Player#getStatistic() values combined
     * @param subStatName the name of the Material or EntityType of this
     * statistic-lookup, acquired by doing #toString() on the Material/
     * EntityType in question
     * @return [Total on this server]: [stat-number] [stat-name] [sub-stat-name]
     */
    TextComponent formatServerStat(long statNumber, Statistic statistic, String subStatName);

    /**
     * Formats the input into a server statistic message with the specified
     * {@link Unit}. The stat-number is formatted into the most suitable
     * {@link Unit} based on the provided Statistic. For Type.Time, the
     * resulting formatted number will have as many additional smaller
     * units as are specified in the config, unless
     * <code>formatServerStatForTypeTime()</code> is used.
     *
     * @param statistic the Statistic enum constant for this message
     * @param statNumber the result of all Player#getStatistic() values combined
     * @param unit the Unit to use to format te <code>statNumber</code>
     * @return [Total on this server]: [stat-number] [stat-name] [unit-name]
     */
    TextComponent formatServerStat(long statNumber, Statistic statistic, Unit unit);

    /**
     * Formats the input into a server statistic message for a time-based
     * statistic with the Unit-range that is between <code>bigUnit</code>
     * and <code>smallUnit</code> (both inclusive).
     *
     * @param statistic the Statistic enum constant for this message
     * @param statNumber the result of all Player#getStatistic() values combined
     * @param bigUnit the biggest Unit to use of {@link Unit.Type#TIME}
     * @param smallUnit the smallest Unit to use of {@link Unit.Type#TIME}
     * @return [Total on this server]: [1D 2H 3M 4S] [stat-name]
     */
    TextComponent formatServerStatForTypeTime(long statNumber, Statistic statistic, Unit bigUnit, Unit smallUnit);

    /**
     * Formats the input into a player statistic message. For Unit.Type.Time,
     * the resulting formatted number will have as many additional smaller
     * units as are specified in the config, unless
     * <code>formatPlayerStatForTypeTime</code> is used.
     *
     * @param playerName the name of the Player
     * @param statistic the Statistic enum constant for this message
     * @param statNumber the result of Player#getStatistic()
     * @return [player-name]: [stat-number] [stat-name]
     */
    TextComponent formatPlayerStat(String playerName, int statNumber, Statistic statistic);

    /**
     * Formats the input into a player statistic message for a statistic
     * that has a sub-statistic (block, item or entity).
     *
     * @param playerName the name of the Player
     * @param statistic the Statistic enum constant for this message
     * @param statNumber the result of Player#getStatistic()
     * @param subStatName the name of the Material or EntityType of
     * this statistic-lookup, acquired by doing #toString() on the
     * Material/EntityType in question
     * @return [player-name]: [stat-number] [stat-name] [sub-stat-name]
     */
    TextComponent formatPlayerStat(String playerName, int statNumber, Statistic statistic, String subStatName);

    /**
     * Formats the input into a player statistic message with the specified
     * {@link Unit}. For Unit.Type.Time, the resulting formatted number will
     * have as many additional smaller units as are specified in the config,
     * unless <code>formatPlayerStatForTypeTime</code> is used.
     *
     * @param playerName the name of the Player
     * @param statistic the Statistic enum constant for this message
     * @param statNumber the result of Player#getStatistic()
     * @param unit the Unit to use when formatting the <code>statNumber</code>
     * @return [player-name]: [stat-number] [stat-name] [stat-unit]
     */
    TextComponent formatPlayerStat(String playerName, int statNumber, Statistic statistic, Unit unit);

    /**
     * Formats the input into a player statistic message for a time-based
     * statistic with the Unit-range that is between <code>bigUnit</code>
     * and <code>smallUnit</code> (both inclusive).
     *
     * @param playerName the name of the Player
     * @param statNumber the result of Player#getStatistic()
     * @param statistic the Statistic enum constant for this message
     * @param bigUnit the biggest Unit to use of {@link Unit.Type#TIME}
     * @param smallUnit the smallest Unit to use of {@link Unit.Type#TIME}
     * @return [player-name]: [1D 2H 3M 4S] [stat-name]
     */
    TextComponent formatPlayerStatForTypeTime(String playerName, int statNumber, Statistic statistic, Unit bigUnit, Unit smallUnit);
}