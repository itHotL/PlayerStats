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

import java.time.Instant;

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
        plugin.getLogger().info("onCommand 33: " + time);
        if (args.length >= 2) {

            String statName = null;
            String subStatEntry = null;
            String playerName = null;
            boolean playerFlag = false;

            plugin.getLogger().info("onCommand 40: " + (System.currentTimeMillis() - time));
            time = System.currentTimeMillis();

            //all args are in lowercase
            for (String arg : args) {
                if (statManager.isStatistic(arg)) {
                    statName = (statName == null) ? arg : statName;
                    plugin.getLogger().info("onCommand 48: " + (System.currentTimeMillis() - time));
                    time = System.currentTimeMillis();
                }
                else if (statManager.isSubStatEntry(arg)) {
                    if (arg.equalsIgnoreCase("player")) {
                        if (!playerFlag) {
                            subStatEntry = (subStatEntry == null) ? arg : subStatEntry;
                            playerFlag = true;
                            plugin.getLogger().info("onCommand 56: " + (System.currentTimeMillis() - time));
                            time = System.currentTimeMillis();
                        }
                    }
                    else {
                        subStatEntry = (subStatEntry == null || playerFlag) ? arg : subStatEntry;
                        plugin.getLogger().info("onCommand 62: " + (System.currentTimeMillis() - time));
                        time = System.currentTimeMillis();
                    }
                }

                else if (arg.equalsIgnoreCase("me") && sender instanceof Player) {
                    playerName = sender.getName();
                    plugin.getLogger().info("onCommand 69: " + (System.currentTimeMillis() - time));
                    time = System.currentTimeMillis();
                }
                else if (offlinePlayerHandler.isOfflinePlayerName(arg)) {
                    playerName = (playerName == null) ? arg : playerName;
                    plugin.getLogger().info("onCommand 74: " + (System.currentTimeMillis() - time));
                    time = System.currentTimeMillis();
                }
            }
            if (playerName != null && statName != null) {
                plugin.getLogger().info("onCommand 79: " + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();
                subStatEntry = statManager.isMatchingSubStatEntry(statName, subStatEntry) ? subStatEntry : null;
                plugin.getLogger().info("onCommand 82: " + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();
                try {
                    plugin.getLogger().info("onCommand 85: " + (System.currentTimeMillis() - time));
                    time = System.currentTimeMillis();
                    sender.sendMessage(outputFormatter.formatPlayerStat(playerName, statName, subStatEntry,
                            statManager.getStatistic(statName, subStatEntry, playerName)));
                    plugin.getLogger().info("onCommand 89: " + (System.currentTimeMillis() - time));
                    time = System.currentTimeMillis();
                }
                catch (Exception e) {
                    sender.sendMessage(e.toString());
                }

            }
        }
        plugin.getLogger().info("onCommand 98: " + (System.currentTimeMillis() - time));
        return true;
    }
}
