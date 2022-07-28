package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

/** Turns user input into a valid {@link StatRequest}. This StatRequest should hold all
 the information PlayerStats needs to work with, and is used by the {@link StatCalculator}
 to get the desired statistic data.*/
public interface RequestGenerator {

    /** This will create a {@link StatRequest} from the provided args, with the requesting Player (or Console)
     as CommandSender. This CommandSender will receive feedback messages if the StatRequest could not be created.

     @param args an Array of args such as a CommandSender would put in Minecraft chat:
     <p>- a <code>statName</code> (example: "mine_block")</p>
     <p>- if applicable, a <code>subStatEntryName</code> (example: diorite)(</p>
     <p>- a <code>target</code> for this lookup: can be "top", "server", "player" (or "me" to indicate the current CommandSender)</p>
     <p>- if "player" was chosen, include a <code>playerName</code></p>

     @param sender the CommandSender that requested this specific statistic
     @return the generated StatRequest
     */
    StatRequest generateRequest(CommandSender sender, String[] args);

    StatRequest generateAPIRequest(Target target, Statistic statistic, Material material, EntityType entity, String playerName);

    boolean validateRequest(StatRequest request);

    boolean validateAPIRequest(StatRequest request);
}
