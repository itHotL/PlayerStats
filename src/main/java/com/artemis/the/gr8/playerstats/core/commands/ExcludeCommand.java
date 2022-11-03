package com.artemis.the.gr8.playerstats.core.commands;

import com.artemis.the.gr8.playerstats.core.msg.OutputManager;
import com.artemis.the.gr8.playerstats.core.utils.OfflinePlayerHandler;
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
        if (args.length >= 1) {
            switch (args[0]) {
                case "list" -> {
                    ArrayList<String> excludedPlayers = offlinePlayerHandler.getExcludedPlayerNames();
                    sender.sendMessage(String.valueOf(excludedPlayers));
                    return true;
                }
                case "info" -> {
                    outputManager.sendExcludeInfo(sender);
                    return true;
                }
                case "test" -> {
                    if (args.length >= 3) {
                        switch (args[1]) {
                            case "help" -> outputManager.sendHelpTest(sender, args[2]);
                            case "examples" -> outputManager.sendExampleTest(sender, args[2]);
                            case "exclude" -> outputManager.sendExcludeTest(sender, args[2]);
                            case "prefix" -> outputManager.sendPrefixTest(sender, args[2]);
                            case "title" -> outputManager.sendPrefixTitleTest(sender, args[2]);
                            case "name" -> {
                                if (args.length >= 4) {
                                    outputManager.sendNameTest(sender, args[2], args[3]);
                                }
                            }
                        }
                    }
                    return true;
                }
                case "add" -> {
                    if (args.length >= 2 &&
                        offlinePlayerHandler.isLoadedPlayer(args[1])) {
                        offlinePlayerHandler.addLoadedPlayerToExcludeList(args[1]);
                        sender.sendMessage("Excluded " + args[1] + "!");
                        return true;
                    }
                }
                case "remove" -> {
                    if (args.length >= 2 &&
                        offlinePlayerHandler.isExcludedPlayer(args[1])) {
                        offlinePlayerHandler.addExcludedPlayerToLoadedList(args[1]);
                        sender.sendMessage("Removed " + args[1] + " from the exclude list again!");
                        return true;
                    }
                }
            }
        }
        return false;
    }
}