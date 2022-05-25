package com.gmail.artemis.the.gr8.playerstats.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;

public class ComponentFactory {

    public static TextComponent helpMsg() {
        TextComponent spaces = text("    ");
        TextComponent underscores = text("____________").color(TextColor.fromHexString("#6E3485"));
        TextComponent arrow = text("→ ").color(NamedTextColor.GOLD);

        //the builder
        TextComponent helpMsg = Component.newline()
                .append(underscores).append(spaces).append(text(OutputFormatter.getPluginPrefix())).append(spaces).append(underscores)
                .append(newline())
                .append(text("Hover over the arguments for more information!").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                .append(newline())
                .append(text("Usage: ").color(NamedTextColor.GOLD)).append(text("/statistic").color(NamedTextColor.YELLOW))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("name").color(NamedTextColor.YELLOW)
                        .hoverEvent(HoverEvent.showText(text("The name of the statistic").color(TextColor.fromHexString("#FFD52B"))
                                .append(newline())
                                .append(text("Example: ").color(TextColor.fromHexString("#FFD52B")))
                                .append(text("\"mine_block\"").color(NamedTextColor.YELLOW)))))
                .append(newline())
                .append(spaces).append(arrow)
                .append(text("sub-statistic"));

        return helpMsg;
    }

    /*

    public BaseComponent[] formatHelpSpigot() {
        String spaces = "    ";
        String underscores = "____________";

        ComponentBuilder underscore = new ComponentBuilder(underscores).color(net.md_5.bungee.api.ChatColor.of("#6E3485"));
        TextComponent arrow = new TextComponent("→ ");
        arrow.setColor(net.md_5.bungee.api.ChatColor.GOLD);

        TextComponent statName = new TextComponent("name");
        statName.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        statName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text("The name of the statistic (Example: \"mine_block\")")));

        TextComponent subStatName = new TextComponent("sub-statistic");
        subStatName.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        subStatName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text("Some statistics require an item, block or entity as sub-statistic (example: \"mine_block diorite\")")));

        TextComponent target = new TextComponent("me | player | top");
        target.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        target.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text("Choose whether you want to see your own statistic, another player's, or the top " + config.getTopListMaxSize())));

        TextComponent playerName = new TextComponent("player-name");
        playerName.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        playerName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text("In case you selected \"player\", specify the player's name here")));

        ComponentBuilder help = new ComponentBuilder()
                .append("\n").append(underscore.create()).append(spaces).append(pluginPrefix).append(spaces).append(underscore.create()).append("\n")
                .append("Hover over the arguments for more information!").color(net.md_5.bungee.api.ChatColor.GRAY).italic(true).append("\n")
                .append("Usage: ").color(net.md_5.bungee.api.ChatColor.GOLD).italic(false)
                .append("/statistic ").color(net.md_5.bungee.api.ChatColor.YELLOW).append("\n")
                .append(spaces).append(arrow).append(statName).append("\n").reset()
                .append(spaces).append(arrow).append(subStatName).append("\n").reset()
                .append(spaces).append(arrow).append(target).append("\n").reset()
                .append(spaces).append(arrow).append(playerName);

        return help.create();
    }

     */
}
