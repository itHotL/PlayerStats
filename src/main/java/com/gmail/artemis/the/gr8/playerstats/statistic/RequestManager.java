package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.enums.StandardMessage;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.msg.OutputManager;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RequestManager {

    private final OfflinePlayerHandler offlinePlayerHandler;
    private static OutputManager outputManager;

    public RequestManager(OfflinePlayerHandler offlinePlayerHandler, OutputManager outputManager) {
        this.offlinePlayerHandler = offlinePlayerHandler;
        RequestManager.outputManager = outputManager;
    }

    /** This will create a {@link StatRequest} from the provided args, with the requesting Player (or Console)
     as CommandSender. This CommandSender will receive feedback messages if the SimpleRequest could not be created.
     @param args an Array of args such as a CommandSender would put in Minecraft chat:
     <p>- a stat-name (example: "mine_block")</p>
     <p>- if applicable, a sub-stat-name (example: diorite)(</p>
     <p>- a target for this lookup: can be "top", "server", "player" (or "me" to indicate the current CommandSender)</p>
     <p>- if "player" was chosen, include a player-name</p>
     @param sender the CommandSender that requested this specific statistic
     @throws IllegalArgumentException if the args do not result in a valid statistic look-up*/
    public StatRequest generateRequest(CommandSender sender, String[] args) {
        StatRequest request = new StatRequest(sender);
        for (String arg : args) {
            //check for statName
            if (EnumHandler.isStatistic(arg) && request.getStatistic() == null) {
                request.setStatistic(EnumHandler.getStatEnum(arg));
            }
            //check for subStatEntry and playerFlag
            else if (EnumHandler.isSubStatEntry(arg)) {
                if (arg.equalsIgnoreCase("player") && !request.getPlayerFlag()) {
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

    public StatRequest generateRequest(@NotNull Target selection, @NotNull Statistic statistic, Material material, EntityType entity, OfflinePlayer player) {
        return null;
    }

    /** This method validates the {@link StatRequest} and returns a feedback message if the Request is invalid.
     It checks the following:
     <p>1. Is a Statistic set?</p>
     <p>2. Is a subStat needed, and is a subStat Enum constant present? (block/entity/item)</p>
     <p>3. If the target is PLAYER, is a valid PlayerName provided? </p>
     @return true if the SimpleRequest is valid, and false + an explanation message otherwise. */
    public boolean requestIsValid(StatRequest request) {
        if (request.getStatistic() == null) {
            outputManager.sendFeedbackMsg(request.getCommandSender(), StandardMessage.MISSING_STAT_NAME);
            return false;
        }
        Statistic.Type type = request.getStatistic().getType();
        if (request.getSubStatEntry() == null && type != Statistic.Type.UNTYPED) {
            outputManager.sendFeedbackMsgMissingSubStat(request.getCommandSender(), type);
            return false;
        }
        else if (!hasMatchingSubStat(request)) {
            outputManager.sendFeedbackMsgWrongSubStat(request.getCommandSender(), type, request.getSubStatEntry());
            return false;
        }
        else if (request.getSelection() == Target.PLAYER && request.getPlayerName() == null) {
            outputManager.sendFeedbackMsg(request.getCommandSender(), StandardMessage.MISSING_PLAYER_NAME);
            return false;
        }
        else {
            return true;
        }
    }

    /** Adjust the SimpleRequest object if needed: unpack the playerFlag into a subStatEntry,
     try to retrieve the corresponding Enum Constant for any relevant block/entity/item,
     and remove any unnecessary subStatEntries.*/
    private void patchRequest(StatRequest request) {
        if (request.getStatistic() != null) {
            Statistic.Type type = request.getStatistic().getType();

            if (request.getPlayerFlag()) {  //unpack the playerFlag
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

    private boolean hasMatchingSubStat(StatRequest request) {
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