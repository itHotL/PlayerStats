package com.artemis.the.gr8.playerstats.msg.components;

import com.artemis.the.gr8.playerstats.config.ConfigHandler;

import com.artemis.the.gr8.playerstats.msg.msgutils.EasterEggProvider;
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

    public PrideComponentFactory(ConfigHandler config) {
        super(config);
    }

    @Override
    public TextComponent getExampleName() {
        return text()
                .append(EasterEggProvider.getFestiveName("Artemis_the_gr8"))
                .build();
    }

    @Override
    public TextComponent sharerName(String sharerName) {
        return text()
                .append(EasterEggProvider.getFestiveName(sharerName))
                .build();
    }

    @Override
    public TextComponent pluginPrefixAsTitle() {
        String title = "<rainbow:16>____________    [PlayerStats]    ____________</rainbow>"; //12 underscores
        return text()
                .append(MiniMessage.miniMessage().deserialize(title))
                .build();
    }

    @Override
    public TextComponent pluginPrefix() {
        Random randomizer = new Random();
        if (randomizer.nextBoolean()) {
            return backwardsPluginPrefixComponent();
        }
        return rainbowPrefix();
    }

    public TextComponent rainbowPrefix() {
        return text()
                .append(MiniMessage.miniMessage()
                        .deserialize("<#f74040>[</#f74040>" +
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
                                "<#631ae6>]</#631ae6>"))
                .build();
    }

    @Contract(" -> new")
    private @NotNull TextComponent backwardsPluginPrefixComponent() {
        return text()
                .append(MiniMessage.miniMessage()
                        .deserialize("<#631ae6>[</#631ae6>" +
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
                                "<#f74040>]</#f74040>"))
                .build();
    }
}