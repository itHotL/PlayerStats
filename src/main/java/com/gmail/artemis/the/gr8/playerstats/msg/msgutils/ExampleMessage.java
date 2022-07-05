package com.gmail.artemis.the.gr8.playerstats.msg.msgutils;

import com.gmail.artemis.the.gr8.playerstats.enums.PluginColor;
import com.gmail.artemis.the.gr8.playerstats.msg.ComponentFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class ExampleMessage implements TextComponent {

    private final TextComponent exampleMessage;
    private final ComponentFactory componentFactory;

    public ExampleMessage(ComponentFactory componentFactory, boolean isBukkitConsole) {
        this.componentFactory = componentFactory;
        exampleMessage = getExampleMessage(isBukkitConsole);
    }

    public TextComponent getExampleMessage(boolean isBukkitConsole) {
        TextColor mainColor = isBukkitConsole ? PluginColor.GOLD.getConsoleColor() : PluginColor.GOLD.getColor();
        TextColor accentColor1 = isBukkitConsole ? PluginColor.MEDIUM_GOLD.getConsoleColor() : PluginColor.MEDIUM_GOLD.getColor();
        TextColor accentColor3 = isBukkitConsole ? PluginColor.LIGHT_YELLOW.getConsoleColor() : PluginColor.LIGHT_YELLOW.getColor();
        String arrow = isBukkitConsole ? "    -> " : "    → ";  //4 spaces, alt + 26, 1 space

        return Component.newline()
                .append(componentFactory.prefixTitleComponent(isBukkitConsole))
                .append(Component.newline())
                .append(text("Examples: ").color(mainColor))
                .append(Component.newline())
                .append(text(arrow).color(mainColor)
                        .append(text("/statistic ")
                                .append(text("animals_bred ").color(accentColor1)
                                        .append(text("top").color(accentColor3)))))
                .append(Component.newline())
                .append(text(arrow).color(mainColor)
                        .append(text("/statistic ")
                                .append(text("mine_block diorite ").color(accentColor1)
                                        .append(text("me").color(accentColor3)))))
                .append(Component.newline())
                .append(text(arrow).color(mainColor)
                        .append(text("/statistic ")
                                .append(text("deaths ").color(accentColor1)
                                        .append(text("player ").color(accentColor3)
                                                .append(text("Artemis_the_gr8"))))));
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
