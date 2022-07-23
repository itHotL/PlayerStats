package com.gmail.artemis.the.gr8.playerstats.statistic;

import com.gmail.artemis.the.gr8.playerstats.api.RequestHandler;
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

public final class StatManager implements RequestHandler {

    private static volatile StatManager instance;

    private final OfflinePlayerHandler offlinePlayerHandler;
    private static OutputManager outputManager;

    private StatManager(OutputManager output, OfflinePlayerHandler offlinePlayerHandler) {
        this.offlinePlayerHandler = offlinePlayerHandler;
        outputManager = output;
    }

    public static StatManager getInstance(OutputManager outputManager, OfflinePlayerHandler offlinePlayerHandler) {
        StatManager statManager = instance;
        if (statManager != null) {
            return statManager;
        }
        synchronized (StatManager.class) {
            if (instance == null) {
                instance = new StatManager(outputManager, offlinePlayerHandler);
            }
            return instance;
        }
    }

    public StatRequest generateRequest(CommandSender sender, String[] args) {
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

    /** Gets the statistic data for an individual player. If somehow the player
     cannot be found, this returns 0.*/
    public int getPlayerStat(StatRequest request) {
        OfflinePlayer player = offlinePlayerHandler.getOfflinePlayer(request.getPlayerName());
        if (player != null) {
            return switch (request.getStatistic().getType()) {
                case UNTYPED -> player.getStatistic(request.getStatistic());
                case ENTITY -> player.getStatistic(request.getStatistic(), request.getEntity());
                case BLOCK -> player.getStatistic(request.getStatistic(), request.getBlock());
                case ITEM -> player.getStatistic(request.getStatistic(), request.getItem());
            };
        }
        return 0;
    }
}