package com.artemis.the.gr8.playerstats.api;

import com.artemis.the.gr8.playerstats.core.Main;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The outgoing API that represents the core functionality of PlayerStats!
 *
 * <p> To work with it, you'll need to call PlayerStats.{@link #getAPI()}
 * and get an instance of PlayerStats. You can then use this object to
 * access any of the further methods.
 *
 * @see StatManager
 * @see StatTextFormatter
 * @see StatNumberFormatter
*/
public interface PlayerStats {

    /** Gets an instance of the PlayerStatsAPI.

     * @return the PlayerStats API
     * @throws IllegalStateException if PlayerStats is not loaded on
     * the server when this method is called
     */
    @Contract(pure = true)
    static @NotNull PlayerStats getAPI() throws IllegalStateException {
        return Main.getPlayerStatsAPI();
    }

    /**
     * Gets the version number of the PlayerStats API
     * that's present for this instance of PlayerStats.
     * This number equals the major version number
     * of PlayerStats. For v1.7.2, for example,
     * the API version will be 1.
     *
     * @return the API version number
     */
    String getVersion();

    StatManager getStatManager();

    StatTextFormatter getStatTextFormatter();

    StatNumberFormatter getStatNumberFormatter();
}