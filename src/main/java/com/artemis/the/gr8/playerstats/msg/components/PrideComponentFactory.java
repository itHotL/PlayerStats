package com.artemis.the.gr8.playerstats.msg.components;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * A festive version of the {@link ComponentFactory}
 */
public class PrideComponentFactory extends ComponentFactory {

    private final Random random;

    public PrideComponentFactory() {
        super();
        random = new Random();
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
        if (random.nextBoolean()) {
            return backwardsPluginPrefixComponent();
        }
        return rainbowPrefix();
    }

    public TextComponent rainbowPrefix() {
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

    @Contract(" -> new")
    private @NotNull TextComponent backwardsPluginPrefixComponent() {
        return miniMessageToComponent("<#631ae6>[</#631ae6>" +
                                "<#3341E6>P</#3341E6>" +
                                "<#1F8BEB>l</#1F8BEB>" +
                                "<#01c1a7>a</#01c1a7>" +
                                "<#46D858>y</#46D858>" +
                                "<#84D937>e</#84D937>" +
                                "<#C1DA15>r</#C1DA15>" +
                                "<#F7C522>S</#F7C522>" +
                                "<#EEA019>t</#EEA019>" +
                                "<#ee8a19>a</#ee8a19>" +
                                "<#f67824>t</#f67824>" +
                                "<#f76540>s</#f76540>" +
                                "<#f74040>]</#f74040>");
    }

    private @NotNull String decorateWithRandomGradient(@NotNull String input) {
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