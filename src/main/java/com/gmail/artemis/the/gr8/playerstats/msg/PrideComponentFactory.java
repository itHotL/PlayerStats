package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;

import com.gmail.artemis.the.gr8.playerstats.enums.DebugLevel;
import com.gmail.artemis.the.gr8.playerstats.enums.PluginColor;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;

import static net.kyori.adventure.text.Component.*;

public class PrideComponentFactory extends ComponentFactory {


    public PrideComponentFactory(ConfigHandler c) {
        super(c);

        MyLogger.logMsg("PrideComponentFactory created!", DebugLevel.MEDIUM);
    }

    @Override
    protected void prepareColors() {
        PREFIX = PluginColor.GOLD.getColor();
        BRACKETS = PluginColor.GRAY.getColor();
        UNDERSCORE = PluginColor.DARK_PURPLE.getColor();
        MSG_MAIN = PluginColor.MEDIUM_GOLD.getColor();
        MSG_MAIN_2 = PluginColor.GOLD.getColor();
        MSG_ACCENT = PluginColor.GOLD.getColor();
        MSG_ACCENT_2 = PluginColor.LIGHT_YELLOW.getColor();
        HOVER_MSG = PluginColor.LIGHT_BLUE.getColor();
        HOVER_ACCENT = PluginColor.LIGHT_GOLD.getColor();
    }

    @Override
    public TextComponent prefixTitleComponent() {
        String title = "<rainbow:16>____________    [PlayerStats]    ____________</rainbow>"; //12 underscores
        return text()
                .append(MiniMessage.miniMessage().deserialize(title))
                .build();
    }

    @Override
    public TextComponent pluginPrefixComponent() {
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

    public TextComponent backwardsPluginPrefixComponents() {
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