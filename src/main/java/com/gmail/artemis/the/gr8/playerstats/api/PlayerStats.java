package com.gmail.artemis.the.gr8.playerstats.api;

public interface PlayerStats {

    /** The RequestManager will help you turn a String (such as "stat animals_bred") into a specific StatRequest
     with all the information PlayerStats needs to work with. You'll need this StatRequest Object to get the Statistic
     data that you want, and to format it into a fancy Component or String, so you can output it somewhere.*/
    RequestManager getRequestGenerator();

    StatGetter getStatGetter();

    StatFormatter getStatFormatter();
}
