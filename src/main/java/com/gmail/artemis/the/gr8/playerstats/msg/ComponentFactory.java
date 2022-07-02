package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.enums.PluginColor;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.LanguageKeyHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.util.HSVLike;
import net.kyori.adventure.util.Index;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.Component.text;

/** Creates Components with the desired formatting. This class can put Strings
 into formatted Components with TextColor and TextDecoration, and turn
 certain Strings into appropriate LanguageKeys to return a TranslatableComponent.*/
public class ComponentFactory {

    private static ConfigHandler config;
    private final LanguageKeyHandler languageKeyHandler;
    private final NumberFormatter format;

    public ComponentFactory(ConfigHandler c) {
        config = c;

        languageKeyHandler = new LanguageKeyHandler();
        format = new NumberFormatter(config);
    }

    /** Returns [PlayerStats]. */
    public TextComponent pluginPrefixComponent(boolean isBukkitConsole) {
        return text("[")
                .color(PluginColor.GRAY.getColor())
                .append(text("PlayerStats").color(PluginColor.GOLD.getColor()))
                .append(text("]"));
    }

    /** Returns [PlayerStats] surrounded by underscores on both sides. */
    public TextComponent prefixTitleComponent(boolean isBukkitConsole) {
        String underscores = "____________";  //12 underscores for both console and in-game
        TextColor underscoreColor = isBukkitConsole ?
                PluginColor.DARK_PURPLE.getConsoleColor() : PluginColor.DARK_PURPLE.getColor();

        return text(underscores).color(underscoreColor)
                .append(text("    "))  //4 spaces
                .append(pluginPrefixComponent(isBukkitConsole))
                .append(text("    "))  //4 spaces
                .append(text(underscores));
    }

    /** Returns a TextComponent with the input String as content, with color Gray and decoration Italic.*/
    public TextComponent subTitleComponent(String content) {
        return text(content).color(PluginColor.GRAY.getColor()).decorate(TextDecoration.ITALIC);
    }

    /** Returns a TextComponents that represents a full message, with [PlayerStats] prepended. */
    public TextComponent messageComponent() {
        return text().color(PluginColor.MEDIUM_BLUE.getColor()).build();
    }

    public TextComponent.Builder playerNameBuilder(String playerName, Target selection) {
        return getComponentBuilder(playerName,
                getColorFromString(config.getPlayerNameFormatting(selection, false)),
                getStyleFromString(config.getPlayerNameFormatting(selection, true)));
    }

    /** @param prettyStatName a statName with underscores removed and each word capitalized
     @param prettySubStatName if present, a subStatName with underscores removed and each word capitalized*/
    public TextComponent statNameTextComponent(String prettyStatName, @Nullable String prettySubStatName, Target selection) {
        TextComponent.Builder totalStatNameBuilder =  getComponentBuilder(prettyStatName,
                getColorFromString(config.getStatNameFormatting(selection, false)),
                getStyleFromString(config.getStatNameFormatting(selection, true)));
        TextComponent subStat = subStatNameTextComponent(prettySubStatName, selection);

        if (!subStat.equals(Component.empty())) {
                totalStatNameBuilder
                        .append(space().decorations(TextDecoration.NAMES.values(), false))
                        .append(subStatNameTextComponent(prettySubStatName, selection));
        }
        return totalStatNameBuilder.build();
    }

    /** Returns a TextComponent for the subStatName, or an empty component.*/
    private TextComponent subStatNameTextComponent(@Nullable String prettySubStatName, Target selection) {
        if (prettySubStatName == null) {
            return Component.empty();
        } else {
            return getComponentBuilder(null,
                    getColorFromString(config.getSubStatNameFormatting(selection, false)),
                    getStyleFromString(config.getSubStatNameFormatting(selection, true)))
                            .append(text("("))
                            .append(text(prettySubStatName))
                            .append(text(")"))
                    .build();
        }
    }

    /** Returns a TextComponent with TranslatableComponent as a child.*/
    public TextComponent statNameTransComponent(@NotNull StatRequest request) {
        TextComponent.Builder totalStatNameBuilder = getComponentBuilder(null,
                getColorFromString(config.getStatNameFormatting(request.getSelection(), false)),
                getStyleFromString(config.getStatNameFormatting(request.getSelection(), true)));
        TextComponent subStat = subStatNameTransComponent(request);

        String statName = languageKeyHandler.getStatKey(request.getStatistic());
        if (statName.equalsIgnoreCase("stat_type.minecraft.killed")) {
            return totalStatNameBuilder.append(killEntityBuilder(subStat)).build();
        }
        else if (statName.equalsIgnoreCase("stat_type.minecraft.killed_by")) {
            return totalStatNameBuilder.append(entityKilledByBuilder(subStat)).build();
        }
        else {
            totalStatNameBuilder.append(translatable().key(statName));
            if (!subStat.equals(Component.empty())) {
                totalStatNameBuilder.append(
                        space().decorations(TextDecoration.NAMES.values(), false)
                                .append(subStat));
            }
            return totalStatNameBuilder.build();
        }
    }

    /** Returns a TranslatableComponent for the subStatName, or an empty component.*/
    private TextComponent subStatNameTransComponent(@NotNull StatRequest request) {
        if (request.getSubStatEntry() != null) {
            String subStatName = request.getSubStatEntry();
            switch (request.getStatistic().getType()) {
                case BLOCK -> subStatName = languageKeyHandler.getBlockKey(request.getBlock());
                case ENTITY -> subStatName = languageKeyHandler.getEntityKey(request.getEntity());
                case ITEM -> subStatName = languageKeyHandler.getItemKey(request.getItem());
                default -> {
                }
            }
            if (subStatName != null) {
                return getComponentBuilder(null,
                        getColorFromString(config.getSubStatNameFormatting(request.getSelection(), false)),
                        getStyleFromString(config.getSubStatNameFormatting(request.getSelection(), true)))
                        .append(text("("))
                        .append(translatable()
                                .key(subStatName))
                        .append(text(")"))
                        .build();
            }
        }
        return Component.empty();
    }

    /** Construct a custom translation for kill_entity with the language key for commands.kill.success.single ("Killed %s").
     @return a TranslatableComponent Builder with the subStat Component as args.*/
    private TranslatableComponent.Builder killEntityBuilder(@NotNull TextComponent subStat) {
        return translatable()
                .key("commands.kill.success.single")  //"Killed %s"
                .args(subStat);
    }

    /** Construct a custom translation for entity_killed_by with the language keys for stat.minecraft.deaths
     ("Number of Deaths") and book.byAuthor ("by %s").
     @return a TranslatableComponent Builder with stat.minecraft.deaths as key, with a ChildComponent
     with book.byAuthor as key and the subStat Component as args.*/
    private TranslatableComponent.Builder entityKilledByBuilder(@NotNull TextComponent subStat) {
        return translatable()
                .key("stat.minecraft.deaths")  //"Number of Deaths"
                .append(space())
                .append(translatable()
                        .key("book.byAuthor") //"by %s"
                        .args(subStat));
    }

    //TODO Make this dark gray (or at least darker than statNumber, and at least for time statistics)
    public TextComponent statUnitComponent(String statName, Target selection) {
        if (!statName.toLowerCase().contains("one_cm") && !statName.toLowerCase().contains("damage")) {
            return Component.empty();
        }
        String key;
        switch (config.getDistanceUnit()) {
            case CM -> key = "cm";
            case KM -> key = "km";
            case MILE -> key = "Miles";
            default -> key = config.useTranslatableComponents() ? languageKeyHandler.getDistanceKey() : "Blocks";
        }
        return getComponentBuilder(null,
                getColorFromString(config.getSubStatNameFormatting(selection, false)),
                getStyleFromString(config.getSubStatNameFormatting(selection, true)))
                .append(text("["))
                        .append(translatable(key))
                .append(text("]"))
                .build();
    }

    //TODO Add hoverComponent with full number
    public TextComponent statNumberComponent(long number, String statName, Target selection) {
        TextColor baseColor = getColorFromString(config.getStatNumberFormatting(selection, false));
        TextDecoration style = getStyleFromString(config.getStatNumberFormatting(selection, true));
        TextComponent.Builder statNumber = getComponentBuilder(format.formatMainNumber(statName, number), baseColor, style);

        if (config.useHoverText()) {
            statNumber.hoverEvent(HoverEvent.showText(getComponent(format.formatHoverNumber(statName, number),
                    getLighterColor(baseColor), style)
                    .append(space())
                    .append(statUnitComponent(statName, selection))));
        }
        return statNumber.build();
    }

    public TextComponent titleComponent(String content, Target selection) {
        return getComponent(content,
                getColorFromString(config.getTitleFormatting(selection, false)),
                getStyleFromString(config.getTitleFormatting(selection, true)));
    }

    public TextComponent titleNumberComponent(int number) {
        return getComponent(number + "",
                getColorFromString(config.getTitleNumberFormatting(false)),
                getStyleFromString(config.getTitleNumberFormatting(true)));
    }

    public TextComponent serverNameComponent(String serverName) {
        TextComponent colon = text(":").color(getColorFromString(config.getServerNameFormatting(false)));
        return getComponent(serverName,
                getColorFromString(config.getServerNameFormatting(false)),
                getStyleFromString(config.getServerNameFormatting(true)))
                .append(colon);
    }

    public TextComponent rankingNumberComponent(String number) {
        return getComponent(number,
                getColorFromString(config.getRankNumberFormatting(false)),
                getStyleFromString(config.getRankNumberFormatting(true)));
    }

    public TextComponent.Builder dotsBuilder() {
        return getComponentBuilder(null,
                getColorFromString(config.getDotsFormatting(false)),
                getStyleFromString(config.getDotsFormatting(true)));
    }

    private TextComponent getComponent(String content, TextColor color, @Nullable TextDecoration style) {
        return getComponentBuilder(content, color, style).build();
    }

    private TextComponent.Builder getComponentBuilder(@Nullable String content, TextColor color, @Nullable TextDecoration style) {
        TextComponent.Builder builder = text()
                .decorations(TextDecoration.NAMES.values(), false)
                .color(color);
        if (content != null) {
            builder.append(text(content));
        }
        if (style != null) {
            builder.decorate(style);
        }
        return builder;
    }

    private TextColor getColorFromString(String configString) {
        if (configString != null) {
            try {
                if (configString.contains("#")) {
                    return TextColor.fromHexString(configString);
                }
                else {
                    return getTextColorByName(configString);
                }
            }
            catch (IllegalArgumentException | NullPointerException exception) {
                Bukkit.getLogger().warning(exception.toString());
            }
        }
        return null;
    }

    private TextColor getTextColorByName(String textColor) {
        Index<String, NamedTextColor> names = NamedTextColor.NAMES;
        return names.value(textColor);
    }

    private TextColor getLighterColor(TextColor color) {
        HSVLike oldColor = HSVLike.fromRGB(color.red(), color.green(), color.blue());
        HSVLike newColor = HSVLike.hsvLike(oldColor.h(), 0.45F, oldColor.v());
        return TextColor.color(newColor);
    }

    private @Nullable TextDecoration getStyleFromString(@NotNull String configString) {
        if (configString.equalsIgnoreCase("none")) {
            return null;
        }
        else if (configString.equalsIgnoreCase("magic")) {
            return TextDecoration.OBFUSCATED;
        }
        else {
            Index<String, TextDecoration> styles = TextDecoration.NAMES;
            return styles.value(configString);
        }
    }
}