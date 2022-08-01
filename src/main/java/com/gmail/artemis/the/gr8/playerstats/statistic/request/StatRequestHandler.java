package com.gmail.artemis.the.gr8.playerstats.statistic.request;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
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

public record StatRequestHandler(StatRequest statRequest) implements RequestGenerator {

    public static StatRequestHandler playerRequestHandler(String playerName) {
        StatRequest request = new StatRequest(Bukkit.getConsoleSender(), true);
        request.setTarget(Target.PLAYER);
        request.setPlayerName(playerName);
        return new StatRequestHandler(request);
    }

    public static StatRequestHandler serverRequestHandler() {
        StatRequest request = new StatRequest(Bukkit.getConsoleSender(), true);
        request.setTarget(Target.SERVER);
        return new StatRequestHandler(request);
    }

    public static StatRequestHandler topRequestHandler(int topListSize) {
        StatRequest request = new StatRequest(Bukkit.getConsoleSender(), true);
        request.setTarget(Target.TOP);
        request.setTopListSize(topListSize != 0 ? topListSize : Main.getConfigHandler().getTopListMaxSize());
        return new StatRequestHandler(request);
    }

    /**
     @param sender the CommandSender that requested this specific statistic
     */
    public static StatRequestHandler internalRequestHandler(CommandSender sender) {
        StatRequest request = new StatRequest(sender);
        return new StatRequestHandler(request);
    }

    @Override
    public StatRequest untyped(@NotNull Statistic statistic) throws IllegalArgumentException {
        if (statistic.getType() == Statistic.Type.UNTYPED) {
            statRequest.setStatistic(statistic);
            return statRequest;
        }
        throw new IllegalArgumentException("This statistic is not of Type.Untyped");
    }

    @Override
    public StatRequest blockOrItemType(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        Statistic.Type type = statistic.getType();
        if (type == Statistic.Type.BLOCK && material.isBlock()) {
            statRequest.setBlock(material);
        }
        else if (type == Statistic.Type.ITEM && material.isItem()){
            statRequest.setItem(material);
        }
        else {
            throw new IllegalArgumentException("Either this statistic is not of Type.Block or Type.Item, or no valid block or item has been provided");
        }
        statRequest.setSubStatEntryName(material.toString());
        return statRequest;
    }

    @Override
    public StatRequest entityType(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        if (statistic.getType() == Statistic.Type.ENTITY) {
            statRequest.setSubStatEntryName(entityType.toString());
            statRequest.setEntity(entityType);
            return statRequest;
        }
        throw new IllegalArgumentException("This statistic is not of Type.Entity");
    }

    /**
     This will create a {@link StatRequest} from the provided args, with the requesting Player (or Console)
     as CommandSender. This CommandSender will receive feedback messages if the StatRequest could not be created.

     @param args an Array of args such as a CommandSender would put in Minecraft chat:
     <p>- a <code>statName</code> (example: "mine_block")</p>
     <p>- if applicable, a <code>subStatEntryName</code> (example: diorite)(</p>
     <p>- a <code>target</code> for this lookup: can be "top", "server", "player" (or "me" to indicate the current CommandSender)</p>
     <p>- if "player" was chosen, include a <code>playerName</code></p>
     @return the generated StatRequest
     */
    public StatRequest getRequestFromArgs(String[] args) {
        EnumHandler enumHandler = Main.getEnumHandler();
        OfflinePlayerHandler offlinePlayerHandler = Main.getOfflinePlayerHandler();
        CommandSender sender = statRequest.getCommandSender();

        for (String arg : args) {
            //check for statName
            if (enumHandler.isStatistic(arg) && statRequest.getStatistic() == null) {
                statRequest.setStatistic(EnumHandler.getStatEnum(arg));
            }
            //check for subStatEntry and playerFlag
            else if (enumHandler.isSubStatEntry(arg)) {
                if (arg.equalsIgnoreCase("player") && !statRequest.getPlayerFlag()) {
                    statRequest.setPlayerFlag(true);
                } else {
                    if (statRequest.getSubStatEntryName() == null) statRequest.setSubStatEntryName(arg);
                }
            }
            //check for selection
            else if (arg.equalsIgnoreCase("top")) {
                statRequest.setTarget(Target.TOP);
            } else if (arg.equalsIgnoreCase("server")) {
                statRequest.setTarget(Target.SERVER);
            } else if (arg.equalsIgnoreCase("me")) {
                if (sender instanceof Player) {
                    statRequest.setPlayerName(sender.getName());
                    statRequest.setTarget(Target.PLAYER);
                } else if (sender instanceof ConsoleCommandSender) {
                    statRequest.setTarget(Target.SERVER);
                }
            } else if (offlinePlayerHandler.isRelevantPlayer(arg) && statRequest.getPlayerName() == null) {
                statRequest.setPlayerName(arg);
                statRequest.setTarget(Target.PLAYER);
            } else if (arg.equalsIgnoreCase("api")) {
                statRequest.setAPIRequest();
            }
        }
        patchRequest(statRequest);
        return statRequest;
    }

    /**
     Adjust the StatRequest object if needed: unpack the playerFlag into a subStatEntry,
     try to retrieve the corresponding Enum Constant for any relevant block/entity/item,
     and remove any unnecessary subStatEntries.
     */
    private void patchRequest(StatRequest statRequest) {
        if (statRequest.getStatistic() != null) {
            Statistic.Type type = statRequest.getStatistic().getType();

            if (statRequest.getPlayerFlag()) {  //unpack the playerFlag
                if (type == Statistic.Type.ENTITY && statRequest.getSubStatEntryName() == null) {
                    statRequest.setSubStatEntryName("player");
                } else {
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
}