package com.artemis.the.gr8.playerstats.core.commands;

import com.artemis.the.gr8.playerstats.core.enums.StandardMessage;
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

    public ExcludeCommand() {
        outputManager = OutputManager.getInstance();
        this.offlinePlayerHandler = OfflinePlayerHandler.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            outputManager.sendExcludeInfo(sender);
        }
        else if (args.length == 1) {
            switch (args[0]) {
                case "info" -> outputManager.sendExcludeInfo(sender);
                case "list" -> {
                    ArrayList<String> excludedPlayers = offlinePlayerHandler.getExcludedPlayerNames();
                    outputManager.sendExcludedList(sender, excludedPlayers);
                }
            }
        }
        else {
            switch (args[0]) {
                case "add" -> {
                    if (offlinePlayerHandler.addPlayerToExcludeList(args[1])) {
                        outputManager.sendFeedbackMsgPlayerExcluded(sender, args[1]);
                    } else {
                        outputManager.sendFeedbackMsg(sender, StandardMessage.EXCLUDE_FAILED);
                    }
                }
                case "remove" -> {
                    if (offlinePlayerHandler.removePlayerFromExcludeList(args[1])) {
                        outputManager.sendFeedbackMsgPlayerIncluded(sender, args[1]);
                    } else {
                        outputManager.sendFeedbackMsg(sender, StandardMessage.INCLUDE_FAILED);
                    }
                }
            }
        }
        return true;
    }
}