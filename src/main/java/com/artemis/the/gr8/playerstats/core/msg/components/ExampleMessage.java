package com.artemis.the.gr8.playerstats.core.msg.components;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

/**
 * A fully constructed message with examples on how to use PlayerStats.
 */
public final class ExampleMessage implements TextComponent {

    private final TextComponent exampleMessage;

    private ExampleMessage(ComponentFactory factory) {
        exampleMessage = buildMessage(factory);
    }

    @Contract("_ -> new")
    public static @NotNull ExampleMessage construct(ComponentFactory factory) {
        return new ExampleMessage(factory);
    }

    private @NotNull TextComponent buildMessage(@NotNull ComponentFactory factory) {
        TextComponent spaces = text("    ");  //4 spaces

        return Component.newline()
                .append(factory.pluginPrefixAsTitle())
                .append(Component.newline())
                .append(factory.subTitle("Examples: "))
                .append(Component.newline())
                .append(spaces).append(
                        factory.arrow()).append(Component.space())
                .append(text("/stat ").color(factory.INFO_MSG)
                        .append(text("animals_bred ").color(factory.INFO_MSG_ACCENT_MEDIUM)
                                .append(text("top").color(factory.INFO_MSG_ACCENT_LIGHTEST))))
                .append(Component.newline())
                .append(spaces).append(
                        factory.arrow()).append(Component.space())
                .append(text("/stat ").color(factory.INFO_MSG)
                        .append(text("mine_block diorite ").color(factory.INFO_MSG_ACCENT_MEDIUM)
                                .append(text("me").color(factory.INFO_MSG_ACCENT_LIGHTEST))))
                .append(Component.newline())
                .append(spaces).append(
                        factory.arrow()).append(Component.space())
                .append(text("/stat ").color(factory.INFO_MSG)
                        .append(text("deaths ").color(factory.INFO_MSG_ACCENT_MEDIUM)
                                .append(text("player ").color(factory.INFO_MSG_ACCENT_LIGHTEST)
                                        .append(factory.getExampleName()))));
    }

    @Override
    public @NotNull String content() {
        return exampleMessage.content();
    }

    @Override
    public @NotNull TextComponent content(@NotNull String content) {
        return exampleMessage.content(content);
    }

    @Override
    public @NotNull TextComponent.Builder toBuilder() {
        return exampleMessage.toBuilder();
    }

    @Override
    public @Unmodifiable @NotNull List<Component> children() {
        return exampleMessage.children();
    }

    @Override
    public @NotNull TextComponent children(@NotNull List<? extends ComponentLike> children) {
        return exampleMessage.children(children);
    }

    @Override
    public @NotNull Style style() {
        return exampleMessage.style();
    }

    @Override
    public @NotNull TextComponent style(@NotNull Style style) {
        return exampleMessage.style(style);
    }
}