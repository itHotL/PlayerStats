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
     * Gets the current version of PlayerStatsAPI.
     * Use this method to ensure the correct version of
     * PlayerStats is running on the server before
     * accessing further API methods, to prevent
     * <code>ClassDefNotFoundExceptions</code>.
     *
     * @return the version of PlayerStatsAPI present on the server
     */
    String getVersion();

    StatManager getStatManager();

    StatTextFormatter getStatTextFormatter();

    StatNumberFormatter getStatNumberFormatter();
}