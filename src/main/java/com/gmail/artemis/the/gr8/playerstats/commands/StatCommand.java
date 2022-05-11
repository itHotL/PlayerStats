package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.StatManager;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OutputFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;


public class StatCommand implements CommandExecutor {

    private final OfflinePlayerHandler offlinePlayerHandler;
    private final OutputFormatter outputFormatter;
    private final StatManager statManager;
    private final Main plugin;

    public StatCommand(OutputFormatter o, StatManager s, Main p) {
        outputFormatter = o;
        statManager = s;
        plugin = p;

        offlinePlayerHandler = OfflinePlayerHandler.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        long time = System.currentTimeMillis();

        //part 1: collecting all relevant information from the args
        if (args.length >= 2) {
            String statName = null;
            String subStatEntry = null;
            String playerName = null;
            boolean playerFlag = false;
            boolean topFlag = false;

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

                else if (arg.equalsIgnoreCase("top")) {
                    topFlag = true;
                }
                else if (arg.equalsIgnoreCase("me") && sender instanceof Player) {
                    playerName = sender.getName();
                }
                else if (offlinePlayerHandler.isOfflinePlayerName(arg)) {
                    playerName = (playerName == null) ? arg : playerName;
                }
            }

            //part 2: sending the information to the StatManager
            if (statName != null) {
                subStatEntry = statManager.isMatchingSubStatEntry(statName, subStatEntry) ? subStatEntry : null;

                if (topFlag) {
                    try {
                        LinkedHashMap<String, Integer> topStats = statManager.getTopStatistics(statName, subStatEntry);
                        return true;
                    }
                    catch (Exception e) {
                        sender.sendMessage(outputFormatter.formatExceptions(e.toString()));
                    }

                }

                else if (playerName != null) {
                    try {
                        sender.sendMessage(outputFormatter.formatPlayerStat(playerName, statName, subStatEntry, statManager.getStatistic
                                        (statName, subStatEntry, playerName)));
                    }
                    catch (Exception e) {
                        sender.sendMessage(outputFormatter.formatExceptions(e.toString()));
                    }
                }
            }
        }
        plugin.logTimeTaken("StatCommand", "onCommand", time, 90);
        return true;
    }

}
