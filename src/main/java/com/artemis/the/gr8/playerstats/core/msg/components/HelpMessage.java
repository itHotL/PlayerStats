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

/**
 * The help message that explains how to use PlayerStats.
 */
public final class HelpMessage implements TextComponent {

    private final TextComponent helpMessage;

    private HelpMessage(ComponentFactory factory, boolean useHover, int listSize) {
        if (!useHover) {
            helpMessage = buildPlainMsg(factory, listSize);
        } else {
            helpMessage = buildHoverMsg(factory, listSize);
        }
    }

    @Contract("_, _ -> new")
    public static @NotNull HelpMessage constructPlainMsg(ComponentFactory factory, int listSize) {
        return new HelpMessage(factory, false, listSize);
    }

    @Contract("_, _ -> new")
    public static @NotNull HelpMessage constructHoverMsg(ComponentFactory factory, int listSize) {
        return new HelpMessage(factory, true, listSize);
    }

    private @NotNull TextComponent buildPlainMsg(ComponentFactory factory, int listSize) {
        return Component.newline()
                .append(factory.pluginPrefixAsTitle())
                .append(newline())
                .append(text("Type \"/statistic examples\" to see examples!").color(factory.BRACKETS).decorate(TextDecoration.ITALIC))
                .append(newline())
                .append(text("Usage:").color(factory.INFO_MSG)).append(space())
                .append(text("/statistic").color(factory.INFO_MSG_ACCENT_MEDIUM))
                .append(newline())
                .append(factory.arrow()).append(space())
                .append(text("name").color(factory.INFO_MSG_ACCENT_MEDIUM))
                .append(newline())
                .append(factory.arrow()).append(space())
                .append(text("{sub-statistic}").color(factory.INFO_MSG_ACCENT_MEDIUM)).append(space())
                .append(text("(a block, item or entity)").color(factory.BRACKETS))
                .append(newline())
                .append(factory.arrow()).append(space())
                .append(text("me | player | server | top").color(factory.INFO_MSG_ACCENT_MEDIUM))
                .append(newline())
                .append(factory.bulletPointIndented()).append(space())
                .append(text("me:").color(factory.INFO_MSG_ACCENT_DARKEST)).append(space())
                .append(text("your own statistic").color(factory.BRACKETS))
                .append(newline())
                .append(factory.bulletPointIndented()).append(space())
                .append(text("player:").color(factory.INFO_MSG_ACCENT_DARKEST)).append(space())
                .append(text("choose a player").color(factory.BRACKETS))
                .append(newline())
                .append(factory.bulletPointIndented()).append(space())
                .append(text("server:").color(factory.INFO_MSG_ACCENT_DARKEST)).append(space())
                .append(text("everyone on the server combined").color(factory.BRACKETS))
                .append(newline())
                .append(factory.bulletPointIndented()).append(space())
                .append(text("top:").color(factory.INFO_MSG_ACCENT_DARKEST)).append(space())
                .append(text("the top").color(factory.BRACKETS).append(space()).append(text(listSize)))
                .append(newline())
                .append(factory.arrow()).append(space())
                .append(text("{player-name}").color(factory.INFO_MSG_ACCENT_MEDIUM));
    }

    private @NotNull TextComponent buildHoverMsg(@NotNull ComponentFactory factory, int listSize) {
        return Component.newline()
                .append(factory.pluginPrefixAsTitle())
                .append(newline())
                .append(factory.subTitle("Hover over the arguments for more information!"))
                .append(newline())
                .append(text("Usage:").color(factory.INFO_MSG)).append(space())
                .append(text("/statistic").color(factory.INFO_MSG_ACCENT_MEDIUM))
                .append(newline())
                .append(factory.arrow()).append(space())
                .append(text("name").color(factory.INFO_MSG_ACCENT_MEDIUM)
                        .hoverEvent(HoverEvent.showText(text("The name that describes the statistic").color(factory.MSG_HOVER)
                                .append(newline())
                                .append(text("Example: ").color(factory.INFO_MSG))
                                .append(text("\"animals_bred\"").color(factory.INFO_MSG_ACCENT_MEDIUM)))))
                .append(newline())
                .append(factory.arrow()).append(space())
                .append(text("sub-statistic").color(factory.INFO_MSG_ACCENT_MEDIUM)
                        .hoverEvent(HoverEvent.showText(
                                text("Some statistics need an item, block or entity as extra input").color(factory.MSG_HOVER)
                                        .append(newline())
                                        .append(text("Example: ").color(factory.INFO_MSG)
                                                .append(text("\"mine_block diorite\"").color(factory.INFO_MSG_ACCENT_MEDIUM))))))
                .append(newline())
                .append(factory.arrow()
                        .hoverEvent(HoverEvent.showText(
                                text("Choose one").color(factory.MSG_CLICKED))))
                .append(space())
                .append(text("me").color(factory.INFO_MSG_ACCENT_MEDIUM)
                        .hoverEvent(HoverEvent.showText(
                                text("See your own statistic").color(factory.MSG_HOVER))))
                .append(text(" | ").color(factory.INFO_MSG_ACCENT_MEDIUM))
                .append(text("player").color(factory.INFO_MSG_ACCENT_MEDIUM)
                        .hoverEvent(HoverEvent.showText(
                                text("Choose any player that has played on your server").color(factory.MSG_HOVER))))
                .append(text(" | ").color(factory.INFO_MSG_ACCENT_MEDIUM))
                .append(text("server").color(factory.INFO_MSG_ACCENT_MEDIUM)
                        .hoverEvent(HoverEvent.showText(
                                text("See the combined total for everyone on your server").color(factory.MSG_HOVER))))
                .append(text(" | ").color(factory.INFO_MSG_ACCENT_MEDIUM))
                .append(text("top").color(factory.INFO_MSG_ACCENT_MEDIUM)
                        .hoverEvent(HoverEvent.showText(
                                text("See the top").color(factory.MSG_HOVER).append(space())
                                        .append(text(listSize)))))
                .append(newline())
                .append(factory.arrow()).append(space())
                .append(text("player-name").color(factory.INFO_MSG_ACCENT_MEDIUM)
                        .hoverEvent(HoverEvent.showText(
                                text("In case you typed").color(factory.MSG_HOVER).append(space())
                                        .append(text("\"player\"").color(factory.INFO_MSG_ACCENT_MEDIUM))
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