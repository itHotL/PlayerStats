package com.artemis.the.gr8.playerstats.core.msg.components;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * A festive version of the {@link ComponentFactory}
 */
public final class PrideComponentFactory extends ComponentFactory {

    public PrideComponentFactory() {
        super();
    }

    @Override
    public TextComponent getExampleName() {
        return miniMessageToComponent("<gradient:#f74040:gold:#FF6600:#f74040>Artemis_the_gr8</gradient>");
    }

    @Override
    public TextComponent sharerName(String sharerName) {
        return miniMessageToComponent(decorateWithRandomGradient(sharerName));
    }

    @Override
    //12 underscores
    public TextComponent pluginPrefixAsTitle() {
        return miniMessageToComponent("<rainbow:16>____________    [PlayerStats]    ____________</rainbow>");
    }

    @Override
    public TextComponent pluginPrefix() {
        return miniMessageToComponent("<#f74040>[</#f74040>" +
                "<#F54D39>P</#F54D39>" +
                "<#F16E28>l</#F16E28>" +
                "<#ee8a19>a</#ee8a19>" +
                "<#EEA019>y</#EEA019>" +
                "<#F7C522>e</#F7C522>" +
                "<#C1DA15>r</#C1DA15>" +
                "<#84D937>S</#84D937>" +
                "<#46D858>t</#46D858>" +
                "<#01c1a7>a</#01c1a7>" +
                "<#1F8BEB>t</#1F8BEB>" +
                "<#3341E6>s</#3341E6>" +
                "<#631ae6>]</#631ae6>");
    }

    private @NotNull String decorateWithRandomGradient(@NotNull String input) {
        Random random = new Random();
        String colorString = switch (random.nextInt(8)) {
            case 0 -> "<gradient:#03b6fc:#f854df>";
            case 1 -> "<gradient:#14f7a0:#4287f5>";
            case 2 -> "<gradient:#f971ae:#fcad23>";
            case 3 -> "<gradient:#309de6:#af45ed>";
            case 4 -> "<gradient:#f971ae:#af45ed:#4287f5>";
            case 5 -> "<gradient:#FFEA40:#fcad23:#F79438>";
            case 6 -> "<gradient:#309de6:#01c1a7:#F7F438>";
            case 7 -> "<gradient:#F79438:#f967b2>";
            default -> "<gradient:#F7F438:#01c1a7>";
        };
        return colorString + input + "</gradient>";
    }
}