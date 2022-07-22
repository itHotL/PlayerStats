package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import org.bukkit.command.CommandSender;

/** The RequestManager will help you turn a String (such as "stat animals_bred") into a specific StatRequest
 with all the information PlayerStats needs to work with. You'll need this StatRequest Object to get the Statistic
 data that you want, and to format it into a fancy Component or String, so you can output it somewhere.*/
public interface RequestManager {

    /** This will create a StatRequest from the provided args, with the requesting Player (or Console)
     as CommandSender. This CommandSender will receive feedback messages if the StatRequest could not be created.
     @param args an Array of args corresponding to a Statistic, a potential Sub-Statistic, and a Target
     (exactly as one would type them in Minecraft chat when using PlayerStats' /stat command)*/
    StatRequest generateRequest(CommandSender sender, String[] args);

    boolean requestIsValid(StatRequest request);
}
