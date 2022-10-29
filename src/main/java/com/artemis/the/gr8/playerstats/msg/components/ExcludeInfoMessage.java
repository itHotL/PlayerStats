package com.artemis.the.gr8.playerstats.msg.components;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
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
        String arrowString = factory instanceof BukkitConsoleComponentFactory ? "    -> " : "    → ";  //4 spaces, alt + 26, 1 space
        TextComponent arrow = text(arrowString).color(factory.INFO_MSG);

        return Component.newline()
                .append(factory.pluginPrefixAsTitle())
                .append(Component.newline())
                .append(factory.subTitle("The /statexclude command is used to hide"))
                .append(Component.newline())
                .append(factory.subTitle("specific players' results from /stat lookups"))
                .append(Component.newline())
                .append(text("Excluded players are:").color(factory.INFO_MSG))
                .append(Component.newline())
                .append(arrow).append(text("not visible in the top 10").color(factory.INFO_MSG_ACCENT_1))
                .append(Component.newline())
                .append(arrow).append(text("not counted for the server total").color(factory.INFO_MSG_ACCENT_1));
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