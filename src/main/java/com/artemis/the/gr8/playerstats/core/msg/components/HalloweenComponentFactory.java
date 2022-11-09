package com.artemis.the.gr8.playerstats.core.msg.components;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public final class HalloweenComponentFactory extends ComponentFactory {


    public HalloweenComponentFactory() {
        super();
    }

    @Override
    public TextComponent pluginPrefixAsTitle() {
        return miniMessageToComponent(
                "<gradient:#ff9300:#f74040:#f73b3b:#ff9300:#f74040:#ff9300>" +
                "<white>\u2620</white> __________    [PlayerStats]    __________ " +
                "<white>\u2620</white></gradient>");
    }

    @Override
    public TextComponent pluginPrefix() {
        return miniMessageToComponent(
                "<gradient:#f74040:gold:#f74040>[PlayerStats]</gradient>");
    }

    @Override
    public TextComponent sharerName(String sharerName) {
        return miniMessageToComponent(decorateWithRandomGradient(sharerName));
    }

    private @NotNull String decorateWithRandomGradient(@NotNull String input) {
        Random random = new Random();
        String colorString = switch (random.nextInt(6)) {
            case 0 -> "<gradient:#fcad23:red>";
            case 1 -> "<gradient:#fcad23:#f967b2:#F79438:#ffe30f>";
            case 2 -> "<gradient:red:#fcad23:red>";
            case 3 -> "<gradient:#f28e30:#f5cb42:#f74040>";
            case 4 -> "<gradient:#F79438:#f967b2>";
            case 5 -> "<gradient:#f967b2:#fcad23:#f967b2>";
            default -> "<gradient:#fcad23:#f967b2:#F74040>";
        };
        return colorString + input + "</gradient>";
    }
}