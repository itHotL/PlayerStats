package com.gmail.artemis.the.gr8.playerstats.msg.msgutils;

import com.gmail.artemis.the.gr8.playerstats.msg.BukkitConsoleComponentFactory;
import com.gmail.artemis.the.gr8.playerstats.msg.ComponentFactory;
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
    private final ComponentFactory componentFactory;

    public ExampleMessage(ComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
        exampleMessage = getExampleMessage();
    }

    public TextComponent getExampleMessage() {
        String arrow = componentFactory instanceof BukkitConsoleComponentFactory ? "    -> " : "    → ";  //4 spaces, alt + 26, 1 space

        return Component.newline()
                .append(componentFactory.prefixTitleComponent())
                .append(Component.newline())
                .append(text("Examples: ").color(componentFactory.msgMain2()))
                .append(Component.newline())
                .append(text(arrow).color(componentFactory.msgMain2())
                        .append(text("/statistic ")
                                .append(text("animals_bred ").color(componentFactory.msgAccent2A())
                                        .append(text("top").color(componentFactory.msgAccent2B())))))
                .append(Component.newline())
                .append(text(arrow).color(componentFactory.msgMain2())
                        .append(text("/statistic ")
                                .append(text("mine_block diorite ").color(componentFactory.msgAccent2A())
                                        .append(text("me").color(componentFactory.msgAccent2B())))))
                .append(Component.newline())
                .append(text(arrow).color(componentFactory.msgMain2())
                        .append(text("/statistic ")
                                .append(text("deaths ").color(componentFactory.msgAccent2A())
                                        .append(text("player ").color(componentFactory.msgAccent2B())
                                                .append(text("Artemis_the_gr8")
                                                        .color(componentFactory.getExampleNameColor()))))));
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