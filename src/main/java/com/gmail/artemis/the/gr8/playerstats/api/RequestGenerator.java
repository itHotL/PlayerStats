package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

/** The {@link RequestGenerator} can turn some user input, such as a String
 (for example: "stat animals_bred") into a specific {@link StatRequest} that holds
 all the information {@link PlayerStatsAPI} needs to work with.
 This StatRequest is then used by the {@link StatCalculator} to get the desired statistic data.*/
public interface RequestGenerator {

    /** This will create a {@link StatRequest} from the provided args, with the requesting Player (or Console)
     as CommandSender. This CommandSender will receive feedback messages if the SimpleStatRequest could not be created.
     @param args an Array of args such as a CommandSender would put in Minecraft chat:
     <p>- a stat-name (example: "mine_block")</p>
     <p>- if applicable, a sub-stat-name (example: diorite)(</p>
     <p>- a target for this lookup: can be "top", "server", "player" (or "me" to indicate the current CommandSender)</p>
     <p>- if "player" was chosen, include a player-name</p>
     @param sender the CommandSender that requested this specific statistic
     @throws IllegalArgumentException if the args do not result in a valid statistic look-up*/
    StatRequest generateRequest(CommandSender sender, String[] args);

    StatRequest generateRequest(@NotNull Target selection, @NotNull Statistic statistic, Material material, EntityType entity, String playerName);
}
