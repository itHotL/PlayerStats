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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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
        if (args.length == 0) {  //in case of less than 1 argument, display the help message
            adventure.sender(sender).sendMessage(messageFactory.helpMsg(sender instanceof ConsoleCommandSender));
            return true;
        }
        else if (args[0].equalsIgnoreCase("help")) {
            adventure.sender(sender).sendMessage(messageFactory.helpMsg(sender instanceof ConsoleCommandSender));
            return false;
        }

        else if (args[0].equalsIgnoreCase("examples") ||
                args[0].equalsIgnoreCase("example")) {  //in case of "statistic examples", show examples
            adventure.sender(sender).sendMessage(messageFactory.usageExamples(sender instanceof ConsoleCommandSender));
            return true;
        }
        else if (args[0].equalsIgnoreCase("test")) {
            String selection = (args.length > 1) ? args[1] : null;
            printTranslatableNames(sender, selection);
            return true;
        }

        else {  //part 1: collecting all relevant information from the args
            StatRequest request = super.generateRequest(sender, args);

            if (isValidStatRequest(request)) {  //part 2: sending the information to the StatThread
                threadManager.startStatThread(request);
                return true;
            }
            else {  //part 2: or give feedback if request is invalid
                adventure.sender(sender).sendMessage(getRelevantFeedback(request));
                return false;
            }
        }
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
                String key = lang.getBlockKey(name);
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
                String key = lang.getEntityKey(name);
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
                String key = lang.getItemKey(name);
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
                String key = lang.getStatKey(name);
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
