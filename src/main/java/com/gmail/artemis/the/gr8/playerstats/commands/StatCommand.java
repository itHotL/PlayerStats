package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.StatManager;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OutputFormatter;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StatCommand implements CommandExecutor {

    private final EnumHandler enumHandler;
    private final StatManager statManager;

    public StatCommand(EnumHandler e, StatManager s) {
        enumHandler = e;
        statManager = s;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length >= 2) {

            String statName = null;
            String blockName = null;
            String itemName = null;
            String entityName = null;
            Material block = null;
            Material item = null;
            EntityType entity = null;
            String playerName = null;
            boolean playerFlag = false;

            //all args are in lowercase
            for (String arg : args) {
                if (statManager.isStatistic(arg)) {
                    statName = arg;
                }
                else if (enumHandler.isBlock(arg)) {
                    blockName = arg;
                }
                else if (enumHandler.isItem(arg)) {
                    itemName = arg;
                }

                else if (enumHandler.isEntityType(arg)) {
                    if (arg.equalsIgnoreCase("player")) {
                        if (!playerFlag) {
                            entityName = (entityName == null) ? arg : entityName;
                            playerFlag = true;
                        }
                        else {
                            entityName = arg;
                        }
                    }
                    else {
                        entityName = arg;
                    }
                }

                else if (arg.equalsIgnoreCase("me") && sender instanceof Player) {
                    playerName = sender.getName();
                }
                else if (OfflinePlayerHandler.isOfflinePlayer(arg)) {
                    playerName = arg;
                }
            }
            if (playerName != null && statName != null) {
                switch (statManager.getStatType(statName)) {
                    case UNTYPED:
                        sender.sendMessage(OutputFormatter.formatPlayerStat(playerName, statName, OfflinePlayerHandler.getOfflinePlayer(playerName).getStatistic(stat)));
                        break;
                    case BLOCK:
                        if (block != null) {
                            sender.sendMessage(statName + " " + block + " for " + playerName + ": " + OfflinePlayerHandler.getOfflinePlayer(playerName).getStatistic(stat, block));
                        }
                        break;
                    case ITEM:
                        if (item != null) {
                            sender.sendMessage(statName + " " + item + " for " + playerName + ": " + OfflinePlayerHandler.getOfflinePlayer(playerName).getStatistic(stat, item));
                        }
                    case ENTITY:
                        if (entity != null) {
                            sender.sendMessage(statName + " " + entity + " for " + playerName + ": " + OfflinePlayerHandler.getOfflinePlayer(playerName).getStatistic(stat, entity));
                        }

                }
            }
        }
        return true;
    }
}
