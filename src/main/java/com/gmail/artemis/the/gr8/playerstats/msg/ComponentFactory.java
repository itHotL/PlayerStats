package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.enums.PluginColor;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import com.gmail.artemis.the.gr8.playerstats.utils.NumberFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.util.Index;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.Component.text;

/** Constructs Components with  */
public class ComponentFactory {

    private static ConfigHandler config;
    private final LanguageKeyHandler language;

    public ComponentFactory(ConfigHandler c, LanguageKeyHandler l) {
        config = c;
        language = l;
    }

    /** Returns [PlayerStats] followed by a single space. */
    public TextComponent pluginPrefix(boolean isBukkitConsole) {
        return text("[")
                .color(PluginColor.GRAY.getColor())
                .append(text("PlayerStats").color(PluginColor.GOLD.getColor()))
                .append(text("]"))
                .append(space());
    }

    /** Returns [PlayerStats] surrounded by underscores on both sides. */
    public TextComponent prefixTitle(boolean isBukkitConsole) {
        String underscores = "____________";  //12 underscores for both console and in-game
        TextColor underscoreColor = isBukkitConsole ?
                PluginColor.DARK_PURPLE.getConsoleColor() : PluginColor.DARK_PURPLE.getColor();

        return text(underscores).color(underscoreColor)
                .append(text("    "))  //4 spaces
                .append(pluginPrefix(isBukkitConsole))
                .append(text("   "))  //3 spaces (since prefix already has one)
                .append(text(underscores));
    }

    /** Returns a TextComponent with the input String as content, with color Gray and decoration Italic.*/
    public TextComponent subTitle(String content) {
        return text(content).color(PluginColor.GRAY.getColor()).decorate(TextDecoration.ITALIC);
    }

    /** Returns a TextComponents that represents a full message, with [PlayerStats] prepended. */
    public TextComponent msg(String msg, boolean isBukkitConsole) {
        return pluginPrefix(isBukkitConsole)
                .append(text(msg)
                        .color(PluginColor.MEDIUM_BLUE.getColor()));
    }

    /** Returns a plain TextComponent that represents a single message line.
     A space will be inserted after part1, part2 and part3.
     Each message part has its own designated color.
     @param part1 color DARK_GOLD
     @param part2 color MEDIUM_GOLD
     @param part3 color YELLOW
     @param part4 color GRAY
     */
    public TextComponent msgPart(@Nullable String part1, @Nullable String part2, @Nullable String part3, @Nullable String part4) {
        return msgPart(part1, part2, part3, part4, false);
    }

    /** Returns a plain TextComponent that represents a single message line.
     A space will be inserted after part1, part2 and part3.
     Each message part has its own designated color.
     if isBukkitConsole is true, the colors will be the nearest ChatColor to the below colors.
     @param part1 color DARK_GOLD
     @param part2 color MEDIUM_GOLD
     @param part3 color YELLOW
     @param part4 color GRAY
     */
    public TextComponent msgPart(@Nullable String part1, @Nullable String part2, @Nullable String part3, @Nullable String part4, boolean isBukkitConsole) {
        TextComponent.Builder msg = Component.text();
        if (part1 != null) {
            TextColor pluginColor = isBukkitConsole ? PluginColor.GOLD.getConsoleColor() : PluginColor.GOLD.getColor();
            msg.append(text(part1)
                            .color(pluginColor))
                    .append(space());
        }
        if (part2 != null) {
            TextColor pluginColor = isBukkitConsole ? PluginColor.MEDIUM_GOLD.getConsoleColor() : PluginColor.MEDIUM_GOLD.getColor();
            msg.append(text(part2)
                            .color(pluginColor))
                    .append(space());
        }
        if (part3 != null) {
            TextColor pluginColor = isBukkitConsole ? PluginColor.LIGHT_GOLD.getConsoleColor() : PluginColor.LIGHT_GOLD.getColor();
            msg.append(text(part3)
                            .color(pluginColor))
                    .append(space());
        }
        if (part4 != null) {
            TextColor pluginColor = isBukkitConsole ? PluginColor.GRAY.getConsoleColor() : PluginColor.GRAY.getColor();
            msg.append(text(part4)
                    .color(pluginColor));
        }
        return msg.build();
    }

    /** Returns a TextComponent with a single line of hover-text in the specified color.
     @param plainText the base message
     @param hoverText the hovering text
     @param hoverColor color of the hovering text */
    public TextComponent simpleHoverPart(String plainText, String hoverText, PluginColor hoverColor) {
        return simpleHoverPart(plainText, null, hoverText, hoverColor);
    }

    /** Returns a TextComponent with a single line of hover-text in the specified color.
     If a PluginColor is provided for the plainText, the base color is set as well.
     @param plainText the base message
     @param plainColor color of the base message
     @param hoverText the hovering text
     @param hoverColor color of the hovering text */
    public TextComponent simpleHoverPart(String plainText, @Nullable PluginColor plainColor, String hoverText, PluginColor hoverColor) {
        TextComponent.Builder msg = Component.text()
                .append(text(plainText))
                .hoverEvent(HoverEvent.showText(
                        text(hoverText)
                                .color(hoverColor.getColor())));
        if (plainColor != null) {
            msg.color(plainColor.getColor());
        }
        return msg.build();
    }

    /** Returns a TextComponent with hover-text that can consist of three different parts,
     divided over two different lines. Each part has its own designated color. If all the
     input Strings are null, it will return an empty Component.
     @param plainText the non-hovering part
     @param color the color for the non-hovering part
     @param hoverLineOne text on the first line, with color LIGHT_BLUE
     @param hoverLineTwoA text on the second line, with color GOLD
     @param hoverLineTwoB text on the second part of the second line, with color LIGHT_GOLD
     */
    public TextComponent complexHoverPart(@NotNull String plainText, @NotNull PluginColor color, String hoverLineOne, String hoverLineTwoA, String hoverLineTwoB) {
        TextComponent base = Component.text(plainText).color(color.getColor());
        TextComponent.Builder hoverText = Component.text();
        if (hoverLineOne != null) {
            hoverText.append(text(hoverLineOne)
                    .color(PluginColor.LIGHT_BLUE.getColor()));
            if (hoverLineTwoA != null || hoverLineTwoB != null) {
                hoverText.append(newline());
            }
        }
        if (hoverLineTwoA != null) {
            hoverText.append(text(hoverLineTwoA)
                    .color(PluginColor.GOLD.getColor()));
            if (hoverLineTwoB != null) {
                hoverText.append(space());
            }
        }
        if (hoverLineTwoB != null) {
            hoverText.append(text(hoverLineTwoB).color(PluginColor.LIGHT_GOLD.getColor()));
        }
        return base.hoverEvent(HoverEvent.showText(hoverText.build()));
    }


    public TextComponent playerName(String playerName, Target selection) {
        return createComponent(playerName,
                getColorFromString(config.getPlayerNameFormatting(selection, false)),
                getStyleFromString(config.getPlayerNameFormatting(selection, true)));
    }

    public TranslatableComponent statName(@NotNull StatRequest request) {
        String statName = request.getStatistic().name();
        String subStatName = request.getSubStatEntry();
        if (!config.useTranslatableComponents()) {
            statName = getPrettyName(statName);
            subStatName = getPrettyName(subStatName);
        }
        else {
            statName = language.getStatKey(request.getStatistic());
            switch (request.getStatistic().getType()) {
                case BLOCK -> subStatName = language.getBlockKey(request.getBlock());
                case ENTITY -> subStatName = language.getEntityKey(request.getEntity());
                case ITEM -> subStatName = language.getItemKey(request.getItem());
                case UNTYPED -> {
                }
            }
        }
       return statName(statName, subStatName, request.getSelection());
    }

    private TranslatableComponent statName(@NotNull String statKey, String subStatKey, @NotNull Target selection) {
        TranslatableComponent.Builder totalName;
        TextComponent subStat = subStatName(subStatKey, selection);
        TextColor statNameColor = getColorFromString(config.getStatNameFormatting(selection, false));
        TextDecoration statNameStyle = getStyleFromString(config.getStatNameFormatting(selection, true));

        if (statKey.equalsIgnoreCase("stat_type.minecraft.killed") && subStat != null) {
            totalName = killEntity(subStat);
        }
        else if (statKey.equalsIgnoreCase("stat_type.minecraft.killed_by") && subStat != null) {
            totalName = entityKilledBy(subStat);
        }
        else {
            totalName = translatable().key(statKey);
            if (subStat != null) totalName.append(space()).append(subStat);
        }

        if (statNameStyle != null) totalName.decoration(statNameStyle, TextDecoration.State.TRUE);
        return totalName
                .color(statNameColor)
                .build();
    }

    private @Nullable TextComponent subStatName(@Nullable String subStatName, Target selection) {
        if (subStatName != null) {
            TextDecoration style = getStyleFromString(config.getSubStatNameFormatting(selection, true));
            TextComponent.Builder subStat = text()
                    .append(text("("))
                    .append(translatable()
                            .key(subStatName))
                    .append(text(")"))
                    .color(getColorFromString(config.getSubStatNameFormatting(selection, false)));

            subStat.decorations(TextDecoration.NAMES.values(), false);
            if (style != null) subStat.decoration(style, TextDecoration.State.TRUE);
            return subStat.build();
        }
        else {
            return null;
        }
    }

    /** Construct a custom translation for kill_entity with the language key for commands.kill.success.single ("Killed %s").
     @return a TranslatableComponent Builder with the subStat Component as args.*/
    private TranslatableComponent.Builder killEntity(@NotNull TextComponent subStat) {
        return translatable()
                .key("commands.kill.success.single")  //"Killed %s"
                .args(subStat);
    }

    /** Construct a custom translation for entity_killed_by with the language keys for stat.minecraft.deaths
     ("Number of Deaths") and book.byAuthor ("by %s").
     @return a TranslatableComponent Builder with stat.minecraft.deaths as key, with a ChildComponent
     with book.byAuthor as key and the subStat Component as args.*/
    private TranslatableComponent.Builder entityKilledBy(@NotNull TextComponent subStat) {
        return translatable()
                .key("stat.minecraft.deaths")  //"Number of Deaths"
                .append(space())
                .append(translatable()
                        .key("book.byAuthor") //"by %s"
                        .args(subStat));
    }

    public TextComponent statNumber(long number, Target selection) {
        return createComponent(NumberFormatter.format(number),
                getColorFromString(config.getStatNumberFormatting(selection, false)),
                getStyleFromString(config.getStatNumberFormatting(selection, true)));
    }

    public TextComponent title(String content, Target selection) {
        return createComponent(content,
                getColorFromString(config.getTitleFormatting(selection, false)),
                getStyleFromString(config.getTitleFormatting(selection, true)));
    }

    public TextComponent titleNumber(int number) {
        return createComponent(number + "",
                getColorFromString(config.getTitleNumberFormatting(false)),
                getStyleFromString(config.getTitleNumberFormatting(true)));
    }

    public TextComponent serverName(String serverName) {
        TextComponent colon = text(":").color(getColorFromString(config.getServerNameFormatting(false)));
        return createComponent(serverName,
                getColorFromString(config.getServerNameFormatting(false)),
                getStyleFromString(config.getServerNameFormatting(true)))
                .append(colon);
    }

    public TextComponent rankingNumber(String number) {
        return createComponent(number,
                getColorFromString(config.getRankNumberFormatting(false)),
                getStyleFromString(config.getRankNumberFormatting(true)));
    }

    public TextComponent dots(String dots) {
        return createComponent(dots,
                getColorFromString(config.getDotsFormatting(false)),
                getStyleFromString(config.getDotsFormatting(true)));
    }

    private TextComponent createComponent(String content, TextColor color, @Nullable TextDecoration style) {
        return style == null ? text(content).color(color) : text(content).color(color).decoration(style, TextDecoration.State.TRUE);
    }

    /** Replace "_" with " " and capitalize each first letter of the input.
     @param input String to prettify, case-insensitive*/
    private String getPrettyName(String input) {
        if (input == null) return null;
        StringBuilder capitals = new StringBuilder(input.toLowerCase());
        capitals.setCharAt(0, Character.toUpperCase(capitals.charAt(0)));
        while (capitals.indexOf("_") != -1) {
            MyLogger.replacingUnderscores();

            int index = capitals.indexOf("_");
            capitals.setCharAt(index + 1, Character.toUpperCase(capitals.charAt(index + 1)));
            capitals.setCharAt(index, ' ');
        }
        return capitals.toString();
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