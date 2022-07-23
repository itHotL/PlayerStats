package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.Main;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/** This Interface is the outgoing API and represents the heart of PlayerStats.
 To work with it, you can call {@link #getAPI()} to get an instance of {@link PlayerStatsAPI}.*/
public interface PlayerStats {

    /** Returns an instance of the {@link PlayerStatsAPI}.
     @throws IllegalStateException if PlayerStats is not loaded on the server while you're trying to access the API*/
    @Contract(pure = true)
    static @NotNull PlayerStats getAPI() throws IllegalStateException {
        return Main.getPlayerStatsAPI();
    }

    /** Returns a stat-result as if the caller ran the /stat command in Minecraft chat. Since calculating the
     top or server statistics can take some time, it is recommended to call this method asynchronously
     (otherwise the main Thread will have to wait until the calculations are done).
     The result of this method is returned in the form of a TextComponent,
     which can be sent directly to a Minecraft client, or turned into a String with {@link #componentToString(TextComponent)}.
     @param args an Array of args very similar to the input a CommandSender would put in Minecraft chat:
     <p>- a stat-name (example: "mine_block")</p>
     <p>- if applicable, a sub-stat-name (example: diorite)(</p>
     <p>- a target for this lookup: can be "top", "server", "player" (or "me" to indicate the current CommandSender)</p>
     <p>- if "player" was chosen, include a player-name</p>
     @param sender the CommandSender that requested this specific statistic
     @throws IllegalArgumentException if the args do not result in a valid statistic look-up*/
    TextComponent getFancyStat(CommandSender sender, String[] args) throws IllegalArgumentException;

    /** Turns a TextComponent into its String representation. It will lose all color and style,
     but it will keep line-breaks.*/
    String componentToString(TextComponent component);
}