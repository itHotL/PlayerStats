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

public final class RequestManager extends StatRequest implements RequestGenerator {

    private final EnumHandler enumHandler;
    private final OfflinePlayerHandler offlinePlayerHandler;
    private static OutputManager outputManager;

    public RequestManager(EnumHandler enumHandler, OfflinePlayerHandler offlinePlayerHandler, OutputManager outputManager) {
        super(Bukkit.getConsoleSender());
        this.enumHandler = enumHandler;
        this.offlinePlayerHandler = offlinePlayerHandler;
        RequestManager.outputManager = outputManager;
    }

    /** This will create a {@link StatRequest} from the provided args, with the requesting Player (or Console)
     as CommandSender. This CommandSender will receive feedback messages if the StatRequest could not be created.

     @param args an Array of args such as a CommandSender would put in Minecraft chat:
     <p>- a <code>statName</code> (example: "mine_block")</p>
     <p>- if applicable, a <code>subStatEntryName</code> (example: diorite)(</p>
     <p>- a <code>target</code> for this lookup: can be "top", "server", "player" (or "me" to indicate the current CommandSender)</p>
     <p>- if "player" was chosen, include a <code>playerName</code></p>

     @param sender the CommandSender that requested this specific statistic
     @return the generated StatRequest
     */
    public StatRequest generateRequest(CommandSender sender, String[] args) {
        StatRequest request = new StatRequest(sender);
        for (String arg : args) {
            //check for statName
            if (enumHandler.isStatistic(arg) && request.getStatistic() == null) {
                request.setStatistic(EnumHandler.getStatEnum(arg));
            }
            //check for subStatEntry and playerFlag
            else if (enumHandler.isSubStatEntry(arg)) {
                if (arg.equalsIgnoreCase("player") && !request.getPlayerFlag()) {
                    request.setPlayerFlag(true);
                }
                else {
                    if (request.getSubStatEntryName() == null) request.setSubStatEntryName(arg);
                }
            }
            //check for selection
            else if (arg.equalsIgnoreCase("top")) {
                request.setTarget(Target.TOP);
            }
            else if (arg.equalsIgnoreCase("server")) {
                request.setTarget(Target.SERVER);
            }
            else if (arg.equalsIgnoreCase("me")) {
                if (sender instanceof Player) {
                    request.setPlayerName(sender.getName());
                    request.setTarget(Target.PLAYER);
                }
                else if (sender instanceof ConsoleCommandSender) {
                    request.setTarget(Target.SERVER);
                }
            }
            else if (offlinePlayerHandler.isRelevantPlayer(arg) && request.getPlayerName() == null) {
                request.setPlayerName(arg);
                request.setTarget(Target.PLAYER);
            }
            else if (arg.equalsIgnoreCase("api")) {
                request.setAPIRequest();
            }
        }
        patchRequest(request);
        return request;
    }

    @Override
    public StatRequest createPlayerStatRequest(String playerName, Statistic statistic, Material material, EntityType entity) {
        return generateRequest(Target.PLAYER, statistic, material, entity, playerName);
    }

    @Override
    public StatRequest createServerStatRequest(Statistic statistic, Material material, EntityType entityType) {
        return generateRequest(Target.SERVER, statistic, material, entityType, null);
    }

    @Override
    public StatRequest createTopStatRequest(Statistic statistic, Material material, EntityType entityType) {
        return generateRequest(Target.TOP, statistic, material, entityType, null);
    }

    /** This method will generate a {@link StatRequest} for a request arriving through the API.*/
    private StatRequest generateRequest(@NotNull Target selection, @NotNull Statistic statistic, Material material, EntityType entity, String playerName) {
        StatRequest request = new StatRequest(Bukkit.getConsoleSender(), true);
        request.setTarget(selection);
        request.setStatistic(statistic);
        switch (statistic.getType()) {
            case BLOCK -> {
                request.setBlock(material);
                request.setSubStatEntryName(material.toString());
            }
            case ITEM -> {
                request.setItem(material);
                request.setSubStatEntryName(material.toString());
            }
            case ENTITY -> {
                request.setEntity(entity);
                request.setSubStatEntryName(entity.toString());
            }
        }
        if (selection == Target.PLAYER) request.setPlayerName(playerName);
        return request;
    }

    /** Checks if a given {@link StatRequest} would result in a valid statistic look-up,
     and sends a feedback message to the CommandSender that prompted the request if it is invalid.
     <br> The following is checked:
     <ul>
     <li>Is a <code>statistic</code> set?
     <li>Is a <code>subStatEntry</code> needed, and if so, is a corresponding Material/EntityType present?
     <li>If the <code>target</code> is Player, is a valid <code>playerName</code> provided?
     </ul>
     @param request the StatRequest to check
     @return true if the StatRequest is valid, and false otherwise.
     */
    public boolean validateRequest(StatRequest request) {
        return validateRequestAndSendMessage(request, request.getCommandSender());
    }

    /** Checks if a given {@link StatRequest} would result in a valid statistic look-up,
     and sends a feedback message in the server console if it is invalid.
     <br> The following is checked:
     <ul>
     <li>Is a <code>statistic</code> set?
     <li>Is a <code>subStatEntry</code> needed, and if so, is a corresponding Material/EntityType present?
     <li>If the <code>target</code> is Player, is a valid <code>playerName</code> provided?
     </ul>
     @param request the StatRequest to check
     @return true if the StatRequest is valid, and false otherwise.
     */
    public boolean validateAPIRequest(StatRequest request) {
        return validateRequestAndSendMessage(request, Bukkit.getConsoleSender());
    }

    private boolean validateRequestAndSendMessage(StatRequest request, CommandSender sender) {
        if (request.getStatistic() == null) {
            outputManager.sendFeedbackMsg(sender, StandardMessage.MISSING_STAT_NAME);
            return false;
        }
        Statistic.Type type = request.getStatistic().getType();
        if (request.getSubStatEntryName() == null && type != Statistic.Type.UNTYPED) {
            outputManager.sendFeedbackMsgMissingSubStat(sender, type);
            return false;
        }
        else if (!hasMatchingSubStat(request)) {
            outputManager.sendFeedbackMsgWrongSubStat(sender, type, request.getSubStatEntryName());
            return false;
        }
        else if (request.getTarget() == Target.PLAYER && request.getPlayerName() == null) {
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
    private void patchRequest(StatRequest request) {
        if (request.getStatistic() != null) {
            Statistic.Type type = request.getStatistic().getType();

            if (request.getPlayerFlag()) {  //unpack the playerFlag
                if (type == Statistic.Type.ENTITY && request.getSubStatEntryName() == null) {
                    request.setSubStatEntryName("player");
                }
                else {
                    request.setTarget(Target.PLAYER);
                }
            }

            String subStatEntry = request.getSubStatEntryName();
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
                    if (subStatEntry != null) request.setSubStatEntryName(null);
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