package com.gmail.artemis.the.gr8.playerstats.test;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.commands.StatCommand;
import com.gmail.artemis.the.gr8.playerstats.msg.LanguageKeyHandler;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageFactory;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;

public class TestStatCommand extends StatCommand {

    private final BukkitAudiences adventure;
    private final MessageFactory messageFactory;
    private final ThreadManager threadManager;

    public TestStatCommand(BukkitAudiences a, MessageFactory m, ThreadManager t) {
        super(a, m, t);

        adventure = a;
        messageFactory = m;
        threadManager = t;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {  //in case of less than 1 argument or "help", display the help message
            adventure.sender(sender).sendMessage(messageFactory.helpMsg(sender instanceof ConsoleCommandSender));
        }
        else if (args[0].equalsIgnoreCase("examples") ||
                args[0].equalsIgnoreCase("example")) {  //in case of "statistic examples", show examples
            adventure.sender(sender).sendMessage(messageFactory.usageExamples(sender instanceof ConsoleCommandSender));
        }
        else if (args[0].equalsIgnoreCase("test")) {
            String selection = (args.length > 1) ? args[1] : null;
            printTranslatableNames(sender, selection);
        }

        else {  //part 1: collecting all relevant information from the args
            StatRequest request = generateRequest(sender, args);
            TextComponent issues = checkRequest(request);
            if (issues == null) {
                threadManager.startStatThread(request);
            }
            else {
                adventure.sender(sender).sendMessage(issues);
                return false;
            }
        }
        return true;
    }

    //test method
    private void printTranslatableNames(CommandSender sender, String selection) {
        LanguageKeyHandler lang = new LanguageKeyHandler();

        if (selection == null) {
            TextComponent msg = Component.text("Include 'block', 'item', 'entity' or 'stat'").color(TextColor.fromHexString("#FFB80E"));
            adventure.sender(sender).sendMessage(msg);
        }
        else if (selection.equalsIgnoreCase("block")) {
            for (String name : EnumHandler.getBlockNames()) {
                Material block = EnumHandler.getBlockEnum(name);
                String key = lang.getBlockKey(block);
                if (key != null) {
                    TranslatableComponent msg = Component.translatable(key)
                            .color(TextColor.fromHexString("#FFB80E"))
                            .append(space())
                            .append(text("for blockName: ").color(NamedTextColor.WHITE))
                            .append(text(name).color(TextColor.fromHexString("#55AAFF")));
                    adventure.sender(sender).sendMessage(msg);
                }
            }
        }
        else if (selection.equalsIgnoreCase("entity")) {
            for (String name : EnumHandler.getEntityNames()) {
                EntityType entity = EnumHandler.getEntityEnum(name);
                String key = lang.getEntityKey(entity);
                if (key != null) {
                    TranslatableComponent msg = Component.translatable(key)
                            .color(TextColor.fromHexString("#FFB80E"))
                            .append(space())
                            .append(text("for entityName: ").color(NamedTextColor.WHITE))
                            .append(text(name).color(TextColor.fromHexString("#55AAFF")));
                    adventure.sender(sender).sendMessage(msg);
                }
            }
        }
        else if (selection.equalsIgnoreCase("item")) {
            for (String name : EnumHandler.getItemNames()) {
                Material item = EnumHandler.getItemEnum(name);
                String key = lang.getItemKey(item);
                if (key != null) {
                    TranslatableComponent msg = Component.translatable(key)
                            .color(TextColor.fromHexString("#FFB80E"))
                            .append(space())
                            .append(text("for itemName: ").color(NamedTextColor.WHITE))
                            .append(text(name).color(TextColor.fromHexString("#55AAFF")));
                    adventure.sender(sender).sendMessage(msg);
                }
            }
        }
        else if (selection.equalsIgnoreCase("stat")) {
            for (String name : EnumHandler.getStatNames()) {
                Statistic stat = EnumHandler.getStatEnum(name);
                String key = null;
                if (stat != null) {
                    key = lang.getStatKey(stat);
                }
                if (key != null) {
                    TranslatableComponent msg = Component.translatable(key)
                            .color(TextColor.fromHexString("#FFB80E"))
                            .append(space())
                            .append(text("for statName: ").color(NamedTextColor.WHITE))
                            .append(text(name).color(TextColor.fromHexString("#55AAFF")));
                    adventure.sender(sender).sendMessage(msg);
                }
            }
        }
        else {
            TextComponent msg = Component.text("hi :)").color(TextColor.fromHexString("#FFB80E"));
            adventure.sender(sender).sendMessage(msg);
        }
    }
}
