package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import org.bukkit.command.CommandSender;

public interface RequestManager {

    /** This will create a StatRequest from the provided args, with the requesting Player (or Console)
     as CommandSender. This CommandSender will receive feedback messages if the StatRequest could not be created.
     @param args an Array of args corresponding to a Statistic, a potential Sub-Statistic, and a Target
     (exactly as one would type them in Minecraft chat when using PlayerStats' /stat command)*/
    StatRequest generateRequest(CommandSender sender, String[] args);

    boolean requestIsValid(StatRequest request);
}
