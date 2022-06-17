package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

import static net.kyori.adventure.text.Component.*;


public class PrideMessageFactory extends MessageFactory {


    public PrideMessageFactory(ConfigHandler c) {
        super(c);
    }

    @Override
    protected TextComponent getPrefixAsTitle(boolean isConsoleSender) {
        if (isConsoleSender && Bukkit.getName().equalsIgnoreCase("CraftBukkit")) {
            return super.getPrefixAsTitle(true);
        }
        else {
            String title = "<rainbow:16>____________    [PlayerStats]    ____________</rainbow>"; //12 underscores
            return text()
                    .append(MiniMessage.miniMessage().deserialize(title))
                    .build();
        }
    }

    @Override
    protected TextComponent pluginPrefix(boolean isConsoleSender) {
        if (isConsoleSender && Bukkit.getName().equalsIgnoreCase("CraftBukkit")) {
            return super.pluginPrefix(true);
        }
        return text()
                .append(MiniMessage.miniMessage()
                        .deserialize("<#fe3e3e>[</#fe3e3e>" +
                                "<#fe5640>P</#fe5640>" +
                                "<#f67824>l</#f67824>" +
                                "<#ee8a19>a</#ee8a19>" +
                                "<#e49b0f>y</#e49b0f>" +
                                "<#cbbd03>e</#cbbd03>" +
                                "<#bccb01>r</#bccb01>" +
                                "<#8aee08>S</#8aee08>" +
                                "<#45fe31>t</#45fe31>" +
                                "<#01c1a7>a</#01c1a7>" +
                                "<#0690d4>t</#0690d4>" +
                                "<#205bf3>s</#205bf3>" +
                                "<#6c15fa>] </#6c15fa>"))
                .build();
    }
}
