package com.artemis.the.gr8.playerstats.commands;


import com.artemis.the.gr8.playerstats.utils.MyLogger;
import com.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ExcludeCommand implements CommandExecutor {

    private final OfflinePlayerHandler offlinePlayerHandler;

    public ExcludeCommand() {
        this.offlinePlayerHandler = OfflinePlayerHandler.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            List<String> excludedPlayers = offlinePlayerHandler.getListOfExcludedPlayerNames();

            for (String player : excludedPlayers) {
                MyLogger.logLowLevelMsg(player);
            }
        }
        //this is going to return false for all UUIDs in file at boot-up - that's an issue
        else if (args.length >= 2 && offlinePlayerHandler.isLoadedPlayer(args[1])) {
            String playerName = args[1];
            OfflinePlayer player = offlinePlayerHandler.getOfflinePlayer(playerName);

            switch (args[0]) {
                case "add" -> offlinePlayerHandler.addPlayerToExcludeList(player.getUniqueId());
                case "remove" -> offlinePlayerHandler.removePlayerFromExcludeList(player.getUniqueId());
                case "info" -> {
                    boolean isExcluded = offlinePlayerHandler.isExcluded(player.getUniqueId());
                    MyLogger.logLowLevelMsg(player.getName() + " is excluded: " + isExcluded);
                }
            }
        }
        return false;
    }
}
