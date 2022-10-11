package com.artemis.the.gr8.playerstats.api;

import com.artemis.the.gr8.playerstats.Main;
import com.artemis.the.gr8.playerstats.statistic.request.StatRequest;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The outgoing API that represents the core functionality of PlayerStats!
 *
 * <p> To work with it, you'll need to call PlayerStats.{@link #getAPI()} and get an instance of
 * {@link PlayerStatsImpl}. You can then use this object to access any of the further methods.
 *
 * <p> Since calculating a top or server statistics can take some time, I strongly
 * encourage you to call {@link StatRequest#execute()} asynchronously.
 * Otherwise, the main Thread will have to wait until all calculations are done,
 * and this can severely impact server performance.
 *
 * @see StatManager
 * @see StatFormatter
*/
public interface PlayerStats {

    /** Gets an instance of the {@link PlayerStatsImpl}.

     * @return the PlayerStats API
     * @throws IllegalStateException if PlayerStats is not loaded on the server when this method is called*/
    @Contract(pure = true)
    static @NotNull PlayerStats getAPI() throws IllegalStateException {
        return Main.getPlayerStatsAPI();
    }

    /**
     * Gets the current version of PlayerStatsImpl.
     * Use this method to ensure the correct version of
     * PlayerStats is running on the server before
     * accessing further API methods, to prevent
     * <code>ClassDefNotFoundExceptions</code>.
     *
     * @return the version of PlayerStatsImpl present on the server
     */
    default String getVersion() {
        return "1.8";
    }

    StatManager getStatManager();

    StatFormatter getFormatter();
}