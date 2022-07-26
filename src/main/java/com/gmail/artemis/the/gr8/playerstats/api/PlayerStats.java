package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.LanguageKeyHandler;
import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.StringUtils;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/** This is the outgoing API that you can use to access the core functionality of PlayerStats.
 To work with it, you need to call PlayerStats.{@link #getAPI()} to get an instance of {@link PlayerStatsAPI}.
 You can then use this object to access any of the further methods.
 <br>
 <br>Since calculating a top or server statistics can take some time, it is recommended to call all the
 getServerStat() or getTopStats() methods asynchronously. Otherwise, the main Thread will have
 to wait until all calculations are done, and this might cause some lag spikes on the server.
 <br>
 <br>The result of the methods in PlayerStats' API are returned in the form of a TextComponent,
 which can be sent directly to a Minecraft client or console with the Adventure library.
 To send a Component, you need to get a {@link BukkitAudiences} object. Normally you would
 have to add the library as a dependency, but since the library is included in PlayerStats, you can
 access it directly. Information on how to get and use the BukkitAudiences object can be found on
 <a href="https://docs.adventure.kyori.net/platform/bukkit.html">Adventure's website</a>.
 <br>
 <br>Alternatively, you can also turn your TextComponent into a plain String with
 {@link #statResultComponentToString(TextComponent)}. Don't use Adventure's method .content()
 on your statResult to do this - because of the way the TextComponent is built by PlayerStats,
 you won't be able to get the full content that way. </p>*/
public interface PlayerStats {

    /** Returns an instance of the {@link PlayerStatsAPI}.
     @throws IllegalStateException if PlayerStats is not loaded on the server when this method is called*/
    @Contract(pure = true)
    static @NotNull PlayerStats getAPI() throws IllegalStateException {
        return Main.getPlayerStatsAPI();
    }

    /** Turns a TextComponent into its String representation. If you don't want to work with
     Adventure's TextComponents, you can call this method to turn any stat-result into a String.
     @return a String representation of this TextComponent, without hover/click events, but with color, style and formatting */
    default String statResultComponentToString(TextComponent statResult) {
    ComponentFlattener flattener = ComponentFlattener.basic().toBuilder()
            .mapper(TranslatableComponent.class, trans ->
                    StringUtils.prettify(
                            LanguageKeyHandler.extractName(
                                    trans.key())))
            .build();

        return LegacyComponentSerializer.builder()
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .flattener(flattener)
                .build()
                .serialize(statResult);
    }

    /** Get a formatted player-statistic of Statistic.Type Untyped.
     @return a TextComponent with the following parts:
     <br>[player-name]: [number] [stat-name]
     @throws NullPointerException if statistic or playerName is null*/
    TextComponent getPlayerStat(@NotNull Statistic statistic, @NotNull String playerName) throws NullPointerException;

    /** Get a formatted player-statistic of Statistic.Type Block or Item.
     @return a TextComponent with the following parts:
     <br>[player-name]: [number] [stat-name] [sub-stat-name]
     @throws NullPointerException if statistic, material or playerName is null*/
    TextComponent getPlayerStat(@NotNull Statistic statistic, @NotNull Material material, @NotNull String playerName) throws NullPointerException;

    /** Get a formatted player-statistic of Statistic.Type Entity.
     @return a TextComponent with the following parts:
     <br>[player-name]: [number] [stat-name] [sub-stat-name]
     @throws NullPointerException if statistic, entity or playerName is null*/
    TextComponent getPlayerStat(@NotNull Statistic statistic, @NotNull EntityType entity, @NotNull String playerName) throws NullPointerException;

    /** Get a formatted server-statistic of Statistic.Type Untyped. Don't call this from the main Thread (see class description)!
     @return a TextComponent with the following parts:
     <br>[Total on] [server-name]: [number] [stat-name]
     @throws NullPointerException if statistic is null*/
    TextComponent getServerStat(@NotNull Statistic statistic) throws NullPointerException;

    /** Get a formatted server-statistic of Statistic.Type Block or Item. Don't call this from the main Thread (see class description)!
     @return a TextComponent with the following parts:
     <br>[Total on] [server-name]: [number] [stat-name] [sub-stat-name]
     @throws NullPointerException if statistic or material is null*/
    TextComponent getServerStat(@NotNull Statistic statistic, @NotNull Material material) throws NullPointerException;

    /** Get a formatted server-statistic of Statistic.Type Entity. Don't call this from the main Thread (see class description)!
     @return a TextComponent with the following parts:
     <br>[Total on] [server-name]: [number] [stat-name] [sub-stat-name]
     @throws NullPointerException if statistic or entity is null*/
    TextComponent getServerStat(@NotNull Statistic statistic, @NotNull EntityType entity) throws NullPointerException;

    /** Get a formatted top-statistic of Statistic.Type Untyped. Don't call this from the main Thread (see class description)!
     @return a TextComponent with the following parts:
     <br>[PlayerStats] [Top 10] [stat-name]
     <br> [1.] [player-name] [number]
     <br> [2.] [player-name] [number]
     <br> [3.] etc...
     @throws NullPointerException if statistic is null*/
    TextComponent getTopStats(@NotNull Statistic statistic) throws NullPointerException;

    /** Get a formatted top-statistic of Statistic.Type Block or Item. Don't call this from the main Thread (see class description)!
     @return a TextComponent with the following parts:
     <br>[PlayerStats] [Top 10] [stat-name] [sub-stat-name]
     <br> [1.] [player-name] [number]
     <br> [2.] [player-name] [number]
     <br> [3.] etc...
     @throws NullPointerException if statistic or material is null*/
    TextComponent getTopStats(@NotNull Statistic statistic, @NotNull Material material) throws NullPointerException;

    /** Get a formatted top-statistic of Statistic.Type Entity. Don't call this from the main Thread (see class description)!
     @return a TextComponent with the following parts:
     <br>[PlayerStats] [Top 10] [stat-name] [sub-stat-name]
     <br> [1.] [player-name] [number]
     <br> [2.] [player-name] [number]
     <br> [3.] etc...
     @throws NullPointerException if statistic or entity is null*/
    TextComponent getTopStats(@NotNull Statistic statistic, @NotNull EntityType entity) throws NullPointerException;
}