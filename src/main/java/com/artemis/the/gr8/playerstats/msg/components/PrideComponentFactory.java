package com.artemis.the.gr8.playerstats.msg.components;

import com.artemis.the.gr8.playerstats.config.ConfigHandler;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static net.kyori.adventure.text.Component.*;

/**
 * A festive version of the {@link ComponentFactory}
 */
public class PrideComponentFactory extends ComponentFactory {

    private final Random random;

    public PrideComponentFactory(ConfigHandler config) {
        super(config);
        random = new Random();
    }

    @Override
    public TextComponent getExampleName() {
        return deserialize("<gradient:#f74040:gold:#FF6600:#f74040>Artemis_the_gr8</gradient>");
    }

    @Override
    public TextComponent sharerName(String sharerName) {
        return deserialize(decorateWithRandomGradient(sharerName));
    }

    @Override
    //12 underscores
    public TextComponent pluginPrefixAsTitle() {
        return deserialize("<rainbow:16>____________    [PlayerStats]    ____________</rainbow>");
    }

    @Override
    public TextComponent pluginPrefix() {
        if (random.nextBoolean()) {
            return backwardsPluginPrefixComponent();
        }
        return rainbowPrefix();
    }

    public TextComponent rainbowPrefix() {
        return deserialize("<#f74040>[</#f74040>" +
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
        return deserialize("<#631ae6>[</#631ae6>" +
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
        String colorString = switch (random.nextInt(9)) {
            case 0 -> "<gradient:#03b6fc:#f73bdb>";
            case 1 -> "<gradient:#14f7a0:#4287f5>";
            case 2 -> "<gradient:#a834eb:#f511da:#ad09ed>";
            case 3 -> "<gradient:#FF6600:#f73bdb:#F7F438>";
            case 4 -> "<gradient:#309de6:#a834eb>";
            case 5 -> "<gradient:#F7F438:#fcad23:#FF6600>";
            case 6 -> "<gradient:#309de6:#F7F438>";
            case 7 -> "<gradient:#F79438:#F7389B>";
            default -> "<gradient:#F7F438:#309de6>";
        };
        return colorString + input + "</gradient>";
    }

    @Contract("_ -> new")
    private @NotNull TextComponent deserialize(String input) {
        return text()
                .append(MiniMessage.miniMessage().deserialize(input))
                .build();
    }
}