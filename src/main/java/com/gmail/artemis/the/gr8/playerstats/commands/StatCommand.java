package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.StatManager;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OutputFormatter;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length >= 2) {

            Statistic stat = null;
            Material block = null;
            Material item = null;
            EntityType entity = null;
            String playerName = null;
            boolean playerFlag = false;

            for (String arg : args) {
                if (StatManager.getStatNames().contains(arg)) {
                    stat = StatManager.getStatistic(arg.toUpperCase());
                }
                else if (EnumHandler.getBlockNames().contains(arg)) {
                    block = EnumHandler.getBlock(arg);
                }
                else if (EnumHandler.getItemNames().contains(arg)) {
                    item = EnumHandler.getItem(arg);
                }
                else if (EnumHandler.getEntityNames().contains(arg)) {
                    if (arg.equalsIgnoreCase("player")) {
                        if (!playerFlag) {
                            entity = (entity == null) ? EnumHandler.getEntityType(arg.toUpperCase()) : entity;
                            playerFlag = true;
                        }
                        else {
                            entity = EnumHandler.getEntityType(arg.toUpperCase());
                        }
                    }
                    else {
                        entity = EnumHandler.getEntityType(arg.toUpperCase());
                    }
                }

                else if (arg.equalsIgnoreCase("me") && sender instanceof Player) {
                    playerName = sender.getName();
                }
                else if (OfflinePlayerHandler.getAllOfflinePlayerNames().stream().anyMatch(arg::equalsIgnoreCase)) {
                    playerName = arg;
                }
            }
            if (playerName != null && stat != null) {
                switch (stat.getType()) {
                    case UNTYPED:
                        sender.sendMessage(OutputFormatter.formatPlayerStat(playerName, stat.toString(), OfflinePlayerHandler.getOfflinePlayer(playerName).getStatistic(stat)));
                        break;
                    case BLOCK:
                        if (block != null) {
                            sender.sendMessage(stat + " " + block + " for " + playerName + ": " + OfflinePlayerHandler.getOfflinePlayer(playerName).getStatistic(stat, block));
                        }
                        break;
                    case ITEM:
                        if (item != null) {
                            sender.sendMessage(stat + " " + item + " for " + playerName + ": " + OfflinePlayerHandler.getOfflinePlayer(playerName).getStatistic(stat, item));
                        }
                    case ENTITY:
                        if (entity != null) {
                            sender.sendMessage(stat + " " + entity + " for " + playerName + ": " + OfflinePlayerHandler.getOfflinePlayer(playerName).getStatistic(stat, entity));
                        }

                }
            }
        }
        return true;
    }
}
