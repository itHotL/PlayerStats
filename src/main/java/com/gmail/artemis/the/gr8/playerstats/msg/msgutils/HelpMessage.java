package com.gmail.artemis.the.gr8.playerstats.msg.msgutils;

import com.gmail.artemis.the.gr8.playerstats.msg.BukkitConsoleComponentFactory;
import com.gmail.artemis.the.gr8.playerstats.msg.ComponentFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class HelpMessage implements TextComponent {

    private final ComponentFactory componentFactory;
    private final TextComponent helpMessage;

    public HelpMessage(ComponentFactory componentFactory, boolean useHover, int listSize) {
        this.componentFactory = componentFactory;

        if (!useHover) {
            helpMessage = getPlainHelpMsg(listSize);
        } else {
            helpMessage = helpMsgHover(listSize);
        }
    }

    private TextComponent getPlainHelpMsg(int listSize) {
        String arrowSymbol = "→";  //alt + 26
        String bulletSymbol = "•";  //alt + 7

        if (componentFactory instanceof BukkitConsoleComponentFactory) {
            arrowSymbol = "->";
            bulletSymbol = "*";
        }
        TextComponent spaces = text("    "); //4 spaces
        TextComponent arrow = text(arrowSymbol).color(componentFactory.msgMain2());
        TextComponent bullet = text(bulletSymbol).color(componentFactory.msgMain2());

        return Component.newline()
                .append(componentFactory.prefixTitleComponent())
                .append(newline())
                .append(text("Type \"/statistic examples\" to see examples!").color(componentFactory.brackets()).decorate(TextDecoration.ITALIC))
                .append(newline())
                .append(text("Usage:").color(componentFactory.msgMain2())).append(space())
                .append(text("/statistic").color(componentFactory.hoverAccent()))
                .append(newline())
                .append(spaces).append(arrow).append(space())
                .append(text("name").color(componentFactory.hoverAccent()))
                .append(newline())
                .append(spaces).append(arrow).append(space())
                .append(text("{sub-statistic}").color(componentFactory.hoverAccent())).append(space())
                .append(text("(a block, item or entity)").color(componentFactory.brackets()))
                .append(newline())
                .append(spaces).append(arrow).append(space())
                .append(text("me | player | server | top").color(componentFactory.hoverAccent()))
                .append(newline())
                .append(spaces).append(spaces).append(bullet).append(space())
                .append(text("me:").color(componentFactory.msgAccent())).append(space())
                .append(text("your own statistic").color(componentFactory.brackets()))
                .append(newline())
                .append(spaces).append(spaces).append(bullet).append(space())
                .append(text("player:").color(componentFactory.msgAccent())).append(space())
                .append(text("choose a player").color(componentFactory.brackets()))
                .append(newline())
                .append(spaces).append(spaces).append(bullet).append(space())
                .append(text("server:").color(componentFactory.msgAccent())).append(space())
                .append(text("everyone on the server combined").color(componentFactory.brackets()))
                .append(newline())
                .append(spaces).append(spaces).append(bullet).append(space())
                .append(text("top:").color(componentFactory.msgAccent())).append(space())
                .append(text("the top").color(componentFactory.brackets()).append(space()).append(text(listSize)))
                .append(newline())
                .append(spaces).append(arrow).append(space())
                .append(text("{player-name}").color(componentFactory.hoverAccent()));
    }

    private TextComponent helpMsgHover(int listSize) {
        TextComponent spaces = text("    ");
        TextComponent arrow = text("→").color(componentFactory.msgMain2());

        return Component.newline()
                .append(componentFactory.prefixTitleComponent())
                .append(newline())
                .append(componentFactory.subTitleComponent("Hover over the arguments for more information!"))
                .append(newline())
                .append(text("Usage:").color(componentFactory.msgMain2())).append(space())
                .append(text("/statistic").color(componentFactory.hoverAccent()))
                .append(newline())
                .append(spaces).append(arrow).append(space())
                .append(text("name").color(componentFactory.hoverAccent())
                        .hoverEvent(HoverEvent.showText(text("The name that describes the statistic").color(componentFactory.hoverMsg())
                                .append(newline())
                                .append(text("Example: ").color(componentFactory.msgMain2()))
                                .append(text("\"animals_bred\"").color(componentFactory.hoverAccent())))))
                .append(newline())
                .append(spaces).append(arrow).append(space())
                .append(text("sub-statistic").color(componentFactory.hoverAccent())
                        .hoverEvent(HoverEvent.showText(
                                text("Some statistics need an item, block or entity as extra input").color(componentFactory.hoverMsg())
                                        .append(newline())
                                        .append(text("Example: ").color(componentFactory.msgMain2())
                                                .append(text("\"mine_block diorite\"").color(componentFactory.hoverAccent()))))))
                .append(newline())
                .append(spaces).append(arrow
                        .hoverEvent(HoverEvent.showText(
                                text("Choose one").color(componentFactory.underscore())))).append(space())
                .append(text("me").color(componentFactory.hoverAccent())
                        .hoverEvent(HoverEvent.showText(
                                text("See your own statistic").color(componentFactory.hoverMsg()))))
                .append(text(" | ").color(componentFactory.hoverAccent()))
                .append(text("player").color(componentFactory.hoverAccent())
                        .hoverEvent(HoverEvent.showText(
                                text("Choose any player that has played on your server").color(componentFactory.hoverMsg()))))
                .append(text(" | ").color(componentFactory.hoverAccent()))
                .append(text("server").color(componentFactory.hoverAccent())
                        .hoverEvent(HoverEvent.showText(
                                text("See the combined total for everyone on your server").color(componentFactory.hoverMsg()))))
                .append(text(" | ").color(componentFactory.hoverAccent()))
                .append(text("top").color(componentFactory.hoverAccent())
                        .hoverEvent(HoverEvent.showText(
                                text("See the top").color(componentFactory.hoverMsg()).append(space())
                                        .append(text(listSize)))))
                .append(newline())
                .append(spaces).append(arrow).append(space())
                .append(text("player-name").color(componentFactory.hoverAccent())
                        .hoverEvent(HoverEvent.showText(
                                text("In case you typed").color(componentFactory.hoverMsg()).append(space())
                                        .append(text("\"player\"").color(componentFactory.hoverAccent()))
                                                .append(text(", add the player's name")))));
    }

    @Override
    public @NotNull String content() {
        return helpMessage.content();
    }

    @Override
    public @NotNull TextComponent content(@NotNull String content) {
        return helpMessage.content(content);
    }

    @Override
    public @NotNull Builder toBuilder() {
        return helpMessage.toBuilder();
    }

    @Override
    public @Unmodifiable @NotNull List<Component> children() {
        return helpMessage.children();
    }

    @Override
    public @NotNull TextComponent children(@NotNull List<? extends ComponentLike> children) {
        return helpMessage.children(children);
    }

    @Override
    public @NotNull Style style() {
        return helpMessage.style();
    }

    @Override
    public @NotNull TextComponent style(@NotNull Style style) {
        return helpMessage.style(style);
    }

    private TextComponent space() {
        return Component.space();
    }

    private TextComponent newline() {
        return Component.newline();
    }
}