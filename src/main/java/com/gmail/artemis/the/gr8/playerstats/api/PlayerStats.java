package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.Main;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/** This Interface is the outgoing API that provides access to the core functionality of PlayerStats.
 To work with it, you can call PlayerStats.{@link #getAPI()} to get an instance of {@link PlayerStatsAPI}.
 You can then use this object to get formatted statistics.

 <p>Since calculating a top or server statistics can take some time, it is recommended to call any of the
 getServerStat() or getTopStats() methods asynchronously. Otherwise, the main Thread will have
 to wait until all calculations are done, and this might cause lag spikes on the server.</p>

 <p>The result of the methods in PlayerStats' API are returned in the form of a TextComponent,
 which can be sent directly to a Minecraft client with the Adventure library,
 or turned into a String with {@link #statResultComponentToString(TextComponent)}.
 Don't use the Adventure method .content() on your result instead of the method specified in here -
 because of the way the TextComponent is built by PlayerStats, you won't be able to get the full content that way. </p>*/
public interface PlayerStats {

    /** Returns an instance of the {@link PlayerStatsAPI}.
     @throws IllegalStateException if PlayerStats is not loaded on the server when this method is called*/
    @Contract(pure = true)
    static @NotNull PlayerStats getAPI() throws IllegalStateException {
        return Main.getPlayerStatsAPI();
    }

    /** Turns a TextComponent into its String representation. If you don't want to work with
     Adventure's TextComponents, you can call this method to turn any stat-result into a String.
     It will lose all color and style, but it will keep line-breaks.*/
    default String statResultComponentToString(TextComponent statResult) {
        return PlainTextComponentSerializer.plainText().serialize(statResult);
    }

    /** Get a formatted player-statistic of Statistic.Type UNTYPED.*/
    TextComponent getPlayerStat(@NotNull Statistic statistic, @NotNull String playerName) throws IllegalArgumentException;

    /** Get a formatted player-statistic of Statistic.Type BLOCK or ITEM.*/
    TextComponent getPlayerStat(@NotNull Statistic statistic, @NotNull Material material, @NotNull String playerName) throws IllegalArgumentException;

    /** Get a formatted player-statistic of Statistic.Type ENTITY.*/
    TextComponent getPlayerStat(@NotNull Statistic statistic, @NotNull EntityType entity, @NotNull String playerName) throws IllegalArgumentException;

    /** Get a formatted server-statistic of Statistic.Type UNTYPED. Not recommended to call this from the main Thread (see class description).*/
    TextComponent getServerStat(@NotNull Statistic statistic) throws IllegalArgumentException;

    /** Get a formatted server-statistic of Statistic.Type BLOCK or ITEM. Not recommended to call this from the main Thread (see class description).*/
    TextComponent getServerStat(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException;

    /** Get a formatted server-statistic of Statistic.Type ENTITY. Not recommended to call this from the main Thread (see class description).*/
    TextComponent getServerStat(@NotNull Statistic statistic, @NotNull EntityType entity) throws IllegalArgumentException;

    /** Get a formatted top-statistic of Statistic.Type UNTYPED. Not recommended to call this from the main Thread (see class description).*/
    TextComponent getTopStats(@NotNull Statistic statistic) throws IllegalArgumentException;

    /** Get a formatted top-statistic of Statistic.Type BLOCK or ITEM. Not recommended to call this from the main Thread (see class description).*/
    TextComponent getTopStats(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException;

    /** Get a formatted top-statistic of Statistic.Type ENTITY. Not recommended to call this from the main Thread (see class description).*/
    TextComponent getTopStats(@NotNull Statistic statistic, @NotNull EntityType entity) throws IllegalArgumentException;
}