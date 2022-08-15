package com.gmail.artemis.the.gr8.playerstats.statistic.request;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class RequestHandler {

    private final RequestSettings requestSettings;

    public RequestHandler(RequestSettings request) {
        requestSettings = request;
    }

    public static RequestSettings getBasicPlayerStatRequest(String playerName) {
        RequestSettings request = RequestSettings.getBasicAPIRequest();
        request.setTarget(Target.PLAYER);
        request.setPlayerName(playerName);
        return request;
    }

    public static RequestSettings getBasicServerStatRequest() {
        RequestSettings request = RequestSettings.getBasicAPIRequest();
        request.setTarget(Target.SERVER);
        return request;
    }

    public static RequestSettings getBasicTopStatRequest(int topListSize) {
        RequestSettings request = RequestSettings.getBasicAPIRequest();
        request.setTarget(Target.TOP);
        request.setTopListSize(topListSize != 0 ? topListSize : Main.getConfigHandler().getTopListMaxSize());
        return request;
    }

    /**
     @param sender the CommandSender that requested this specific statistic
     */
    public static RequestSettings getBasicInternalStatRequest(CommandSender sender) {
        RequestSettings request = RequestSettings.getBasicRequest(sender);
        request.setTopListSize(Main.getConfigHandler().getTopListMaxSize());
        return request;
    }

    public RequestSettings untyped(@NotNull Statistic statistic) throws IllegalArgumentException {
        if (statistic.getType() == Statistic.Type.UNTYPED) {
            requestSettings.setStatistic(statistic);
            return requestSettings;
        }
        throw new IllegalArgumentException("This statistic is not of Type.Untyped");
    }

    public RequestSettings blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        Statistic.Type type = statistic.getType();
        if (type == Statistic.Type.BLOCK && material.isBlock()) {
            requestSettings.setBlock(material);
        }
        else if (type == Statistic.Type.ITEM && material.isItem()){
            requestSettings.setItem(material);
        }
        else {
            throw new IllegalArgumentException("Either this statistic is not of Type.Block or Type.Item, or no valid block or item has been provided");
        }
        requestSettings.setStatistic(statistic);
        requestSettings.setSubStatEntryName(material.toString());
        return requestSettings;
    }

    public RequestSettings entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        if (statistic.getType() == Statistic.Type.ENTITY) {
            requestSettings.setStatistic(statistic);
            requestSettings.setSubStatEntryName(entityType.toString());
            requestSettings.setEntity(entityType);
            return requestSettings;
        }
        throw new IllegalArgumentException("This statistic is not of Type.Entity");
    }

    /**
     This will create a {@link RequestSettings} object from the provided args, with the requesting Player (or Console)
     as CommandSender. This CommandSender will receive feedback messages if the RequestSettings could not be created.

     @param args an Array of args such as a CommandSender would put in Minecraft chat:
     <p>- a <code>statName</code> (example: "mine_block")</p>
     <p>- if applicable, a <code>subStatEntryName</code> (example: diorite)(</p>
     <p>- a <code>target</code> for this lookup: can be "top", "server", "player" (or "me" to indicate the current CommandSender)</p>
     <p>- if "player" was chosen, include a <code>playerName</code></p>
     @return the generated RequestSettings
     */
    public RequestSettings getRequestFromArgs(String[] args) {
        EnumHandler enumHandler = Main.getEnumHandler();
        OfflinePlayerHandler offlinePlayerHandler = Main.getOfflinePlayerHandler();
        CommandSender sender = requestSettings.getCommandSender();

        for (String arg : args) {
            //check for statName
            if (enumHandler.isStatistic(arg) && requestSettings.getStatistic() == null) {
                requestSettings.setStatistic(EnumHandler.getStatEnum(arg));
            }
            //check for subStatEntry and playerFlag
            else if (enumHandler.isSubStatEntry(arg)) {
                if (arg.equalsIgnoreCase("player") && !requestSettings.getPlayerFlag()) {
                    requestSettings.setPlayerFlag(true);
                } else {
                    if (requestSettings.getSubStatEntryName() == null) requestSettings.setSubStatEntryName(arg);
                }
            }
            //check for selection
            else if (arg.equalsIgnoreCase("top")) {
                requestSettings.setTarget(Target.TOP);
            } else if (arg.equalsIgnoreCase("server")) {
                requestSettings.setTarget(Target.SERVER);
            } else if (arg.equalsIgnoreCase("me")) {
                if (sender instanceof Player) {
                    requestSettings.setPlayerName(sender.getName());
                    requestSettings.setTarget(Target.PLAYER);
                } else if (sender instanceof ConsoleCommandSender) {
                    requestSettings.setTarget(Target.SERVER);
                }
            } else if (offlinePlayerHandler.isRelevantPlayer(arg) && requestSettings.getPlayerName() == null) {
                requestSettings.setPlayerName(arg);
                requestSettings.setTarget(Target.PLAYER);
            }
        }
        patchRequest(requestSettings);
        return requestSettings;
    }

    /**
     Adjust the RequestSettings object if needed: unpack the playerFlag into a subStatEntry,
     try to retrieve the corresponding Enum Constant for any relevant block/entity/item,
     and remove any unnecessary subStatEntries.
     */
    private void patchRequest(RequestSettings requestSettings) {
        if (requestSettings.getStatistic() != null) {
            Statistic.Type type = requestSettings.getStatistic().getType();

            if (requestSettings.getPlayerFlag()) {  //unpack the playerFlag
                if (type == Statistic.Type.ENTITY && requestSettings.getSubStatEntryName() == null) {
                    requestSettings.setSubStatEntryName("player");
                } else {
                    requestSettings.setTarget(Target.PLAYER);
                }
            }

            String subStatEntry = requestSettings.getSubStatEntryName();
            switch (type) {  //attempt to convert relevant subStatEntries into their corresponding Enum Constant
                case BLOCK -> {
                    Material block = EnumHandler.getBlockEnum(subStatEntry);
                    if (block != null) requestSettings.setBlock(block);
                }
                case ENTITY -> {
                    EntityType entity = EnumHandler.getEntityEnum(subStatEntry);
                    if (entity != null) requestSettings.setEntity(entity);
                }
                case ITEM -> {
                    Material item = EnumHandler.getItemEnum(subStatEntry);
                    if (item != null) requestSettings.setItem(item);
                }
                case UNTYPED -> {  //remove unnecessary subStatEntries
                    if (subStatEntry != null) requestSettings.setSubStatEntryName(null);
                }
            }
        }
    }
}