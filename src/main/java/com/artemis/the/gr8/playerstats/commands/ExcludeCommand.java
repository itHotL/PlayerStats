package com.artemis.the.gr8.playerstats.commands;


import com.artemis.the.gr8.playerstats.msg.OutputManager;
import com.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class ExcludeCommand implements CommandExecutor {

    private static OutputManager outputManager;
    private final OfflinePlayerHandler offlinePlayerHandler;

    public ExcludeCommand(OutputManager outputManager) {
        ExcludeCommand.outputManager = outputManager;
        this.offlinePlayerHandler = OfflinePlayerHandler.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            ArrayList<String> excludedPlayers = offlinePlayerHandler.getExcludedPlayerNames();
            sender.sendMessage(String.valueOf(excludedPlayers));
            return true;
        }
        //this is going to return false for all UUIDs in file at boot-up - that's an issue
        else if (args.length >= 2) {

            String playerName = args[1];
            switch (args[0]) {
                case "add" -> {
                    if (offlinePlayerHandler.isLoadedPlayer(playerName)) {
                        offlinePlayerHandler.addLoadedPlayerToExcludeList(playerName);
                        sender.sendMessage("Excluded " + playerName + "!");
                        return true;
                    }
                }
                case "remove" -> {
                    if (offlinePlayerHandler.isExcludedPlayer(playerName)) {
                        offlinePlayerHandler.addExcludedPlayerToLoadedList(playerName);
                        sender.sendMessage("Removed " + playerName + " from the exclude list again!");
                        return true;
                    }
                }
                case "info" -> {
                    boolean isExcluded = offlinePlayerHandler.isExcludedPlayer(playerName);
                    sender.sendMessage(playerName+ " is excluded: " + isExcluded);
                    return true;
                }
            }
        }
        return false;
    }
}
