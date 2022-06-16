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
    private final String prefixTitle = "____________    [PlayerStats]    ____________";  //12 underscores

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
            String title = "<rainbow:16>" + prefixTitle + "</rainbow>";
            return text()
                    .append(MiniMessage.miniMessage().deserialize(title))
                    .build();
        }
    }

    @Override
    protected TextComponent pluginPrefix() {
        return text().append(MiniMessage.miniMessage().deserialize("<#fc3661>[</#fc3661>" +
                "<#fe4550>P</#fe4550>" +
                "<#fe5640>l</#fe5640>" +
                "<#fb6731>a</#fb6731>" +
                "<#f67824>y</#f67824>" +
                "<#ee8a19>e</#ee8a19>" +
                "<#e49b0f>r</#e49b0f>" +
                "<#d9ac08>S</#d9ac08>" +
                "<#cbbd03>t</#cbbd03>" +
                "<#bccb01>a</#bccb01>" +
                "<#acd901>t</#acd901>" +
                "<#9be503>s</#9be503>" +
                "<#8aee08>] </#8aee08>")).build();
        // <#fe4550></#fe4550><#fe5640>P</#fe5640><#fb6731>l</#fb6731><#f67824>a</#f67824><#ee8a19>y</#ee8a19><#e49b0f>e</#e49b0f><#FFB80E></#FFB80E><#cbbd03>S</#cbbd03><#bccb01>t</#bccb01><#acd901></#acd901><#9be503></#9be503><#8BD448>s</#8BD448><#2AA8F2>]</#2AA8F2>]]
        // <#205bf3>_</#205bf3>
        // <#2d4afa>_</#2d4afa>
        // <#3b3bfd>_</#3b3bfd>
        // <#4a2dfe>_</#4a2dfe>
        // <#5b20fd>_</#5b20fd>
        // <#6c15fa>_</#6c15fa>
        // <#7e0df4>_</#7e0df4>
        // <#9006eb>_</#9006eb>
        // <#a102e1>_</#a102e1>
        // <#b201d5>_</#b201d5>
        // <#c201c7>_</#c201c7>
        // <#d005b7>_</#d005b7>
        // <#dd0aa7> </#dd0aa7>
        // <#e81296> </#e81296>
        // <#f11c84> </#f11c84>
        // <#f82872> </#f82872>
        // <#fc3661>[</#fc3661>
        // <#fe4550>P</#fe4550>
        // <#fe5640>l</#fe5640>
        // <#fb6731>a</#fb6731>
        // <#f67824>y</#f67824>
        // <#ee8a19>e</#ee8a19>
        // <#e49b0f>r</#e49b0f>
        // <#d9ac08>S</#d9ac08>
        // <#cbbd03>t</#cbbd03>
        // <#bccb01>a</#bccb01>
        // <#acd901>t</#acd901>
        // <#9be503>s</#9be503>
        // <#8aee08>]</#8aee08>
        // <#78f60f> </#78f60f>
        // <#67fb19> </#67fb19>
        // <#55fe24> </#55fe24>
        // <#45fe31> </#45fe31>
        // <#36fc40>_</#36fc40>
        // <#28f850>_</#28f850>
        // <#1cf161>_</#1cf161>
        // <#12e872>_</#12e872>
        // <#0add84>_</#0add84>
        // <#05d095>_</#05d095>
        // <#01c1a7>_</#01c1a7>
        // <#01b2b7>_</#01b2b7>
        // <#02a1c6>_</#02a1c6>
        // <#0690d4>_</#0690d4>
        // <#0d7ee1>_</#0d7ee1>
        // <#156ceb>_</#156ceb>

    }

    /*
    @Override
    protected TextComponent getTopStatTitle(int topLength, String statName, String subStatEntryName, boolean isConsoleSender) {
        if (isConsoleSender && Bukkit.getName().equalsIgnoreCase("CraftBukkit")) {
            return super.getTopStatTitle(topLength, statName, subStatEntryName, true);
        }
        else {
            MinecraftFont font = new MinecraftFont();
            TextComponent statTitle = Component.text()
                    .append(titleComponent(Query.TOP, config.getTopStatsTitel())).append(space())
                    .append(titleNumberComponent(topLength)).append(space())
                    .append(statNameComponent(Query.TOP, statName)).append(space())
                    .append(subStatNameComponent(Query.TOP, subStatEntryName))
                    .build();
            String title = config.getTopStatsTitel() + " " + topLength + " " + statName + " " + subStatEntryName;
            if (font.getWidth(prefixTitle) > font.getWidth(title)) {
                //divide by 4 to get spaces, then by 2 to get distance needed at the front
                int spaces = (int) Math.round((double) (font.getWidth(prefixTitle) - font.getWidth(title))/8);
                Bukkit.getLogger().info("Width of prefixTitle: " + font.getWidth(prefixTitle));
                Bukkit.getLogger().info("Width of statTitle: " + font.getWidth(title));
                Bukkit.getLogger().info("Spaces: " + spaces);

                String space = " ".repeat(spaces);
                return Component.newline()
                        .append(getPrefixAsTitle(false))
                        .append(newline())
                        .append(text(space))
                        .append(statTitle);
            }
            else {
                return Component.newline()
                        .append(getPrefixAsTitle(false))
                        .append(newline())
                        .append(statTitle);
            }
        }
    }
     */

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
