package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.PlayerStatRequest;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.ServerStatRequest;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.TopStatRequest;
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

    /** Gets a StatRequest object that can be used to look up a player-statistic.
     This StatRequest will have all default settings already configured,
     and will be processed as soon as you call one of its methods.

     @return a PlayerStatRequest that can be used to look up a statistic for the
     Player whose name is provided*/
    PlayerStatRequest playerStat(String playerName);

    /** Gets a StatRequest object that can be used to look up a server-statistic.
     This StatRequest will have all default settings already configured,
     and will be processed as soon as you call one of its methods.
     <br>
     <br> Don't call this from the main Thread! (see class description)

     @return a ServerStatRequest that can be used to look up a server total*/
    ServerStatRequest serverStat();

    /** Gets a StatRequest object that can be used to look up a top-x-statistic.
     This StatRequest will have all default settings already configured, and will be
     processed as soon as you call one of its methods.
     <br>
     <br> Don't call this from the main Thread! (see class description)

     @param topListSize how big the top-x should be (10 by default)
     @return a TopStatRequest that can be used to look up a top statistic*/
    TopStatRequest topStat(int topListSize);
}