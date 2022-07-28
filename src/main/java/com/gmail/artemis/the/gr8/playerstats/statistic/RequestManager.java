package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.gmail.artemis.the.gr8.playerstats.enums.StandardMessage;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.msg.OutputManager;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class RequestManager implements RequestGenerator {

    private final EnumHandler enumHandler;
    private final OfflinePlayerHandler offlinePlayerHandler;
    private static OutputManager outputManager;

    public RequestManager(EnumHandler enumHandler, OfflinePlayerHandler offlinePlayerHandler, OutputManager outputManager) {
        this.enumHandler = enumHandler;
        this.offlinePlayerHandler = offlinePlayerHandler;
        RequestManager.outputManager = outputManager;
    }

    public StatRequest generateRequest(CommandSender sender, String[] args) {
        StatRequest statRequest = new StatRequest(sender);
        for (String arg : args) {
            //check for statName
            if (enumHandler.isStatistic(arg) && statRequest.getStatistic() == null) {
                statRequest.setStatistic(EnumHandler.getStatEnum(arg));
            }
            //check for subStatEntry and playerFlag
            else if (enumHandler.isSubStatEntry(arg)) {
                if (arg.equalsIgnoreCase("player") && !statRequest.getPlayerFlag()) {
                    statRequest.setPlayerFlag(true);
                }
                else {
                    if (statRequest.getSubStatEntryName() == null) statRequest.setSubStatEntryName(arg);
                }
            }
            //check for selection
            else if (arg.equalsIgnoreCase("top")) {
                statRequest.setTarget(Target.TOP);
            }
            else if (arg.equalsIgnoreCase("server")) {
                statRequest.setTarget(Target.SERVER);
            }
            else if (arg.equalsIgnoreCase("me")) {
                if (sender instanceof Player) {
                    statRequest.setPlayerName(sender.getName());
                    statRequest.setTarget(Target.PLAYER);
                }
                else if (sender instanceof ConsoleCommandSender) {
                    statRequest.setTarget(Target.SERVER);
                }
            }
            else if (offlinePlayerHandler.isRelevantPlayer(arg) && statRequest.getPlayerName() == null) {
                statRequest.setPlayerName(arg);
                statRequest.setTarget(Target.PLAYER);
            }
            else if (arg.equalsIgnoreCase("api")) {
                statRequest.setAPIRequest();
            }
        }
        patchRequest(statRequest);
        return statRequest;
    }

    /** This method will generate a {@link StatRequest} for a request arriving through the API.*/
    public StatRequest generateAPIRequest(@NotNull Target selection, @NotNull Statistic statistic, Material material, EntityType entity, String playerName) {
        StatRequest statRequest = new StatRequest(Bukkit.getConsoleSender(), true);
        statRequest.setTarget(selection);
        statRequest.setStatistic(statistic);
        switch (statistic.getType()) {
            case BLOCK -> {
                statRequest.setBlock(material);
                statRequest.setSubStatEntryName(material.toString());
            }
            case ITEM -> {
                statRequest.setItem(material);
                statRequest.setSubStatEntryName(material.toString());
            }
            case ENTITY -> {
                statRequest.setEntity(entity);
                statRequest.setSubStatEntryName(entity.toString());
            }
        }
        if (selection == Target.PLAYER) statRequest.setPlayerName(playerName);
        return statRequest;
    }

    /** Checks if a given {@link StatRequest} would result in a valid statistic look-up,
     and sends a feedback message to the CommandSender that prompted the statRequest if it is invalid.
     <br> The following is checked:
     <ul>
     <li>Is a <code>statistic</code> set?
     <li>Is a <code>subStatEntry</code> needed, and if so, is a corresponding Material/EntityType present?
     <li>If the <code>target</code> is Player, is a valid <code>playerName</code> provided?
     </ul>
     @param statRequest the StatRequest to check
     @return true if the StatRequest is valid, and false otherwise.
     */
    public boolean validateRequest(StatRequest statRequest) {
        return validateRequestAndSendMessage(statRequest, statRequest.getCommandSender());
    }

    /** Checks if a given {@link StatRequest} would result in a valid statistic look-up,
     and sends a feedback message in the server console if it is invalid.
     <br> The following is checked:
     <ul>
     <li>Is a <code>statistic</code> set?
     <li>Is a <code>subStatEntry</code> needed, and if so, is a corresponding Material/EntityType present?
     <li>If the <code>target</code> is Player, is a valid <code>playerName</code> provided?
     </ul>
     @param statRequest the StatRequest to check
     @return true if the StatRequest is valid, and false otherwise.
     */
    public boolean validateAPIRequest(StatRequest statRequest) {
        return validateRequestAndSendMessage(statRequest, Bukkit.getConsoleSender());
    }

    private boolean validateRequestAndSendMessage(StatRequest statRequest, CommandSender sender) {
        if (statRequest.getStatistic() == null) {
            outputManager.sendFeedbackMsg(sender, StandardMessage.MISSING_STAT_NAME);
            return false;
        }
        Statistic.Type type = statRequest.getStatistic().getType();
        if (statRequest.getSubStatEntryName() == null && type != Statistic.Type.UNTYPED) {
            outputManager.sendFeedbackMsgMissingSubStat(sender, type);
            return false;
        }
        else if (!hasMatchingSubStat(statRequest)) {
            outputManager.sendFeedbackMsgWrongSubStat(sender, type, statRequest.getSubStatEntryName());
            return false;
        }
        else if (statRequest.getTarget() == Target.PLAYER && statRequest.getPlayerName() == null) {
            outputManager.sendFeedbackMsg(sender, StandardMessage.MISSING_PLAYER_NAME);
            return false;
        }
        else {
            return true;
        }
    }

    /** Adjust the StatRequest object if needed: unpack the playerFlag into a subStatEntry,
     try to retrieve the corresponding Enum Constant for any relevant block/entity/item,
     and remove any unnecessary subStatEntries.*/
    private void patchRequest(StatRequest statRequest) {
        if (statRequest.getStatistic() != null) {
            Statistic.Type type = statRequest.getStatistic().getType();

            if (statRequest.getPlayerFlag()) {  //unpack the playerFlag
                if (type == Statistic.Type.ENTITY && statRequest.getSubStatEntryName() == null) {
                    statRequest.setSubStatEntryName("player");
                }
                else {
                    statRequest.setTarget(Target.PLAYER);
                }
            }

            String subStatEntry = statRequest.getSubStatEntryName();
            switch (type) {  //attempt to convert relevant subStatEntries into their corresponding Enum Constant
                case BLOCK -> {
                    Material block = EnumHandler.getBlockEnum(subStatEntry);
                    if (block != null) statRequest.setBlock(block);
                }
                case ENTITY -> {
                    EntityType entity = EnumHandler.getEntityEnum(subStatEntry);
                    if (entity != null) statRequest.setEntity(entity);
                }
                case ITEM -> {
                    Material item = EnumHandler.getItemEnum(subStatEntry);
                    if (item != null) statRequest.setItem(item);
                }
                case UNTYPED -> {  //remove unnecessary subStatEntries
                    if (subStatEntry != null) statRequest.setSubStatEntryName(null);
                }
            }
        }
    }

    private boolean hasMatchingSubStat(StatRequest statRequest) {
        Statistic.Type type = statRequest.getStatistic().getType();
        switch (type) {
            case BLOCK -> {
                return statRequest.getBlock() != null;
            }
            case ENTITY -> {
                return statRequest.getEntity() != null;
            }
            case ITEM -> {
                return statRequest.getItem() != null;
            }
            default -> {
                return true;
            }
        }
    }
}