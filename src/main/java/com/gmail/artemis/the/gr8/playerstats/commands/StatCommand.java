package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.enums.Query;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageFactory;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


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
