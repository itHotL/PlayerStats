package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.LinkedHashMap;

/** The {@link StatCalculator} is responsible for getting, calculating and/or ordering raw numbers.
 It represents the actual statistic-getting magic that happens once a valid
 {@link StatRequest} is passed to it.
 <br>
 <br>The StatCalculator gets its data from the vanilla statistic files (stored by the server). It can return three kinds of data,
 depending on the chosen {@link Target}:
 <br>- int (for {@link Target#PLAYER})
 <br>- long (for {@link Target#SERVER})
 <br>- LinkedHashMap[String player-name, Integer number] (for {@link Target#TOP})
 <br>
 <br>For more information on how to create a valid StatRequest,
 see the class description for {@link StatRequest}.*/
@Internal
public interface StatCalculator {

    /** Returns the requested Statistic*/
    int getPlayerStat(StatRequest statRequest);

    /** Don't call from main Thread!*/
    long getServerStat(StatRequest statRequest);

    /** Don't call from main Thread!*/
    LinkedHashMap<String, Integer> getTopStats(StatRequest statRequest);
}