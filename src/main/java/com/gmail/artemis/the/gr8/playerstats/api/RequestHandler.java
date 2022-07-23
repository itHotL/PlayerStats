package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import org.bukkit.command.CommandSender;

/** The {@link RequestHandler} will help you turn a String (such as "stat animals_bred") into a specific {@link StatRequest}
 with all the information {@link PlayerStatsAPI} needs to work with. You'll need this StatRequest Object to get the statistic
 data that you want, and to format this data into a fancy Component or String, so you can output it somewhere.*/
public interface RequestHandler {

    /** This will create a {@link StatRequest} from the provided args, with the requesting Player (or Console)
     as CommandSender. This CommandSender will receive feedback messages if the StatRequest could not be created.
     @param args an Array of args corresponding to a Statistic, a potential Sub-Statistic, and a Target
     (exactly as they are typed in Minecraft chat when using PlayerStatsAPI' /stat command -
     for example "/stat kill_entity bee top")
     @param sender the CommandSender that requested this specific statistic*/
    StatRequest generateRequest(CommandSender sender, String[] args);

    /** This method validates the {@link StatRequest} and returns feedback to the player if it returns false.
     It checks the following:
     <p>1. Is a Statistic set?</p>
     <p>2. Is a subStat needed, and is a subStat Enum constant present? (block/entity/item)</p>
     <p>3. If the target is PLAYER, is a valid PlayerName provided? </p>
     @return true if the StatRequest is valid, and false + an explanation message otherwise. */
    boolean requestIsValid(StatRequest request);
}
