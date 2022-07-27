package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/** This is the outgoing API that you can use to access the core functionality of PlayerStats.
 To work with it, you need to call PlayerStats.{@link #getAPI()} to get an instance of
 {@link PlayerStatsAPI}. You can then use this object to access any of the further methods.
 <br>
 <br>Since calculating a top or server statistics can take some time, I strongly
 encourage you to call all the getServerStat() and getTopStats() methods from the
 {@link StatCalculator} asynchronously. Otherwise, the main Thread will have to wait
 until all calculations are done, and this might severely impact server performance.
*/
public interface PlayerStats {

    /** Returns an instance of the {@link PlayerStatsAPI}.
     @throws IllegalStateException if PlayerStats is not loaded on the server when this method is called*/
    @Contract(pure = true)
    static @NotNull PlayerStats getAPI() throws IllegalStateException {
        return Main.getPlayerStatsAPI();
    }

    /** The {@link StatCalculator} is responsible for getting, calculating and/or ordering raw numbers.
     It gets its data from the vanilla statistic files (stored by the server). It can return three kinds of data,
     depending on the chosen {@link Target}:
     <br>- int (for {@link Target#PLAYER})
     <br>- long (for {@link Target#SERVER})
     <br>- LinkedHashMap[String player-name, Integer number] (for {@link Target#TOP})*/
    StatCalculator statCalculator();

    RequestGenerator requestGenerator();

    StatFormatter statFormatter();
}