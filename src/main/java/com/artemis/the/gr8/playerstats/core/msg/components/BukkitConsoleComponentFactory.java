package com.artemis.the.gr8.playerstats.core.msg.components;

import com.artemis.the.gr8.playerstats.core.enums.PluginColor;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;

/**
 * The {@link ComponentFactory} that is used to build messages for
 * a Bukkit Console. Bukkit consoles don't support hex colors,
 * unlike Paper consoles.
 */
public class BukkitConsoleComponentFactory extends ComponentFactory {

    public BukkitConsoleComponentFactory() {
        super();
    }

    @Override
    protected void prepareColors() {
        PREFIX = PluginColor.GOLD.getConsoleColor();
        BRACKETS = PluginColor.GRAY.getConsoleColor();
        UNDERSCORE = PluginColor.DARK_PURPLE.getConsoleColor();
        HEARTS = PluginColor.RED.getConsoleColor();

        FEEDBACK_MSG = PluginColor.LIGHTEST_BLUE.getConsoleColor();
        FEEDBACK_MSG_ACCENT = PluginColor.LIGHT_BLUE.getConsoleColor();

        INFO_MSG = PluginColor.GOLD.getConsoleColor();
        INFO_MSG_ACCENT_1 = PluginColor.MEDIUM_GOLD.getConsoleColor();
        INFO_MSG_ACCENT_2 = PluginColor.LIGHT_YELLOW.getConsoleColor();

        MSG_HOVER = PluginColor.LIGHTEST_BLUE.getConsoleColor();
        MSG_CLICKED = PluginColor.LIGHT_PURPLE.getConsoleColor();
        MSG_HOVER_ACCENT = PluginColor.LIGHT_GOLD.getConsoleColor();
    }

    @Override
    public boolean isConsoleFactory() {
        return true;
    }

    @Override
    public TextComponent heart() {
        return text()
                .content(String.valueOf('\u2665'))
                .color(HEARTS)
                .build();
    }

    @Override
    public TextComponent arrow() {
        return text("->").color(INFO_MSG);
    }

    @Override
    public TextComponent bulletPoint() {
        return text("*").color(INFO_MSG);
    }

    @Override
    protected TextComponent getComponent(String content, @NotNull TextColor color, @Nullable TextDecoration style) {
        return getComponentBuilder(content, NamedTextColor.nearestTo(color), style).build();
    }

    @Override
    protected TextComponent.Builder getComponentBuilder(@Nullable String content, @NotNull TextColor color, @Nullable TextDecoration style) {
        TextComponent.Builder builder = text()
                .decorations(TextDecoration.NAMES.values(), false)
                .color(NamedTextColor.nearestTo(color));
        if (content != null) {
            builder.append(text(content));
        }
        if (style != null) {
            builder.decorate(style);
        }
        return builder;
    }

    @Override
    protected TextColor getHexColor(String hexColor) {
        TextColor hex = TextColor.fromHexString(hexColor);
        return hex != null ? NamedTextColor.nearestTo(hex) : NamedTextColor.WHITE;
    }
}