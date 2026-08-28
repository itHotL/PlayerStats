package com.artemis.the.gr8.playerstats.api.events;

import com.artemis.the.gr8.playerstats.api.StatRequest;
import com.artemis.the.gr8.playerstats.api.StatResult;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired whenever PlayerStats calculates a statistic result.
 * This includes player stats, server stats, and top stats.
 * <p>
 * This event is called on the main thread after the statistic has been
 * calculated but before it is sent to the requesting player.
 * <p>
 * You can use this event to:
 * <ul>
 * <li>Send stat results to external systems (like Discord via DiscordSRV)
 * <li>Log stat lookups
 * <li>Perform additional actions based on stat results
 * </ul>
 */
public class StatCalculatedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final CommandSender sender;
    private final StatRequest<?> request;
    private final StatResult<?> result;

    public StatCalculatedEvent(@NotNull CommandSender sender, @NotNull StatRequest<?> request, @NotNull StatResult<?> result) {
        this.sender = sender;
        this.request = request;
        this.result = result;
    }

    /**
     * Gets the CommandSender who requested this statistic lookup.
     *
     * @return the CommandSender who initiated the stat request
     */
    @NotNull
    public CommandSender getSender() {
        return sender;
    }

    /**
     * Gets the StatRequest that was used to calculate this statistic.
     *
     * @return the StatRequest containing the lookup parameters
     */
    @NotNull
    public StatRequest<?> getRequest() {
        return request;
    }

    /**
     * Gets the calculated StatResult containing the formatted output.
     * <p>
     * You can use:
     * <ul>
     * <li>{@link StatResult#getNumericalValue()} to get the raw number
     * <li>{@link StatResult#getFormattedTextComponent()} to get the formatted Adventure TextComponent
     * <li>{@link StatResult#formattedString()} to get a plain String representation
     * </ul>
     *
     * @return the StatResult containing the calculated statistic
     */
    @NotNull
    public StatResult<?> getResult() {
        return result;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

