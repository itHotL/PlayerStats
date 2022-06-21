package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.enums.Query;
import com.gmail.artemis.the.gr8.playerstats.msg.LanguageKeyHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageFactory;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;


public class StatCommand implements CommandExecutor {

    private final BukkitAudiences adventure;
    private final MessageFactory messageFactory;
    private final ThreadManager threadManager;

    public StatCommand(BukkitAudiences a, MessageFactory m, ThreadManager t) {
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
            boolean extra = (args.length > 2);
            printTranslatableNames(sender, selection, extra);
            return true;
        }

        else {  //part 1: collecting all relevant information from the args
            StatRequest request = generateRequest(sender, args);

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
    private void printTranslatableNames(CommandSender sender, String selection, boolean extra) {
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
                String key = lang.getItemKey(name, extra);
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

    //create a StatRequest Object with all the relevant information from the args
    private StatRequest generateRequest(CommandSender sender, String[] args) {
        StatRequest request = new StatRequest(sender);

        for (String arg : args) {
            //check for statName
            if (EnumHandler.isStatistic(arg) && request.getStatName() == null) {
                request.setStatName(arg);
            }
            //check for subStatEntry and playerFlag
            else if (EnumHandler.isSubStatEntry(arg)) {
                if (arg.equalsIgnoreCase("player") && !request.playerFlag()) {
                    request.setPlayerFlag(true);
                }
                else {
                    if (request.getSubStatEntry() == null) request.setSubStatEntry(arg);
                }
            }
            //check for selection
            else if (request.getSelection() == null) {
                if (arg.equalsIgnoreCase("top")) {
                    request.setSelection(Query.TOP);
                }
                else if (arg.equalsIgnoreCase("server")) {
                    request.setSelection(Query.SERVER);
                }
                else if (arg.equalsIgnoreCase("me") && sender instanceof Player) {
                    request.setPlayerName(sender.getName());
                    request.setSelection(Query.PLAYER);
                }
                else if (OfflinePlayerHandler.isOfflinePlayerName(arg) && request.getPlayerName() == null) {
                    request.setPlayerName(arg);
                    request.setSelection(Query.PLAYER);
                }
            }
        }
        return request;
    }

    //part 2: check whether all necessary ingredients are present to proceed with a lookup
    private boolean isValidStatRequest(StatRequest request) {
        if (request.getStatName() != null) {
            if (request.playerFlag()) unpackPlayerFlag(request);
            if (request.getSelection() == null) assumeTopAsDefault(request);
            if (request.getSubStatEntry() != null) verifySubStat(request);

            if (request.getSelection() == Query.PLAYER && request.getPlayerName() == null) {
                return false;
            }
            else {
                return EnumHandler.isValidStatEntry(request.getStatType(), request.getSubStatEntry());
            }
        }
        return false;
    }

    //account for the fact that "player" could be either a subStatEntry, a flag to indicate the target for the lookup, or both
    private void unpackPlayerFlag(StatRequest request) {
        if (request.getStatType() == Statistic.Type.ENTITY && request.getSubStatEntry() == null) {
            request.setSubStatEntry("player");
        }
        if (request.getSelection() == null) {
            request.setSelection(Query.PLAYER);
        }
    }

    //in case the statistic is untyped, set the unnecessary subStatEntry to null
    private void verifySubStat(StatRequest request) {
        if (request.getSubStatEntry() != null && request.getStatType() == Statistic.Type.UNTYPED) {
            request.setSubStatEntry(null);
        }
    }

    //if no playerName was provided, and there is no topFlag or serverFlag, substitute a top flag
    private void assumeTopAsDefault(StatRequest request) {
        request.setSelection(Query.TOP);
    }

    //call this method when isValidStatRequest has returned false to get a relevant error-message
    private TextComponent getRelevantFeedback(@NotNull StatRequest request) {
        boolean isConsoleSender = request.getCommandSender() instanceof ConsoleCommandSender;
        if (request.getStatName() == null) {
            return messageFactory.missingStatName(isConsoleSender);
        }
        else if (request.getStatType() != Statistic.Type.UNTYPED && request.getSubStatEntry() == null) {
            return messageFactory.missingSubStatName(request.getStatType(), isConsoleSender);
        }
        else if (!EnumHandler.isValidStatEntry(request.getStatType(), request.getSubStatEntry())){
            return messageFactory.wrongSubStatType(request.getStatType(), request.getSubStatEntry(), isConsoleSender);
        }
        else if (request.getSelection() == Query.PLAYER && request.getPlayerName() == null) {
            return messageFactory.missingPlayerName(isConsoleSender);
        }
        return messageFactory.unknownError(isConsoleSender);
    }
}
