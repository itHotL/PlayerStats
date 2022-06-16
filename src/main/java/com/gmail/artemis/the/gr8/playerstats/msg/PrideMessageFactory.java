package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;

import com.gmail.artemis.the.gr8.playerstats.enums.Query;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.map.MinecraftFont;

import static net.kyori.adventure.text.Component.*;


public class PrideMessageFactory extends MessageFactory {

    private static ConfigHandler config;

    public PrideMessageFactory(ConfigHandler c) {
        super(c);
        config = c;
    }

    @Override
    protected TextComponent getPrefixAsTitle(boolean isConsoleSender) {
        if (isConsoleSender && Bukkit.getName().equalsIgnoreCase("CraftBukkit")) {
            return super.getPrefixAsTitle(true);
        }
        else {
            String underscores = "____________";  //12 underscores
            String title = "<rainbow:16>" + underscores + "    [PlayerStats]    " + underscores + "</rainbow>";
            return text()
                    .append(MiniMessage.miniMessage().deserialize(title))
                    .build();
        }
    }

    @Override
    protected TextComponent getTopStatTitle(int topLength, String statName, String subStatEntryName, boolean isConsoleSender) {
        if (isConsoleSender && Bukkit.getName().equalsIgnoreCase("CraftBukkit")) {
            return super.getTopStatTitle(topLength, statName, subStatEntryName, true);
        }
        else {
            MinecraftFont font = new MinecraftFont();
            TextComponent prefixTitle = getPrefixAsTitle(false);
            TextComponent statTitle = Component.text()
                    .append(titleComponent(Query.TOP, "Top")).append(space())
                    .append(titleNumberComponent(topLength)).append(space())
                    .append(statNameComponent(Query.TOP, statName)).append(space())
                    .append(subStatNameComponent(Query.TOP, subStatEntryName))
                    .build();

            if (font.getWidth(prefixTitle.content()) > font.getWidth(statTitle.content())) {
                //divide by 4 to get spaces, then by 2 to get distance needed at the front
                int spaces = (int) Math.round((double) (font.getWidth(prefixTitle.content()) - font.getWidth(statTitle.content()))/8);
                String space = " ".repeat(spaces);
                return Component.newline()
                        .append(prefixTitle)
                        .append(newline())
                        .append(text(space))
                        .append(statTitle);
            }
            else {
                return Component.newline()
                        .append(prefixTitle)
                        .append(newline())
                        .append(statTitle);
            }
        }
    }

    @Override
    protected TextComponent dotsComponent(String dots, boolean isConsoleSender) {
        if (isConsoleSender && Bukkit.getName().equalsIgnoreCase("CraftBukkit")) {
            return super.dotsComponent(dots, true);
        }
        else {
            String tag = "<rainbow:" + config.getRainbowPhase() + ">";
            return text().append(MiniMessage.miniMessage().deserialize((tag + dots))).build();
        }
    }
}
