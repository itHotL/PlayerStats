package com.artemis.the.gr8.playerstats.core.msg.components;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public final class ExcludeInfoMessage implements TextComponent {

    private final TextComponent excludeInfo;

    private ExcludeInfoMessage(ComponentFactory factory) {
        excludeInfo = buildMessage(factory);
    }

    @Contract("_ -> new")
    public static @NotNull ExcludeInfoMessage construct(ComponentFactory factory) {
        return new ExcludeInfoMessage(factory);
    }

    private @NotNull TextComponent buildMessage(@NotNull ComponentFactory factory) {
        TextComponent spaces = text("    ");

        return Component.newline()
                .append(factory.pluginPrefixAsTitle())
                .append(Component.newline())
                .append(factory.subTitle("Hide a player's statistics from /stat results"))
                .append(Component.newline())
                .append(text("Excluded players are:")
                        .color(factory.INFO_MSG))
                .append(Component.newline())
                .append(spaces).append(
                        factory.arrow()).append(Component.space())
                        .append(text("not visible in the top 10")
                                .color(factory.INFO_MSG_ACCENT_MEDIUM))
                .append(Component.newline())
                .append(spaces).append(
                        factory.arrow()).append(Component.space())
                        .append(text("not counted for the server total")
                                .color(factory.INFO_MSG_ACCENT_MEDIUM))
                .append(Component.newline())
                .append(spaces).append(
                        factory.arrow()).append(Component.space())
                        .append(text("hidden")
                                .decorate(TextDecoration.BOLD)
                                .color(factory.INFO_MSG_ACCENT_MEDIUM)
                                .hoverEvent(HoverEvent.showText(text("All data is still stored by the server,")
                                        .append(Component.newline())
                                        .append(text("and excluded players can still look up"))
                                        .append(Component.newline())
                                        .append(text("their own statistics in the in-game menu"))
                                        .color(factory.FEEDBACK_MSG))))
                .append(text(", not removed")
                        .decorations(TextDecoration.NAMES.values(), false)
                        .color(factory.INFO_MSG_ACCENT_MEDIUM))
                .append(Component.newline())
                .append(Component.newline())
                .append(text("Usage: ").color(factory.INFO_MSG)
                        .append(text("/statexclude").color(factory.INFO_MSG_ACCENT_MEDIUM)))
                .append(Component.newline())
                .append(spaces).append(spaces).append(
                        factory.bulletPoint()).append(Component.space())
                        .append(text("add ").color(factory.INFO_MSG_ACCENT_DARKEST))
                        .append(text("player-name").color(factory.INFO_MSG_ACCENT_MEDIUM))
                .append(Component.newline())
                .append(spaces).append(spaces).append(
                        factory.bulletPoint()).append(Component.space())
                        .append(text("remove ").color(factory.INFO_MSG_ACCENT_DARKEST))
                        .append(text("player-name").color(factory.INFO_MSG_ACCENT_MEDIUM));
    }

    @Override
    public @NotNull String content() {
        return excludeInfo.content();
    }

    @Override
    public @NotNull TextComponent content(@NotNull String content) {
        return excludeInfo.content(content);
    }

    @Override
    public @NotNull Builder toBuilder() {
        return excludeInfo.toBuilder();
    }

    @Override
    public @Unmodifiable @NotNull List<Component> children() {
        return excludeInfo.children();
    }

    @Override
    public @NotNull TextComponent children(@NotNull List<? extends ComponentLike> children) {
        return excludeInfo.children(children);
    }

    @Override
    public @NotNull Style style() {
        return excludeInfo.style();
    }

    @Override
    public @NotNull TextComponent style(@NotNull Style style) {
        return excludeInfo.style(style);
    }
}