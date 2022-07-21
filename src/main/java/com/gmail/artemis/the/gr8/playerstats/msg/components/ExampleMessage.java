package com.gmail.artemis.the.gr8.playerstats.msg.components;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class ExampleMessage implements TextComponent {

    private final TextComponent exampleMessage;

    private ExampleMessage(ComponentFactory factory) {
        exampleMessage = buildMessage(factory);
    }

    public static ExampleMessage construct(ComponentFactory factory) {
        return new ExampleMessage(factory);
    }

    private TextComponent buildMessage(ComponentFactory factory) {
        String arrow = factory instanceof BukkitConsoleComponentFactory ? "    -> " : "    → ";  //4 spaces, alt + 26, 1 space

        return Component.newline()
                .append(factory.pluginPrefixAsTitle())
                .append(Component.newline())
                .append(text("Examples: ").color(factory.MSG_MAIN_2))
                .append(Component.newline())
                .append(text(arrow).color(factory.MSG_MAIN_2)
                        .append(text("/statistic ")
                                .append(text("animals_bred ").color(factory.MSG_ACCENT_2A)
                                        .append(text("top").color(factory.MSG_ACCENT_2B)))))
                .append(Component.newline())
                .append(text(arrow).color(factory.MSG_MAIN_2)
                        .append(text("/statistic ")
                                .append(text("mine_block diorite ").color(factory.MSG_ACCENT_2A)
                                        .append(text("me").color(factory.MSG_ACCENT_2B)))))
                .append(Component.newline())
                .append(text(arrow).color(factory.MSG_MAIN_2)
                        .append(text("/statistic ")
                                .append(text("deaths ").color(factory.MSG_ACCENT_2A)
                                        .append(text("player ").color(factory.MSG_ACCENT_2B)
                                                .append(text("Artemis_the_gr8")
                                                        .color(factory.getExampleNameColor()))))));
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