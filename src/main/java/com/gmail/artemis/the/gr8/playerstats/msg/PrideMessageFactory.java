package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;

import static net.kyori.adventure.text.Component.*;


public class PrideMessageFactory extends MessageFactory {

    private static ConfigHandler config;

    public PrideMessageFactory(ConfigHandler c) {
        super(c);
        config = c;
    }

    @Override
    protected TextComponent getHelpMsgTitle(boolean isConsoleSender) {
        return text().append(MiniMessage.miniMessage().deserialize("<rainbow:16>____________    [PlayerStats]    ____________</rainbow>")).build();
    }

    @Override
    protected TextComponent dotsComponent(String dots) {
        String tag = "<rainbow:" + config.getRainbowPhase() + ">";
        return text().append(MiniMessage.miniMessage().deserialize((tag + dots))).build();
    }
}
