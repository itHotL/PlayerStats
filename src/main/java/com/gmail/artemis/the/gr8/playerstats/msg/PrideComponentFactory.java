package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.time.LocalDate;
import java.time.Month;

import static net.kyori.adventure.text.Component.*;


public class PrideComponentFactory extends ComponentFactory {

    private static ConfigHandler config;

    public PrideComponentFactory(ConfigHandler c) {
        super(c);
        config = c;
    }


    @Override
    public TextComponent prefixTitleComponent(boolean isBukkitConsole) {
        if (cancelRainbow(isBukkitConsole)) {
            return super.prefixTitleComponent(isBukkitConsole);
        }
        else {
            String title = "<rainbow:16>____________    [PlayerStats]    ____________</rainbow>"; //12 underscores
            return text()
                    .append(MiniMessage.miniMessage().deserialize(title))
                    .build();
        }
    }

    @Override
    public TextComponent pluginPrefixComponent(boolean isConsoleSender) {
        if (cancelRainbow(isConsoleSender)) {
            return super.pluginPrefixComponent(isConsoleSender);
        }
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

    /** Don't use rainbow formatting if the rainbow Prefix is disabled,
     if festive formatting is disabled or it is not pride month,
     or the commandsender is a Bukkit or Spigot console.*/
    private boolean cancelRainbow(boolean isBukkitConsole) {
        return !(config.enableRainbowMode() || (config.enableFestiveFormatting() && LocalDate.now().getMonth().equals(Month.JUNE))) ||
                (isBukkitConsole);
    }
}