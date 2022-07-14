package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.enums.PluginMessage;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageSender;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class StatCommand implements CommandExecutor {

    private static ThreadManager threadManager;
    private static MessageSender messageSender;
    private final OfflinePlayerHandler offlinePlayerHandler;

    public StatCommand(MessageSender m, ThreadManager t, OfflinePlayerHandler o) {
        threadManager = t;
        messageSender = m;
        offlinePlayerHandler = o;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {  //in case of less than 1 argument or "help", display the help message
            messageSender.send(sender, PluginMessage.HELP_MSG);
        }
        else if (args[0].equalsIgnoreCase("examples") ||
                args[0].equalsIgnoreCase("example")) {  //in case of "statistic examples", show examples
            messageSender.send(sender, PluginMessage.USAGE_EXAMPLES);
        }
        else {
            StatRequest request = generateRequest(sender, args);
            if (requestIsValid(request)) {
                threadManager.startStatThread(request);
            } else {
                return false;
            }
        }
        return true;
    }

    /** Create a StatRequest Object with all the relevant information from the args[]. */
    private StatRequest generateRequest(CommandSender sender, String[] args) {
        StatRequest request = new StatRequest(sender);
        for (String arg : args) {
            //check for statName
            if (EnumHandler.isStatistic(arg) && request.getStatistic() == null) {
                request.setStatistic(EnumHandler.getStatEnum(arg));
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
            else if (arg.equalsIgnoreCase("top")) {
                request.setSelection(Target.TOP);
            }
            else if (arg.equalsIgnoreCase("server")) {
                request.setSelection(Target.SERVER);
            }
            else if (arg.equalsIgnoreCase("me")) {
                if (sender instanceof Player) {
                    request.setPlayerName(sender.getName());
                    request.setSelection(Target.PLAYER);
                }
                else if (sender instanceof ConsoleCommandSender) {
                    request.setSelection(Target.SERVER);
                }
            }
            else if (offlinePlayerHandler.isRelevantPlayer(arg) && request.getPlayerName() == null) {
                request.setPlayerName(arg);
                request.setSelection(Target.PLAYER);
            }
        }
        patchRequest(request);
        return request;
    }

    /** Adjust the StatRequest object if needed: unpack the playerFlag into a subStatEntry,
     try to retrieve the corresponding Enum Constant for any relevant block/entity/item,
     and remove any unnecessary subStatEntries.*/
    private void patchRequest(StatRequest request) {
        if (request.getStatistic() != null) {
            Statistic.Type type = request.getStatistic().getType();

            if (request.playerFlag()) {  //unpack the playerFlag
                if (type == Statistic.Type.ENTITY && request.getSubStatEntry() == null) {
                    request.setSubStatEntry("player");
                }
                else {
                    request.setSelection(Target.PLAYER);
                }
            }

            String subStatEntry = request.getSubStatEntry();
            switch (type) {  //attempt to convert relevant subStatEntries into their corresponding Enum Constant
                case BLOCK -> {
                    Material block = EnumHandler.getBlockEnum(subStatEntry);
                    if (block != null) request.setBlock(block);
                }
                case ENTITY -> {
                    EntityType entity = EnumHandler.getEntityEnum(subStatEntry);
                    if (entity != null) request.setEntity(entity);
                }
                case ITEM -> {
                    Material item = EnumHandler.getItemEnum(subStatEntry);
                    if (item != null) request.setItem(item);
                }
                case UNTYPED -> {  //remove unnecessary subStatEntries
                    if (subStatEntry != null) request.setSubStatEntry(null);
                }
            }
        }
    }

    /** This method validates the StatRequest and returns feedback to the player if it returns false.
     It checks the following:
     <p>1. Is a Statistic set?</p>
     <p>2. Is a subStat needed, and is a subStat Enum Constant present? (block/entity/item)</p>
     <p>3. If the target is PLAYER, is a valid PlayerName provided? </p>
     @return true if the Request is valid, and false + an explanation message otherwise. */
    private boolean requestIsValid(StatRequest request) {
        if (request.getStatistic() == null) {
            messageSender.send(request.getCommandSender(), PluginMessage.MISSING_STAT_NAME);
            return false;
        }
        Statistic.Type type = request.getStatistic().getType();
        if (request.getSubStatEntry() == null && type != Statistic.Type.UNTYPED) {
            messageSender.send(request.getCommandSender(), PluginMessage.MISSING_SUB_STAT_NAME, type);
            return false;
        }
        else if (!matchingSubStat(request)) {
            messageSender.send(request.getCommandSender(), PluginMessage.WRONG_SUB_STAT_TYPE, type);
            return false;
        }
        else if (request.getSelection() == Target.PLAYER && request.getPlayerName() == null) {
            messageSender.send(request.getCommandSender(), PluginMessage.MISSING_PLAYER_NAME);
            return false;
        }
        else {
            return true;
        }
    }

    private boolean matchingSubStat(StatRequest request) {
        Statistic.Type type = request.getStatistic().getType();
        switch (type) {
            case BLOCK -> {
                return request.getBlock() != null;
            }
            case ENTITY -> {
                return request.getEntity() != null;
            }
            case ITEM -> {
                return request.getItem() != null;
            }
            default -> {
                return true;
            }
        }
    }
}