package com.gmail.artemis.the.gr8.playerstats.statistic.request;

import com.gmail.artemis.the.gr8.playerstats.enums.StandardMessage;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.msg.OutputManager;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public final class InternalStatFetcher {

    private final EnumHandler enumHandler;
    private final OfflinePlayerHandler offlinePlayerHandler;
    private static OutputManager outputManager;

    public InternalStatFetcher(EnumHandler enumHandler, OfflinePlayerHandler offlinePlayerHandler, OutputManager outputManager) {
        this.enumHandler = enumHandler;
        this.offlinePlayerHandler = offlinePlayerHandler;
        InternalStatFetcher.outputManager = outputManager;
    }

    /** This will create a {@link StatRequestCore} from the provided args, with the requesting Player (or Console)
     as CommandSender. This CommandSender will receive feedback messages if the StatRequest could not be created.

     @param args an Array of args such as a CommandSender would put in Minecraft chat:
     <p>- a <code>statName</code> (example: "mine_block")</p>
     <p>- if applicable, a <code>subStatEntryName</code> (example: diorite)(</p>
     <p>- a <code>target</code> for this lookup: can be "top", "server", "player" (or "me" to indicate the current CommandSender)</p>
     <p>- if "player" was chosen, include a <code>playerName</code></p>

     @param sender the CommandSender that requested this specific statistic
     @return the generated StatRequest
     */
    public StatRequestCore generateRequest(CommandSender sender, String[] args) {
        StatRequestCore statRequestCore = new StatRequestCore(sender);
        for (String arg : args) {
            //check for statName
            if (enumHandler.isStatistic(arg) && statRequestCore.getStatistic() == null) {
                statRequestCore.setStatistic(EnumHandler.getStatEnum(arg));
            }
            //check for subStatEntry and playerFlag
            else if (enumHandler.isSubStatEntry(arg)) {
                if (arg.equalsIgnoreCase("player") && !statRequestCore.getPlayerFlag()) {
                    statRequestCore.setPlayerFlag(true);
                }
                else {
                    if (statRequestCore.getSubStatEntryName() == null) statRequestCore.setSubStatEntryName(arg);
                }
            }
            //check for selection
            else if (arg.equalsIgnoreCase("top")) {
                statRequestCore.setTarget(Target.TOP);
            }
            else if (arg.equalsIgnoreCase("server")) {
                statRequestCore.setTarget(Target.SERVER);
            }
            else if (arg.equalsIgnoreCase("me")) {
                if (sender instanceof Player) {
                    statRequestCore.setPlayerName(sender.getName());
                    statRequestCore.setTarget(Target.PLAYER);
                }
                else if (sender instanceof ConsoleCommandSender) {
                    statRequestCore.setTarget(Target.SERVER);
                }
            }
            else if (offlinePlayerHandler.isRelevantPlayer(arg) && statRequestCore.getPlayerName() == null) {
                statRequestCore.setPlayerName(arg);
                statRequestCore.setTarget(Target.PLAYER);
            }
            else if (arg.equalsIgnoreCase("api")) {
                statRequestCore.setAPIRequest();
            }
        }
        patchRequest(statRequestCore);
        return statRequestCore;
    }

    /** Checks if a given {@link StatRequestCore} would result in a valid statistic look-up,
     and sends a feedback message to the CommandSender that prompted the statRequest if it is invalid.
     <br> The following is checked:
     <ul>
     <li>Is a <code>statistic</code> set?
     <li>Is a <code>subStatEntry</code> needed, and if so, is a corresponding Material/EntityType present?
     <li>If the <code>target</code> is Player, is a valid <code>playerName</code> provided?
     </ul>
     @param statRequestCore the StatRequest to check
     @return true if the StatRequest is valid, and false otherwise.
     */
    public boolean validateRequest(StatRequestCore statRequestCore) {
        return validateRequestAndSendMessage(statRequestCore, statRequestCore.getCommandSender());
    }

    /** Checks if a given {@link StatRequestCore} would result in a valid statistic look-up,
     and sends a feedback message if it is invalid.
     <br> The following is checked:
     <ul>
     <li>Is a <code>statistic</code> set?
     <li>Is a <code>subStatEntry</code> needed, and if so, is a corresponding Material/EntityType present?
     <li>If the <code>target</code> is Player, is a valid <code>playerName</code> provided?
     </ul>
     @param statRequestCore the StatRequest to check
     @return true if the StatRequest is valid, and false otherwise.
     */
    private boolean validateRequestAndSendMessage(StatRequestCore statRequestCore, CommandSender sender) {
        if (statRequestCore.getStatistic() == null) {
            outputManager.sendFeedbackMsg(sender, StandardMessage.MISSING_STAT_NAME);
            return false;
        }
        Statistic.Type type = statRequestCore.getStatistic().getType();
        if (statRequestCore.getSubStatEntryName() == null && type != Statistic.Type.UNTYPED) {
            outputManager.sendFeedbackMsgMissingSubStat(sender, type);
            return false;
        }
        else if (!hasMatchingSubStat(statRequestCore)) {
            outputManager.sendFeedbackMsgWrongSubStat(sender, type, statRequestCore.getSubStatEntryName());
            return false;
        }
        else if (statRequestCore.getTarget() == Target.PLAYER && statRequestCore.getPlayerName() == null) {
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
    private void patchRequest(StatRequestCore statRequestCore) {
        if (statRequestCore.getStatistic() != null) {
            Statistic.Type type = statRequestCore.getStatistic().getType();

            if (statRequestCore.getPlayerFlag()) {  //unpack the playerFlag
                if (type == Statistic.Type.ENTITY && statRequestCore.getSubStatEntryName() == null) {
                    statRequestCore.setSubStatEntryName("player");
                }
                else {
                    statRequestCore.setTarget(Target.PLAYER);
                }
            }

            String subStatEntry = statRequestCore.getSubStatEntryName();
            switch (type) {  //attempt to convert relevant subStatEntries into their corresponding Enum Constant
                case BLOCK -> {
                    Material block = EnumHandler.getBlockEnum(subStatEntry);
                    if (block != null) statRequestCore.setBlock(block);
                }
                case ENTITY -> {
                    EntityType entity = EnumHandler.getEntityEnum(subStatEntry);
                    if (entity != null) statRequestCore.setEntity(entity);
                }
                case ITEM -> {
                    Material item = EnumHandler.getItemEnum(subStatEntry);
                    if (item != null) statRequestCore.setItem(item);
                }
                case UNTYPED -> {  //remove unnecessary subStatEntries
                    if (subStatEntry != null) statRequestCore.setSubStatEntryName(null);
                }
            }
        }
    }

    private boolean hasMatchingSubStat(StatRequestCore statRequestCore) {
        Statistic.Type type = statRequestCore.getStatistic().getType();
        switch (type) {
            case BLOCK -> {
                return statRequestCore.getBlock() != null;
            }
            case ENTITY -> {
                return statRequestCore.getEntity() != null;
            }
            case ITEM -> {
                return statRequestCore.getItem() != null;
            }
            default -> {
                return true;
            }
        }
    }
}