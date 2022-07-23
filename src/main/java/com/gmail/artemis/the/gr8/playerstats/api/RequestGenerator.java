package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

/** The {@link RequestGenerator} will help you turn a String (such as "stat animals_bred") into a specific {@link StatRequest}
 with all the information {@link PlayerStatsAPI} needs to work with. You'll need this StatRequest Object to get the statistic
 data that you want, and to format this data into a fancy Component or String, so you can output it somewhere.*/
public interface RequestGenerator {

    /** This will create a {@link StatRequest} from the provided args, with the requesting Player (or Console)
     as CommandSender. This CommandSender will receive feedback messages if the StatRequest could not be created.
     @param args an Array of args corresponding to a Statistic, a potential Sub-Statistic, and a Target
     (exactly as they are typed in Minecraft chat when using PlayerStatsAPI' /stat command -
     for example "/stat kill_entity bee top")
     @param sender the CommandSender that requested this specific statistic*/
    StatRequest generateRequest(CommandSender sender, String[] args);

    StatRequest generateRequest(CommandSender sender, String statName, @Nullable String subStatName, Target selection, @Nullable String PlayerName);
}
