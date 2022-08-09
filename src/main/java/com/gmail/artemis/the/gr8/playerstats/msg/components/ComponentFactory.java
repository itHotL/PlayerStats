package com.gmail.artemis.the.gr8.playerstats.msg.components;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.enums.PluginColor;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.enums.Unit;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageBuilder;
import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.LanguageKeyHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
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

/** Creates Components with the desired formatting for the {@link MessageBuilder} to build messages with.
 This class can put Strings into formatted Components with TextColor
 and TextDecoration, or return empty Components with the desired formatting.*/
public class ComponentFactory {

    private static ConfigHandler config;

    protected TextColor PREFIX;  //gold
    protected TextColor BRACKETS;  //gray
    protected TextColor UNDERSCORE;  //dark_purple
    protected TextColor HEARTS; //red

    protected TextColor MSG_MAIN;  //medium_blue
    protected TextColor MSG_ACCENT; //blue

    protected TextColor MSG_MAIN_2;  //gold
    protected TextColor MSG_ACCENT_2A;  //medium_gold
    protected TextColor MSG_ACCENT_2B;  //light_yellow

    protected TextColor MSG_HOVER;  //light_blue
    protected TextColor MSG_CLICKED;  //light_purple
    protected TextColor MSG_HOVER_ACCENT;  //light_gold


    public ComponentFactory(ConfigHandler c) {
        config = c;
        prepareColors();
    }

    protected void prepareColors() {
        PREFIX = PluginColor.GOLD.getColor();
        BRACKETS = PluginColor.GRAY.getColor();
        UNDERSCORE = PluginColor.DARK_PURPLE.getColor();
        HEARTS = PluginColor.RED.getColor();

        MSG_MAIN = PluginColor.MEDIUM_BLUE.getColor();
        MSG_ACCENT = PluginColor.BLUE.getColor();

        MSG_MAIN_2 = PluginColor.GOLD.getColor();
        MSG_ACCENT_2A = PluginColor.MEDIUM_GOLD.getColor();
        MSG_ACCENT_2B = PluginColor.LIGHT_YELLOW.getColor();

        MSG_HOVER = PluginColor.LIGHT_BLUE.getColor();
        MSG_HOVER_ACCENT = PluginColor.LIGHT_GOLD.getColor();
        MSG_CLICKED = PluginColor.LIGHT_PURPLE.getColor();
    }

    public TextColor getExampleNameColor() {
        return MSG_ACCENT_2B;
    }
    public TextColor getSharerNameColor() {
        return getColorFromString(config.getSharerNameDecoration(false));
    }

    /** Returns [PlayerStats]. */
    public TextComponent pluginPrefix() {
        return text("[")
                .color(BRACKETS)
                .append(text("PlayerStats").color(PREFIX))
                .append(text("]"));
    }

    /** Returns [PlayerStats] surrounded by underscores on both sides. */
    public TextComponent pluginPrefixAsTitle() {
        //12 underscores for both console and in-game
        return text("____________").color(UNDERSCORE)
                .append(text("    "))  //4 spaces
                .append(pluginPrefix())
                .append(text("    "))  //4 spaces
                .append(text("____________"));
    }

    /** Returns a TextComponent with the input String as content, with color Gray and decoration Italic.*/
    public TextComponent subTitle(String content) {
        return text(content).color(BRACKETS).decorate(TextDecoration.ITALIC);
    }

    /** Returns a TextComponents in the style of a default plugin message, with color Medium_Blue. */
    public TextComponent message() {
        return text().color(MSG_MAIN).build();
    }

    public TextComponent messageAccent() {
        return text().color(MSG_ACCENT).build();
    }

    public TextComponent title(String content, Target target) {
        return getComponent(content,
                getColorFromString(config.getTitleDecoration(target, false)),
                getStyleFromString(config.getTitleDecoration(target, true)));
    }

    public TextComponent titleNumber(int number) {
        return getComponent(number + "",
                getColorFromString(config.getTitleNumberDecoration(false)),
                getStyleFromString(config.getTitleNumberDecoration(true)));
    }

    public TextComponent rankNumber(int number) {
        return getComponent(number + ".",
                getColorFromString(config.getRankNumberDecoration(false)),
                getStyleFromString(config.getRankNumberDecoration(true)));
    }

    public TextComponent dots(String dots) {
        return getComponent(dots,
                getColorFromString(config.getDotsDecoration(false)),
                getStyleFromString(config.getDotsDecoration(true)));
    }

    public TextComponent serverName(String serverName) {
        TextComponent colon = text(":").color(getColorFromString(config.getServerNameDecoration(false)));
        return getComponent(serverName,
                getColorFromString(config.getServerNameDecoration(false)),
                getStyleFromString(config.getServerNameDecoration(true)))
                .append(colon);
    }

    public TextComponent playerName(String playerName, Target target) {
        return getComponent(playerName,
                getColorFromString(config.getPlayerNameDecoration(target, false)),
                getStyleFromString(config.getPlayerNameDecoration(target, true)));
    }

    public TextComponent sharerName(String sharerName) {
        return getComponent(sharerName,
                getSharerNameColor(),
                getStyleFromString(config.getSharerNameDecoration(true)));
    }

    public TextComponent shareButton(int shareCode) {
        return surroundWithBrackets(
                text("Share")
                        .color(MSG_HOVER)
                        .clickEvent(ClickEvent.runCommand("/statshare " + shareCode))
                        .hoverEvent(HoverEvent.showText(text("Click here to share this statistic in chat!")
                                .color(MSG_HOVER_ACCENT))));
    }

    public TextComponent sharedByMessage(Component playerName) {
        return surroundWithBrackets(
                text().append(
                                getComponent("Shared by",
                                        getColorFromString(config.getSharedByTextDecoration(false)),
                                        getStyleFromString(config.getSharedByTextDecoration(true))))
                        .append(space())
                        .append(playerName)
                        .build());
    }

    public TextComponent statResultInHoverText(TextComponent statResult) {
        return surroundWithBrackets(
                text().append(text("Hover Here")
                                .color(MSG_CLICKED)
                                .decorate(TextDecoration.ITALIC)
                                .hoverEvent(HoverEvent.showText(statResult)))
                        .build());
    }

    /** @param prettyStatName a statName with underscores removed and each word capitalized
     @param prettySubStatName if present, a subStatName with underscores removed and each word capitalized*/
    public TextComponent statAndSubStatName(String prettyStatName, @Nullable String prettySubStatName, Target target) {
        TextComponent.Builder totalStatNameBuilder =  getComponentBuilder(prettyStatName,
                getColorFromString(config.getStatNameDecoration(target, false)),
                getStyleFromString(config.getStatNameDecoration(target, true)));
        TextComponent subStat = subStatName(prettySubStatName, target);

        if (!subStat.equals(Component.empty())) {
                totalStatNameBuilder
                        .append(space().decorations(TextDecoration.NAMES.values(), false))
                        .append(subStatName(prettySubStatName, target));
        }
        return totalStatNameBuilder.build();
    }

    /** Returns a TextComponent with TranslatableComponent as a child.*/
    public TextComponent statAndSubStatNameTranslatable(String statKey, String subStatKey, Target target) {
        TextComponent.Builder totalStatNameBuilder = getComponentBuilder(null,
                getColorFromString(config.getStatNameDecoration(target, false)),
                getStyleFromString(config.getStatNameDecoration(target, true)));

        TextComponent subStat = subStatNameTranslatable(subStatKey, target);
        if (LanguageKeyHandler.isKeyForKillEntity(statKey)) {
            return totalStatNameBuilder.append(killEntityBuilder(subStat)).build();
        }
        else if (LanguageKeyHandler.isKeyForEntityKilledBy(statKey)) {
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

    public TextComponent statNumber(String prettyNumber, Target target) {
        return getComponent(prettyNumber,
                getColorFromString(config.getStatNumberDecoration(target, false)),
                getStyleFromString(config.getStatNumberDecoration(target, true)));
    }

    public TextComponent timeNumber(String prettyNumber, Target target) {
        return statNumber(prettyNumber, target);
    }

    public TextComponent timeNumberWithHoverText(String mainNumber, String hoverNumber, Target target) {
        return statNumberWithHoverText(mainNumber, hoverNumber, null, null, null, target);
    }

    public TextComponent damageNumber(String prettyNumber, Target target) {
        return statNumber(prettyNumber, target);
    }

    public TextComponent damageNumberWithHoverText(String mainNumber, String hoverNumber, String hoverUnitName, Target target) {
        return statNumberWithHoverText(mainNumber, hoverNumber, hoverUnitName, null, null, target);
    }

    public TextComponent damageNumberWithHeartUnitInHoverText(String mainNumber, String hoverNumber, Target target) {
        return statNumberWithHoverText(mainNumber, hoverNumber, null, null, clientHeart(true), target);
    }

    public TextComponent distanceNumber(String prettyNumber, Target target) {
        return statNumber(prettyNumber, target);
    }

    public TextComponent distanceNumberWithHoverText(String mainNumber, String hoverNumber, String hoverUnitName, Target target) {
        return statNumberWithHoverText(mainNumber, hoverNumber, hoverUnitName, null, null, target);
    }

    public TextComponent distanceNumberWithTranslatableHoverText(String mainNumber, String hoverNumber, String hoverUnitKey, Target target) {
        return statNumberWithHoverText(mainNumber, hoverNumber, null, hoverUnitKey, null, target);
    }

    public TextComponent statUnit(String unitName, Target target) {
        TextComponent statUnit = getComponentBuilder(unitName,
                getColorFromString(config.getSubStatNameDecoration(target, false)),
                getStyleFromString(config.getSubStatNameDecoration(target, true)))
                .build();
        return surroundWithBrackets(statUnit);
    }

    public TextComponent statUnitTranslatable(String unitKey, Target target) {
        TextComponent statUnit = getComponentBuilder(null,
                getColorFromString(config.getSubStatNameDecoration(target, false)),
                getStyleFromString(config.getSubStatNameDecoration(target, true)))
                .append(translatable()
                        .key(unitKey))
                .build();
        return surroundWithBrackets(statUnit);
    }

    public TextComponent clientHeart(boolean isDisplayedInHoverText) {
        TextComponent basicHeartComponent = basicHeartComponent('\u2764');
        if (isDisplayedInHoverText) {
            return basicHeartComponent;
        }
        return surroundWithBrackets(basicHeartComponent);
    }

    public TextComponent clientHeartWithHoverText() {
        TextComponent basicHeartComponent = basicHeartComponent('\u2764')
                .toBuilder()
                .hoverEvent(HoverEvent.showText(
                        text(Unit.HEART.getLabel())
                                .color(MSG_HOVER_ACCENT)))
                .build();
        return surroundWithBrackets(basicHeartComponent);
    }

    public TextComponent consoleHeart() {
        return surroundWithBrackets(basicHeartComponent('\u2665'));
    }

    //console can do u2665, u2764 looks better in-game
    private TextComponent basicHeartComponent(char heartChar) {
        return Component.text()
                .content(String.valueOf(heartChar))
                .color(HEARTS)
                .build();
    }

    /** Returns a TextComponent for the subStatName, or an empty component.*/
    private TextComponent subStatName(@Nullable String prettySubStatName, Target target) {
        if (prettySubStatName == null) {
            return Component.empty();
        } else {
            return getComponentBuilder(null,
                    getColorFromString(config.getSubStatNameDecoration(target, false)),
                    getStyleFromString(config.getSubStatNameDecoration(target, true)))
                    .append(text("("))
                    .append(text(prettySubStatName))
                    .append(text(")"))
                    .build();
        }
    }

    /** Returns a TranslatableComponent for the subStatName, or an empty component.*/
    private TextComponent subStatNameTranslatable(String subStatKey, Target target) {
        if (subStatKey != null) {
            return getComponentBuilder(null,
                    getColorFromString(config.getSubStatNameDecoration(target, false)),
                    getStyleFromString(config.getSubStatNameDecoration(target, true)))
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
                .key(LanguageKeyHandler.getAlternativeKeyForKillEntity())  //"Killed %s"
                .args(subStat);
    }

    /** Construct a custom translation for entity_killed_by with the language keys for stat.minecraft.deaths
     ("Number of Deaths") and book.byAuthor ("by %s").
     @return a TranslatableComponent Builder with stat.minecraft.deaths as key, with a ChildComponent
     with book.byAuthor as key and the subStat Component as args.*/
    private TranslatableComponent.Builder entityKilledByBuilder(@NotNull TextComponent subStat) {
        return translatable()
                .key(LanguageKeyHandler.getAlternativeKeyForEntityKilledBy())  //"Number of Deaths"
                .append(space())
                .append(translatable()
                        .key(LanguageKeyHandler.getAlternativeKeyForEntityKilledByArg()) //"by %s"
                        .args(subStat));
    }

    private TextComponent statNumberWithHoverText(String mainNumber, String hoverNumber,
                                                  @Nullable String hoverUnitName,
                                                  @Nullable String hoverUnitKey,
                                                  @Nullable TextComponent heartComponent, Target target) {

        TextColor baseColor = getColorFromString(config.getStatNumberDecoration(target, false));
        TextDecoration style = getStyleFromString(config.getStatNumberDecoration(target, true));

        TextComponent.Builder hoverText = getComponentBuilder(hoverNumber, getLighterColor(baseColor), style);
        if (heartComponent != null) {
            hoverText.append(space())
                    .append(heartComponent);
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

    private TextComponent surroundWithBrackets(TextComponent component) {
        return getComponent(null, BRACKETS, null)
                .append(text("["))
                .append(component)
                .append(text("]"));
    }

    protected TextComponent getComponent(String content, @NotNull TextColor color, @Nullable TextDecoration style) {
        return getComponentBuilder(content, color, style).build();
    }

    protected TextComponent.Builder getComponentBuilder(@Nullable String content, TextColor color, @Nullable TextDecoration style) {
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
                    return getHexColor(configString);
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

    protected TextColor getHexColor(String hexColor) {
        return TextColor.fromHexString(hexColor);
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