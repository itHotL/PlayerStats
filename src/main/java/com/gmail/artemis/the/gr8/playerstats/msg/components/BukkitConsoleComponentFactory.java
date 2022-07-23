package com.gmail.artemis.the.gr8.playerstats.msg.components;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.enums.PluginColor;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;

/** The {@link ComponentFactory} that is used to build messages for a Bukkit Console.
 Bukkit consoles don't support hex colors, unlike Paper consoles.*/
public class BukkitConsoleComponentFactory extends ComponentFactory {

    public BukkitConsoleComponentFactory(ConfigHandler config) {
        super(config);
    }

    @Override
    protected void prepareColors() {
        PREFIX = PluginColor.GOLD.getConsoleColor();
        BRACKETS = PluginColor.GRAY.getConsoleColor();
        UNDERSCORE = PluginColor.DARK_PURPLE.getConsoleColor();

        MSG_MAIN = PluginColor.MEDIUM_BLUE.getConsoleColor();
        MSG_ACCENT = PluginColor.BLUE.getConsoleColor();

        MSG_MAIN_2 = PluginColor.GOLD.getConsoleColor();
        MSG_ACCENT_2A = PluginColor.MEDIUM_GOLD.getConsoleColor();
        MSG_ACCENT_2B = PluginColor.LIGHT_YELLOW.getConsoleColor();

        MSG_HOVER = PluginColor.LIGHT_BLUE.getConsoleColor();
        MSG_CLICKED = PluginColor.LIGHT_PURPLE.getConsoleColor();
        MSG_HOVER_ACCENT = PluginColor.LIGHT_GOLD.getConsoleColor();
    }

    @Override
    public TextColor getSharerNameColor() {
        return PluginColor.NAME_5.getConsoleColor();
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