package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
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
import org.bukkit.Statistic;
import org.bukkit.map.MinecraftFont;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.kyori.adventure.text.Component.*;

public class MessageFactory {

    private static ConfigHandler config;
    private final LanguageKeyHandler language;

    private final TextColor msgColor;  //my favorite shade of light blue, somewhere between blue and aqua
    private final TextColor hoverBaseColor;  //light blue - one shade lighter than msgColor
    private final TextColor accentColor1;  //gold - one shade lighter than standard gold
    private final TextColor accentColor2;  //yellow - a few shades darker than standard yellow

    public MessageFactory(ConfigHandler c, LanguageKeyHandler l) {
        config = c;
        language = l;

        msgColor = TextColor.fromHexString("#55AAFF");
        hoverBaseColor = TextColor.fromHexString("#55C6FF");
        accentColor1 = TextColor.fromHexString("#FFB80E");
        accentColor2 = TextColor.fromHexString("#FFD52B");
    }

    protected TextComponent pluginPrefix(boolean isConsoleSender) {
        return text("[")
                .color(NamedTextColor.GRAY)
                .append(text("PlayerStats").color(NamedTextColor.GOLD))
                .append(text("]"))
                .append(space());
    }

    public TextComponent reloadedConfig(boolean isConsoleSender) {
        return pluginPrefix(isConsoleSender)
                .append(text("Config reloaded!")
                        .color(msgColor));
    }

    public TextComponent stillReloading(boolean isConsoleSender) {
        return pluginPrefix(isConsoleSender)
                .append(text("The plugin is still (re)loading, your request will be processed when it is done!")
                        .color(msgColor));
    }

    public TextComponent partiallyReloaded(boolean isConsoleSender) {
        return pluginPrefix(isConsoleSender)
                .append(text("The reload process was interrupted. If you notice unexpected behavior, please reload PlayerStats again to fix it!")
                        .color(msgColor));
    }

    public TextComponent waitAMoment(boolean longWait, boolean isConsoleSender) {
        return longWait ? pluginPrefix(isConsoleSender)
                .append(text("Calculating statistics, this may take a minute...")
                        .color(msgColor))
                : pluginPrefix(isConsoleSender)
                .append(text("Calculating statistics, this may take a few moments...")
                        .color(msgColor));
    }

    public TextComponent formatExceptions(@NotNull String exception, boolean isConsoleSender) {
        return pluginPrefix(isConsoleSender)
                .append(text(exception)
                        .color(msgColor));
    }

    public TextComponent missingStatName(boolean isConsoleSender) {
        return pluginPrefix(isConsoleSender)
                .append(text("Please provide a valid statistic name!")
                        .color(msgColor));
    }

    public TextComponent missingSubStatName(Statistic.Type statType, boolean isConsoleSender) {
        return pluginPrefix(isConsoleSender)
                .append(text("Please add a valid ")
                        .append(text(getSubStatTypeName(statType)))
                        .append(text(" to look up this statistic!"))
                        .color(msgColor));
    }

    public TextComponent missingPlayerName(boolean isConsoleSender) {
        return pluginPrefix(isConsoleSender)
                .append(text("Please specify a valid player-name!")
                        .color(msgColor));
    }

    public TextComponent wrongSubStatType(Statistic.Type statType, String subStatEntry, boolean isConsoleSender) {
        return pluginPrefix(isConsoleSender)
                .append(text("\"")
                        .append(text(subStatEntry))
                        .append(text("\""))
                        .append(text(" is not a valid "))
                        .append(text(getSubStatTypeName(statType)))
                        .append(text("!"))
                        .color(msgColor));
    }

    public TextComponent unknownError(boolean isConsoleSender) {
        return pluginPrefix(isConsoleSender)
                .append(text("Something went wrong with your request, please try again or see /statistic for a usage explanation!")
                        .color(msgColor));
    }

    public TextComponent helpMsg(boolean isConsoleSender) {
        if (!isConsoleSender) {
            return config.useHoverText() ? helpMsgHover() : helpMsgPlain(false);
        }
        else {
            return helpMsgPlain(true);
        }
    }

    public TextComponent usageExamples(boolean isConsoleSender) {
        TextComponent spaces = text("    "); //4 spaces
        TextComponent arrow = text("→ ").color(NamedTextColor.GOLD);
        TextColor accentColor = TextColor.fromHexString("#FFE339");

        if (isConsoleSender && Bukkit.getName().equalsIgnoreCase("CraftBukkit")) {
            arrow = text("-> ").color(NamedTextColor.GOLD);
            accentColor = NamedTextColor.YELLOW;
        }

        return Component.newline()
                .append(getPrefixAsTitle(isConsoleSender))
                .append(newline())
                .append(text("Examples: ").color(NamedTextColor.GOLD))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("/statistic animals_bred top").color(accentColor))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("/statistic mine_block diorite me").color(accentColor))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("/statistic deaths player Artemis_the_gr8").color(accentColor))
                .append(newline());
    }

    public TextComponent formatPlayerStat(int stat, @NotNull StatRequest request) {
        if (!request.isValid()) return unknownError(request.isConsoleSender());
        return Component.text()
                .append(playerNameComponent(Target.PLAYER, request.getPlayerName() + ": "))
                .append(statNumberComponent(Target.PLAYER, stat))
                .append(space())
                .append(statNameComponent(request))
                .append(space())
                .build();
    }

    public TextComponent formatTopStats(@NotNull LinkedHashMap<String, Integer> topStats, @NotNull StatRequest request) {
        if (!request.isValid()) return unknownError(request.isConsoleSender());

        TextComponent.Builder topList = Component.text();
        topList.append(getTopStatTitle(topStats.size(), request));

        boolean useDots = config.useDots();
        Set<String> playerNames = topStats.keySet();
        MinecraftFont font = new MinecraftFont();

        int count = 0;
        for (String playerName : playerNames) {
            count = count+1;

            topList.append(newline())
                    .append(rankingNumberComponent(count + ". "))
                    .append(playerNameComponent(Target.TOP, playerName));

            if (useDots) {
                topList.append(space());

                int dots = (int) Math.round((130.0 - font.getWidth(count + ". " + playerName))/2);
                if (request.isConsoleSender()) {
                    dots = (int) Math.round((130.0 - font.getWidth(count + ". " + playerName))/6) + 7;
                }
                else if (config.playerNameIsBold()) {
                    dots = (int) Math.round((130.0 - font.getWidth(count + ". ") - (font.getWidth(playerName) * 1.19))/2);
                }
                if (dots >= 1) {
                    topList.append(dotsComponent(".".repeat(dots)));
                }
            }
            else {
                topList.append(playerNameComponent(Target.TOP, ":"));
            }
            topList.append(space()).append(statNumberComponent(Target.TOP, topStats.get(playerName)));
        }
        return topList.build();
    }

    public TextComponent formatServerStat(long stat, @NotNull StatRequest request) {
        if (!request.isValid()) return unknownError(request.isConsoleSender());
        return Component.text()
                .append(titleComponent(Target.SERVER, config.getServerTitle()))
                .append(space())
                .append(serverNameComponent())
                .append(space())
                .append(statNumberComponent(Target.SERVER, stat))
                .append(space())
                .append(statNameComponent(request))
                .append(space())
                .build();
    }

    protected TextComponent getPrefixAsTitle(boolean isConsoleSender) {
        String underscores = "____________";  //12 underscores for both console and in-game
        TextColor underscoreColor = TextColor.fromHexString("#6E3485");  //a dark shade of purple

        if (isConsoleSender && Bukkit.getName().equalsIgnoreCase("CraftBukkit")) {
            underscoreColor = NamedTextColor.DARK_PURPLE;
        }

        return text(underscores).color(underscoreColor)
                .append(text("    "))  //4 spaces
                .append(pluginPrefix(isConsoleSender))
                .append(text("   "))  //3 spaces (since prefix already has one)
                .append(text(underscores));
    }

    protected TextComponent getTopStatTitle(int topLength, @NotNull StatRequest request) {
        return Component.text()
                .append(newline())
                .append(pluginPrefix(request.isConsoleSender()))
                .append(titleComponent(Target.TOP, config.getTopStatsTitle()))
                .append(space())
                .append(titleNumberComponent(topLength))
                .append(space())
                .append(statNameComponent(request))
                .append(space())
                .build();
    }

    protected TextComponent playerNameComponent(Target selection, String playerName) {
        return getComponent(playerName,
                getColorFromString(config.getPlayerNameFormatting(selection, false)),
                getStyleFromString(config.getPlayerNameFormatting(selection, true)));
    }

    protected TranslatableComponent statNameComponent(@NotNull StatRequest request) {
        if (request.getStatistic() == null) return null;
        TextColor statNameColor = getColorFromString(config.getStatNameFormatting(request.getSelection(), false));
        TextDecoration statNameStyle = getStyleFromString(config.getStatNameFormatting(request.getSelection(), true));

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
        TextComponent subStat = subStatNameComponent(request.getSelection(), subStatName);
        TranslatableComponent.Builder totalName;

        if (statName.equalsIgnoreCase("stat_type.minecraft.killed") && subStat != null) {
            totalName = killEntityComponent(subStat);
        }
        else if (statName.equalsIgnoreCase("stat_type.minecraft.killed_by") && subStat != null) {
            totalName = entityKilledByComponent(subStat);
        }
        else {
            totalName = translatable().key(statName);
            if (subStat != null) totalName.append(space()).append(subStat);
        }

        if (statNameStyle != null) totalName.decoration(statNameStyle, TextDecoration.State.TRUE);
        return totalName
                .color(statNameColor)
                .build();
    }

    protected @Nullable TextComponent subStatNameComponent(Target selection, @Nullable String subStatName) {
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
    private TranslatableComponent.Builder killEntityComponent(@NotNull TextComponent subStat) {
        return translatable()
                .key("commands.kill.success.single")  //"Killed %s"
                .args(subStat);
    }

    /** Construct a custom translation for entity_killed_by with the language keys for stat.minecraft.deaths
     ("Number of Deaths") and book.byAuthor ("by %s").
     @return a TranslatableComponent Builder with stat.minecraft.deaths as key, with a ChildComponent
     with book.byAuthor as key and the subStat Component as args.*/
    private TranslatableComponent.Builder entityKilledByComponent(@NotNull TextComponent subStat) {
        return translatable()
                .key("stat.minecraft.deaths")  //"Number of Deaths"
                .append(space())
                .append(translatable()
                        .key("book.byAuthor") //"by %s"
                        .args(subStat));
    }

    protected TextComponent statNumberComponent(Target selection, long number) {
        return getComponent(NumberFormatter.format(number),
                getColorFromString(config.getStatNumberFormatting(selection, false)),
                getStyleFromString(config.getStatNumberFormatting(selection, true)));
    }

    protected TextComponent titleComponent(Target selection, String content) {
        return getComponent(content,
                getColorFromString(config.getTitleFormatting(selection, false)),
                getStyleFromString(config.getTitleFormatting(selection, true)));
    }

    protected TextComponent titleNumberComponent(int number) {
        return getComponent(number + "",
                getColorFromString(config.getTitleNumberFormatting(false)),
                getStyleFromString(config.getTitleNumberFormatting(true)));
    }

    protected TextComponent serverNameComponent() {
        TextComponent colon = text(":").color(getColorFromString(config.getServerNameFormatting(false)));
        return getComponent(config.getServerName(),
                getColorFromString(config.getServerNameFormatting(false)),
                getStyleFromString(config.getServerNameFormatting(true)))
                .append(colon);
    }

    protected TextComponent rankingNumberComponent(String number) {
        return getComponent(number,
                getColorFromString(config.getRankNumberFormatting(false)),
                getStyleFromString(config.getRankNumberFormatting(true)));
    }

    protected TextComponent dotsComponent(String dots) {
        return getComponent(dots,
                getColorFromString(config.getDotsFormatting(false)),
                getStyleFromString(config.getDotsFormatting(true)));
    }

    private TextComponent getComponent(String content, TextColor color, @Nullable TextDecoration style) {
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

    /** Returns "block", "entity", "item", or "sub-statistic" if the provided Type is null. */
    private String getSubStatTypeName(Statistic.Type statType) {
        String subStat = "sub-statistic";
        if (statType == null) return subStat;
        switch (statType) {
            case BLOCK -> subStat = "block";
            case ENTITY -> subStat = "entity";
            case ITEM -> subStat = "item";
        }
        return subStat;
    }

    /** Returns the usage-explanation with hovering text */
    private @NotNull TextComponent helpMsgHover() {
        TextComponent spaces = text("    "); //4 spaces
        TextComponent arrow = text("→ ").color(NamedTextColor.GOLD);  //alt + 26
        TextColor arguments = NamedTextColor.YELLOW;

        return Component.newline()
                .append(getPrefixAsTitle(false))
                .append(newline())
                .append(text("Hover over the arguments for more information!").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                .append(newline())
                .append(text("Usage: ").color(NamedTextColor.GOLD)).append(text("/statistic").color(arguments))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("name").color(arguments)
                        .hoverEvent(HoverEvent.showText(text("The name that describes the statistic").color(hoverBaseColor)
                                .append(newline())
                                .append(text("Example: ").color(accentColor1))
                                .append(text("\"animals_bred\"").color(accentColor2)))))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("sub-statistic").color(arguments)
                        .hoverEvent(HoverEvent.showText(
                                text("Some statistics need an item, block or entity as extra input").color(hoverBaseColor)
                                        .append(newline())
                                        .append(text("Example: ").color(accentColor1)
                                                .append(text("\"mine_block diorite\"").color(accentColor2))))))
                .append(newline())
                .append(spaces)
                .append(text("→").color(NamedTextColor.GOLD)
                        .hoverEvent(HoverEvent.showText(
                                text("Choose one").color(TextColor.fromHexString("#6E3485")))))
                .append(space())
                .append(text("me").color(arguments)
                        .hoverEvent(HoverEvent.showText(
                                text("See your own statistic").color(hoverBaseColor))))
                .append(text(" | ").color(arguments))
                .append(text("player").color(arguments)
                        .hoverEvent(HoverEvent.showText(
                                text("Choose any player that has played on your server").color(hoverBaseColor))))
                .append(text(" | ").color(arguments))
                .append(text("server").color(arguments)
                        .hoverEvent(HoverEvent.showText(
                                text("See the combined total for everyone on your server").color(hoverBaseColor))))
                .append(text(" | ").color(arguments))
                .append(text("top").color(arguments)
                        .hoverEvent(HoverEvent.showText(
                                text("See the top ").color(hoverBaseColor)
                                        .append(text(config.getTopListMaxSize()).color(hoverBaseColor)))))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("player-name").color(arguments)
                        .hoverEvent(HoverEvent.showText(
                                text("In case you typed ").color(hoverBaseColor)
                                        .append(text("\"player\"").color(accentColor2)
                                                .append(text(", add the player's name").color(hoverBaseColor))))));
    }

    /** Returns the usage-explanation without any hovering text.
    If BukkitVersion is CraftBukkit, this doesn't use unicode symbols or hex colors */
    private @NotNull TextComponent helpMsgPlain(boolean isConsoleSender) {
        TextComponent spaces = text("    "); //4 spaces
        TextComponent arrow = text("→ ").color(NamedTextColor.GOLD); //alt + 26;
        TextComponent bullet = text("• ").color(NamedTextColor.GOLD); //alt + 7
        TextColor arguments = NamedTextColor.YELLOW;
        TextColor accentColor = accentColor2;

        if (isConsoleSender && Bukkit.getName().equalsIgnoreCase("CraftBukkit")) {
            arrow = text("-> ").color(NamedTextColor.GOLD);
            bullet = text("* ").color(NamedTextColor.GOLD);
            accentColor = NamedTextColor.GOLD;
        }

        return Component.newline()
                .append(getPrefixAsTitle(isConsoleSender))
                .append(newline())
                .append(text("Type \"/statistic examples\" to see examples!").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                .append(newline())
                .append(text("Usage: ").color(NamedTextColor.GOLD))
                .append(text("/statistic").color(arguments))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("name").color(arguments))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("{sub-statistic}").color(arguments))
                .append(space())
                .append(text("(a block, item or entity)").color(NamedTextColor.GRAY))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("me | player | server | top").color(arguments))
                .append(newline())
                .append(spaces).append(spaces).append(bullet)
                .append(text("me:").color(accentColor))
                .append(space()).append(text("your own statistic").color(NamedTextColor.GRAY))
                .append(newline())
                .append(spaces).append(spaces).append(bullet)
                .append(text("player:").color(accentColor))
                .append(space()).append(text("choose a player").color(NamedTextColor.GRAY))
                .append(newline())
                .append(spaces).append(spaces).append(bullet)
                .append(text("server:").color(accentColor))
                .append(space()).append(text("everyone on the server combined").color(NamedTextColor.GRAY))
                .append(newline())
                .append(spaces).append(spaces).append(bullet)
                .append(text("top:").color(accentColor))
                .append(space()).append(text("the top").color(NamedTextColor.GRAY)
                        .append(space()).append(text(config.getTopListMaxSize())))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("{player-name}").color(arguments));
    }
}