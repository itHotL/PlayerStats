package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.enums.DebugLevel;
import com.gmail.artemis.the.gr8.playerstats.enums.PluginColor;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.enums.Unit;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
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
 into formatted Components with TextColor and TextDecoration, or return empty Components
 or ComponentBuilders with the desired formatting.*/
public class ComponentFactory {

    private static ConfigHandler config;

    protected static TextColor PREFIX;  //gold
    protected static TextColor BRACKETS;  //gray
    protected static TextColor UNDERSCORE;  //dark_purple
    protected static TextColor MSG_MAIN;  //blue
    protected static TextColor MSG_MAIN_2;  //gold
    protected static TextColor MSG_ACCENT;  //medium_gold
    protected static TextColor MSG_ACCENT_2;  //light_yellow
    protected static TextColor HOVER_MSG;  //light_blue
    protected static TextColor HOVER_ACCENT;  //light_gold

    public ComponentFactory(ConfigHandler c) {
        config = c;
        prepareColors();

        MyLogger.logMsg("Regular ComponentFactory created!", DebugLevel.MEDIUM);
    }

    protected void prepareColors() {
        PREFIX = PluginColor.GOLD.getColor();
        BRACKETS = PluginColor.GRAY.getColor();
        UNDERSCORE = PluginColor.DARK_PURPLE.getColor();
        MSG_MAIN = PluginColor.MEDIUM_BLUE.getColor();
        MSG_MAIN_2 = PluginColor.GOLD.getColor();
        MSG_ACCENT = PluginColor.MEDIUM_GOLD.getColor();
        MSG_ACCENT_2 = PluginColor.LIGHT_YELLOW.getColor();
        HOVER_MSG = PluginColor.LIGHT_BLUE.getColor();
        HOVER_ACCENT = PluginColor.LIGHT_GOLD.getColor();
    }

    public TextColor prefix() {
        return PREFIX;
    }
    public TextColor brackets() {
        return BRACKETS;
    }
    public TextColor underscore() {
        return UNDERSCORE;
    }
    public TextColor msgMain() {
        return MSG_MAIN;
    }
    public TextColor msgMain2() {
        return MSG_MAIN_2;
    }
    public TextColor msgAccent() {
        return MSG_ACCENT;
    }
    public TextColor msgAccent2() {
        return MSG_ACCENT_2;
    }
    public TextColor hoverMsg() {
        return HOVER_MSG;
    }
    public TextColor hoverAccent() {
        return HOVER_ACCENT;
    }


    /** Returns [PlayerStats]. */
    public TextComponent pluginPrefixComponent() {
        return text("[")
                .color(BRACKETS)
                .append(text("PlayerStats").color(PREFIX))
                .append(text("]"));
    }

    /** Returns [PlayerStats] surrounded by underscores on both sides. */
    public TextComponent prefixTitleComponent() {
        //12 underscores for both console and in-game
        return text("____________").color(UNDERSCORE)
                .append(text("    "))  //4 spaces
                .append(pluginPrefixComponent())
                .append(text("    "))  //4 spaces
                .append(text("____________"));
    }

    /** Returns a TextComponent with the input String as content, with color Gray and decoration Italic.*/
    public TextComponent subTitleComponent(String content) {
        return text(content).color(BRACKETS).decorate(TextDecoration.ITALIC);
    }

    /** Returns a TextComponents in the style of a default plugin message, with color Medium_Blue. */
    public TextComponent messageComponent() {
        return text().color(MSG_MAIN).build();
    }

    public TextComponent messageAccentComponent() {
        return text().color(MSG_ACCENT).build();
    }

    public TextComponent.Builder playerNameBuilder(String playerName, Target selection) {
        return getComponentBuilder(playerName,
                getColorFromString(config.getPlayerNameDecoration(selection, false)),
                getStyleFromString(config.getPlayerNameDecoration(selection, true)));
    }

    /** @param prettyStatName a statName with underscores removed and each word capitalized
     @param prettySubStatName if present, a subStatName with underscores removed and each word capitalized*/
    public TextComponent statNameTextComponent(String prettyStatName, @Nullable String prettySubStatName, Target selection) {
        TextComponent.Builder totalStatNameBuilder =  getComponentBuilder(prettyStatName,
                getColorFromString(config.getStatNameDecoration(selection, false)),
                getStyleFromString(config.getStatNameDecoration(selection, true)));
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
                    getColorFromString(config.getSubStatNameDecoration(selection, false)),
                    getStyleFromString(config.getSubStatNameDecoration(selection, true)))
                            .append(text("("))
                            .append(text(prettySubStatName))
                            .append(text(")"))
                    .build();
        }
    }

    /** Returns a TextComponent with TranslatableComponent as a child.*/
    public TextComponent statNameTransComponent(String statKey, String subStatKey, Target selection) {
        TextComponent.Builder totalStatNameBuilder = getComponentBuilder(null,
                getColorFromString(config.getStatNameDecoration(selection, false)),
                getStyleFromString(config.getStatNameDecoration(selection, true)));

        TextComponent subStat = subStatNameTransComponent(subStatKey, selection);
        if (statKey.equalsIgnoreCase("stat_type.minecraft.killed")) {
            return totalStatNameBuilder.append(killEntityBuilder(subStat)).build();
        }
        else if (statKey.equalsIgnoreCase("stat_type.minecraft.killed_by")) {
            return totalStatNameBuilder.append(entityKilledByBuilder(subStat)).build();
        }
        else {
            totalStatNameBuilder.append(translatable().key(statKey));
            if (!subStat.equals(Component.empty())) {
                totalStatNameBuilder.append(
                        space().decorations(TextDecoration.NAMES.values(), false)
                                .append(subStat));
            }
            return totalStatNameBuilder.build();
        }
    }

    /** Returns a TranslatableComponent for the subStatName, or an empty component.*/
    private TextComponent subStatNameTransComponent(String subStatKey, Target selection) {
        if (subStatKey != null) {
            return getComponentBuilder(null,
                    getColorFromString(config.getSubStatNameDecoration(selection, false)),
                    getStyleFromString(config.getSubStatNameDecoration(selection, true)))
                    .append(text("("))
                    .append(translatable()
                            .key(subStatKey))
                    .append(text(")"))
                    .build();
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

    public TextComponent statNumberComponent(String prettyNumber, Target selection) {
        return getComponent(prettyNumber,
                getColorFromString(config.getStatNumberDecoration(selection, false)),
                getStyleFromString(config.getStatNumberDecoration(selection, true)));
    }

    public TextComponent damageNumberHoverComponent(String mainNumber, String hoverNumber, TextComponent heart, Target selection) {
        return statNumberHoverComponent(mainNumber, hoverNumber, null, null, heart, selection);
    }

    public TextComponent statNumberHoverComponent(String mainNumber, String hoverNumber, @Nullable String hoverUnitName, @Nullable String hoverUnitKey, Target selection) {
        return statNumberHoverComponent(mainNumber, hoverNumber, hoverUnitName, hoverUnitKey, null, selection);
    }

    private TextComponent statNumberHoverComponent(String mainNumber, String hoverNumber, @Nullable String hoverUnitName, @Nullable String hoverUnitKey, @Nullable TextComponent heart, Target selection) {
        TextColor baseColor = getColorFromString(config.getStatNumberDecoration(selection, false));
        TextDecoration style = getStyleFromString(config.getStatNumberDecoration(selection, true));

        TextComponent.Builder hoverText = getComponentBuilder(hoverNumber, getLighterColor(baseColor), style);
        if (heart != null) {
            hoverText.append(space())
                    .append(heart);
        }
        else if (hoverUnitKey != null) {
            hoverText.append(space())
                    .append(translatable().key(hoverUnitKey));
        }
        else if (hoverUnitName != null) {
            hoverText.append(space())
                    .append(text(hoverUnitName));
        }
        return getComponent(mainNumber, baseColor, style).hoverEvent(HoverEvent.showText(hoverText));
    }

    public TextComponent statUnitComponent(String unitName, String unitKey, Target selection) {
        if (!(unitName == null && unitKey == null)) {
            TextComponent.Builder statUnitBuilder = getComponentBuilder(null,
                    getColorFromString(config.getSubStatNameDecoration(selection, false)),
                    getStyleFromString(config.getSubStatNameDecoration(selection, true)))
                    .append(text("["));
            if (unitKey != null) {
                statUnitBuilder.append(translatable()
                        .key(unitKey));
            } else {
                statUnitBuilder.append(text(unitName));
            }
            return statUnitBuilder.append(text("]")).build();
        }
        else {
            return Component.empty();
        }
    }

    public TextComponent heartComponent(boolean isConsoleSender, boolean isHoverUnit) {
        TextColor heartColor = TextColor.fromHexString("#FF1313");
        char heart = isConsoleSender ? '\u2665' : '\u2764';
        if (isHoverUnit) {
            return Component.text(heart).color(heartColor);
        }
        TextComponent.Builder heartComponent = Component.text()
                .content(String.valueOf(heart))
                .color(heartColor);
        if (config.useHoverText()) {
            heartComponent.hoverEvent(HoverEvent.showText(
                    text(Unit.HEART.getLabel())
                            .color(HOVER_ACCENT)
                            .decorate(TextDecoration.ITALIC)));
        }
        return Component.text().color(BRACKETS)
                .append(text("["))
                .append(heartComponent)
                .append(text("]"))
                .build();
    }

    public TextComponent titleComponent(String content, Target selection) {
        return getComponent(content,
                getColorFromString(config.getTitleDecoration(selection, false)),
                getStyleFromString(config.getTitleDecoration(selection, true)));
    }

    public TextComponent titleNumberComponent(int number) {
        return getComponent(number + "",
                getColorFromString(config.getTitleNumberDecoration(false)),
                getStyleFromString(config.getTitleNumberDecoration(true)));
    }

    public TextComponent serverNameComponent(String serverName) {
        TextComponent colon = text(":").color(getColorFromString(config.getServerNameDecoration(false)));
        return getComponent(serverName,
                getColorFromString(config.getServerNameDecoration(false)),
                getStyleFromString(config.getServerNameDecoration(true)))
                .append(colon);
    }

    public TextComponent rankingNumberComponent(String number) {
        return getComponent(number,
                getColorFromString(config.getRankNumberDecoration(false)),
                getStyleFromString(config.getRankNumberDecoration(true)));
    }

    public TextComponent.Builder dotsBuilder() {
        return getComponentBuilder(null,
                getColorFromString(config.getDotsDecoration(false)),
                getStyleFromString(config.getDotsDecoration(true)));
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
        float multiplier = (float) ((100 - config.getHoverTextAmountLighter()) / 100.0);
        HSVLike oldColor = HSVLike.fromRGB(color.red(), color.green(), color.blue());
        HSVLike newColor = HSVLike.hsvLike(oldColor.h(), oldColor.s() * multiplier, oldColor.v());
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