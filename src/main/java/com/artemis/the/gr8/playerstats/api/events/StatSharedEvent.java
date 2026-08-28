package com.artemis.the.gr8.playerstats.api.events;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when a player shares a statistic result to the server chat
 * by clicking the share button or using the /statshare command.
 * <p>
 * This event is cancellable. If cancelled, the stat result will not be broadcast
 * to all players.
 * <p>
 * You can use this event to:
 * <ul>
 * <li>Send shared stats to Discord via DiscordSRV
 * <li>Log stat shares
 * <li>Prevent certain stats from being shared
 * <li>Send the stat to custom channels
 * </ul>
 */
public class StatSharedEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final CommandSender sender;
    private final TextComponent statResult;
    private final int shareCode;
    private boolean cancelled = false;

    public StatSharedEvent(@NotNull CommandSender sender, @NotNull TextComponent statResult, int shareCode) {
        this.sender = sender;
        this.statResult = statResult;
        this.shareCode = shareCode;
    }

    /**
     * Gets the CommandSender who is sharing the statistic.
     *
     * @return the CommandSender sharing the stat
     */
    @NotNull
    public CommandSender getSender() {
        return sender;
    }

    /**
     * Gets the formatted statistic result that is being shared.
     * This is an Adventure TextComponent with formatting, colors, and hover text.
     * <p>
     * For a plain String representation, you can serialize this component or
     * use Adventure's PlainTextComponentSerializer.
     *
     * @return the formatted stat result as a TextComponent
     */
    @NotNull
    public TextComponent getStatResult() {
        return statResult;
    }

    /**
     * Gets the share code that was used to retrieve this statistic.
     * This is the unique identifier for this particular stat share.
     *
     * @return the share code
     */
    public int getShareCode() {
        return shareCode;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
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
