package com.gmail.artemis.the.gr8.playerstats.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,String label, String[] args) {

        if (label.equalsIgnoreCase("statistic")) {
            sender.sendMessage("hello");

            return true;
        }
        sender.sendMessage("bye");
        return false;
    }
}
