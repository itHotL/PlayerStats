package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
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
            boolean doublePlayerFlag = false;

            for (String arg : args) {
                if (EnumHandler.getStatNames().contains(arg)) {
                    stat = EnumHandler.getStatistic(arg.toUpperCase());
                }
                else if (EnumHandler.getBlockNames().contains(arg)) {
                    block = EnumHandler.getBlock(arg);
                }
                else if (EnumHandler.getItemNames().contains(arg)) {
                    item = EnumHandler.getItem(arg);
                }
                else if (EnumHandler.getEntityNames().contains(arg)) {
                    entity = EnumHandler.getEntityType(arg);
                }
                else if (arg.equalsIgnoreCase("me") && sender instanceof Player) {
                    playerName = sender.getName();
                }
                else if (arg.equalsIgnoreCase("player")) {
                    if (!playerFlag) playerFlag = true;
                    else doublePlayerFlag = true;
                }
                else if (OfflinePlayerHandler.getAllOfflinePlayerNames().stream().anyMatch(arg::equalsIgnoreCase)) {
                    playerName = arg;
                }
            }
            if (playerName != null && stat != null) {
                if (stat.getType() == Statistic.Type.UNTYPED) {
                    sender.sendMessage(stat + " for " + playerName + ": " + OfflinePlayerHandler.getOfflinePlayer(playerName).getStatistic(stat));
                }
            }

        }
        return true;
    }
}
