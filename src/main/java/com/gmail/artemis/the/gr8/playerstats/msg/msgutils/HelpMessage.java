package com.gmail.artemis.the.gr8.playerstats.msg.msgutils;

import com.gmail.artemis.the.gr8.playerstats.enums.PluginColor;
import com.gmail.artemis.the.gr8.playerstats.msg.ComponentFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class HelpMessage implements TextComponent {

    private final ComponentFactory componentFactory;
    private final TextComponent helpMessage;
    boolean isBukkitConsole;
    TextColor GRAY;
    TextColor DARK_PURPLE;
    TextColor GOLD;
    TextColor MEDIUM_GOLD;
    TextColor LIGHT_GOLD;
    TextColor LIGHT_BLUE;


    public HelpMessage(ComponentFactory componentFactory, boolean useHover, boolean isBukkitConsole, int listSize) {
        this.componentFactory = componentFactory;
        this.isBukkitConsole = isBukkitConsole;
        getPluginColors(isBukkitConsole);

        if (!useHover || isBukkitConsole) {
            helpMessage = getPlainHelpMsg(isBukkitConsole, listSize);
        } else {
            helpMessage = helpMsgHover(listSize);
        }
    }

    private TextComponent getPlainHelpMsg(boolean isBukkitConsole, int listSize) {
        String arrowSymbol = isBukkitConsole ? "->" : "→";  //alt + 26
        String bulletSymbol = isBukkitConsole ? "*" : "•";  //alt + 7
        TextComponent spaces = text("    "); //4 spaces
        TextComponent arrow = text(arrowSymbol).color(NamedTextColor.GOLD);
        TextComponent bullet = text(bulletSymbol).color(NamedTextColor.GOLD);

        return Component.newline()
                .append(componentFactory.prefixTitleComponent(isBukkitConsole))
                .append(newline())
                .append(text("Type \"/statistic examples\" to see examples!").color(GRAY).decorate(TextDecoration.ITALIC))
                .append(newline())
                .append(text("Usage:").color(GOLD)).append(space())
                .append(text("/statistic").color(LIGHT_GOLD))
                .append(newline())
                .append(spaces).append(arrow).append(space())
                .append(text("name").color(LIGHT_GOLD))
                .append(newline())
                .append(spaces).append(arrow).append(space())
                .append(text("{sub-statistic}").color(LIGHT_GOLD)).append(space())
                .append(text("(a block, item or entity)").color(GRAY))
                .append(newline())
                .append(spaces).append(arrow).append(space())
                .append(text("me | player | server | top").color(LIGHT_GOLD))
                .append(newline())
                .append(spaces).append(spaces).append(bullet).append(space())
                .append(text("me:").color(MEDIUM_GOLD)).append(space())
                .append(text("your own statistic").color(GRAY))
                .append(newline())
                .append(spaces).append(spaces).append(bullet).append(space())
                .append(text("player:").color(MEDIUM_GOLD)).append(space())
                .append(text("choose a player").color(GRAY))
                .append(newline())
                .append(spaces).append(spaces).append(bullet).append(space())
                .append(text("server:").color(MEDIUM_GOLD)).append(space())
                .append(text("everyone on the server combined").color(GRAY))
                .append(newline())
                .append(spaces).append(spaces).append(bullet).append(space())
                .append(text("top:").color(MEDIUM_GOLD)).append(space())
                .append(text("the top").color(GRAY).append(space()).append(text(listSize)))
                .append(newline())
                .append(spaces).append(arrow).append(space())
                .append(text("{player-name}").color(LIGHT_GOLD));
    }

    private TextComponent helpMsgHover(int listSize) {
        TextComponent spaces = text("    ");
        TextComponent arrow = text("→").color(GOLD);

        return Component.newline()
                .append(componentFactory.prefixTitleComponent(false))
                .append(newline())
                .append(componentFactory.subTitleComponent("Hover over the arguments for more information!"))
                .append(newline())
                .append(text("Usage:").color(GOLD)).append(space())
                .append(text("/statistic").color(LIGHT_GOLD))
                .append(newline())
                .append(spaces).append(arrow).append(space())
                .append(text("name").color(LIGHT_GOLD)
                        .hoverEvent(HoverEvent.showText(text("The name that describes the statistic").color(LIGHT_BLUE)
                                .append(newline())
                                .append(text("Example: ").color(GOLD))
                                .append(text("\"animals_bred\"").color(LIGHT_GOLD)))))
                .append(newline())
                .append(spaces).append(arrow).append(space())
                .append(text("sub-statistic").color(LIGHT_GOLD)
                        .hoverEvent(HoverEvent.showText(
                                text("Some statistics need an item, block or entity as extra input").color(LIGHT_BLUE)
                                        .append(newline())
                                        .append(text("Example: ").color(GOLD)
                                                .append(text("\"mine_block diorite\"").color(LIGHT_GOLD))))))
                .append(newline())
                .append(spaces).append(arrow
                        .hoverEvent(HoverEvent.showText(
                                text("Choose one").color(DARK_PURPLE)))).append(space())
                .append(text("me").color(LIGHT_GOLD)
                        .hoverEvent(HoverEvent.showText(
                                text("See your own statistic").color(LIGHT_BLUE))))
                .append(text(" | ").color(LIGHT_GOLD))
                .append(text("player").color(LIGHT_GOLD)
                        .hoverEvent(HoverEvent.showText(
                                text("Choose any player that has played on your server").color(LIGHT_BLUE))))
                .append(text(" | ").color(LIGHT_GOLD))
                .append(text("server").color(LIGHT_GOLD)
                        .hoverEvent(HoverEvent.showText(
                                text("See the combined total for everyone on your server").color(LIGHT_BLUE))))
                .append(text(" | ").color(LIGHT_GOLD))
                .append(text("top").color(LIGHT_GOLD)
                        .hoverEvent(HoverEvent.showText(
                                text("See the top").color(LIGHT_BLUE).append(space())
                                        .append(text(listSize)))))
                .append(newline())
                .append(spaces).append(arrow).append(space())
                .append(text("player-name").color(LIGHT_GOLD)
                        .hoverEvent(HoverEvent.showText(
                                text("In case you typed").color(LIGHT_BLUE).append(space())
                                        .append(text("\"player\"").color(LIGHT_GOLD))
                                                .append(text(", add the player's name")))));
    }

    private void getPluginColors(boolean isBukkitConsole) {
        if (isBukkitConsole) {
            GRAY = PluginColor.GRAY.getConsoleColor();
            DARK_PURPLE = PluginColor.DARK_PURPLE.getConsoleColor();
            GOLD = PluginColor.GOLD.getConsoleColor();
            MEDIUM_GOLD = PluginColor.MEDIUM_GOLD.getConsoleColor();
            LIGHT_GOLD = PluginColor.LIGHT_GOLD.getConsoleColor();
            LIGHT_BLUE = PluginColor.LIGHT_BLUE.getConsoleColor();
        } else {
            GRAY = PluginColor.GRAY.getColor();
            DARK_PURPLE = PluginColor.DARK_PURPLE.getColor();
            GOLD = PluginColor.GOLD.getColor();
            MEDIUM_GOLD = PluginColor.MEDIUM_GOLD.getColor();
            LIGHT_GOLD = PluginColor.LIGHT_GOLD.getColor();
            LIGHT_BLUE = PluginColor.LIGHT_BLUE.getColor();
        }
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