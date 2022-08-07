package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.Main;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/** The outgoing API that you can use to access the core functionality of PlayerStats!
 To work with the API, you need to call PlayerStats.{@link #getAPI()} to get an instance of
 {@link PlayerStatsAPI}. You can then use this object to access any of the further methods.
 <br>
 <br>Since calculating a top or server statistics can take some time, I strongly
 encourage you to call all the serverStat() and topStat() methods asynchronously.
 Otherwise, the main Thread will have to wait until all calculations are done,
 and this can severely impact server performance.
*/
public interface PlayerStats {

    /** Gets an instance of the {@link PlayerStatsAPI}.

     @return the PlayerStats API
     @throws IllegalStateException if PlayerStats is not loaded on the server when this method is called*/
    @Contract(pure = true)
    static @NotNull PlayerStats getAPI() throws IllegalStateException {
        return Main.getPlayerStatsAPI();
    }

    StatManager getStatManager();

    Formatter getFormatter();
}