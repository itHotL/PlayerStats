package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.MessageFactory;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class StatCommand implements CommandExecutor {

    private final ThreadManager threadManager;
    private final BukkitAudiences adventure;
    private final MessageFactory messageFactory;

    public StatCommand(ThreadManager t, BukkitAudiences b, MessageFactory o) {
        threadManager = t;
        adventure = b;
        messageFactory = o;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        //part 1: collecting all relevant information from the args
        if (args.length >= 1) {
            StatRequest request = generateRequest(sender, args);

            //part 2: sending the information to the StatThread, or give feedback if request is invalid
            if (isValidStatRequest(request)) {
                threadManager.startStatThread(request);
                return true;
            }

            else {
                adventure.sender(sender).sendMessage(getRelevantFeedback(request));
                return false;
            }
        }

        //in case of less than 1 argument, always display the help message
        else {
            adventure.sender(sender).sendMessage(messageFactory.helpMsg());
            return false;
        }
    }

    private TextComponent getRelevantFeedback(@NotNull StatRequest request) {
        if (request.getStatName() == null) {
            return messageFactory.missingStatName();
        }
        else if (request.getStatType() != Statistic.Type.UNTYPED && request.getSubStatEntry() == null) {
            return messageFactory.missingSubStatName(request.getStatType());
        }
        else if (!EnumHandler.isValidStatEntry(request.getStatType(), request.getSubStatEntry())){
            return messageFactory.wrongSubStatType(request.getStatType(), request.getSubStatEntry());
        }
        else if (!request.topFlag()) {
            if (!request.playerFlag()) {
                return messageFactory.missingTarget();
            }
            else {
                return messageFactory.missingPlayerName();
            }
        }
        return messageFactory.unknownError();
    }

    //part 1: create a StatRequest Object with all the relevant information from the args
    private StatRequest generateRequest(CommandSender sender, String[] args) {
        StatRequest request = new StatRequest(sender);

        for (String arg : args) {
            if (EnumHandler.isStatistic(arg) && request.getStatName() == null) {
                request.setStatName(arg);
            }
            else if (EnumHandler.isSubStatEntry(arg)) {
                if (arg.equalsIgnoreCase("player")) {
                    if (request.playerFlag()) {
                        if (request.getSubStatEntry() == null) request.setSubStatEntry(arg);
                    }
                    else {
                        request.setPlayerFlag(true);
                    }
                }
                else {
                    if (request.getSubStatEntry() == null) request.setSubStatEntry(arg);
                }
            }
            else if (arg.equalsIgnoreCase("top")) {
                request.setTopFlag(true);
            }
            else if (arg.equalsIgnoreCase("me") && sender instanceof Player) {
                request.setPlayerName(sender.getName());
            }
            else if (OfflinePlayerHandler.isOfflinePlayerName(arg) && request.getPlayerName() == null) {
                request.setPlayerName(arg);
            }
        }
        return request;
    }

    //part 2: check whether all necessary ingredients are present to proceed with a lookup
    private boolean isValidStatRequest(StatRequest request) {
        validatePlayerFlag(request);
        removeUnnecessarySubStat(request);

        if (request.getStatName() != null) {
            if (!request.topFlag() && request.getPlayerName() == null) {
                assumeTopAsDefault(request);
            }
            return EnumHandler.isValidStatEntry(request.getStatType(), request.getSubStatEntry());
        }
        return false;
    }

    //account for the fact that "player" could be either a subStatEntry or a flag to indicate the target for the lookup, and correct the request if necessary
    private void validatePlayerFlag(StatRequest request) {
        if (request.getStatType() == Statistic.Type.ENTITY && request.getSubStatEntry() == null && request.playerFlag()) {
            request.setSubStatEntry("player");
        }
    }

    //in case the statistic is untyped, remove any subStatEntry that might be present
    private void removeUnnecessarySubStat(StatRequest request) {
        if (request.getSubStatEntry() != null && request.getStatType() == Statistic.Type.UNTYPED) {
            request.setSubStatEntry(null);
        }
    }

    //if no playerName was provided, and there is no topFlag, substitute a top flag
    private void assumeTopAsDefault(StatRequest request) {
        request.setTopFlag(true);
    }
}
