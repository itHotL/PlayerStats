package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.StatManager;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OutputFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
            String subStatEntry = null;
            String playerName = null;
            boolean playerFlag = false;

            //all args are in lowercase
            for (String arg : args) {
                if (statManager.isStatistic(arg)) {
                    statName = (statName == null) ? arg : statName;
                }
                else if (statManager.isSubStatEntry(arg)) {
                    if (arg.equalsIgnoreCase("player")) {
                        if (!playerFlag) {
                            subStatEntry = (subStatEntry == null) ? arg : subStatEntry;
                            playerFlag = true;
                        }
                    }
                    else {
                        subStatEntry = (subStatEntry == null || playerFlag) ? arg : subStatEntry;
                    }
                }

                else if (arg.equalsIgnoreCase("me") && sender instanceof Player) {
                    playerName = sender.getName();
                }
                else if (OfflinePlayerHandler.isOfflinePlayer(arg)) {
                    playerName = (playerName == null) ? arg : playerName;
                }
            }
            if (playerName != null && statName != null) {
                subStatEntry = statManager.isMatchingSubStatEntry(statName, subStatEntry) ? subStatEntry : null;
                try {
                    sender.sendMessage(OutputFormatter.formatPlayerStat(playerName, statName, subStatEntry,
                            statManager.getStatistic(statName, subStatEntry, playerName)));
                }
                catch (Exception e) {
                    sender.sendMessage(e.toString());
                }

            }
        }
        return true;
    }
}
