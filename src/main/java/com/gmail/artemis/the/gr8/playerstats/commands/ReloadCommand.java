package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OutputFormatter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    private final ConfigHandler config;
    private final OutputFormatter outputFormatter;

    public ReloadCommand(ConfigHandler c, OutputFormatter o) {
        outputFormatter = o;
        config = c;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (config.reloadConfig()) {
            outputFormatter.updateOutputColors();
            sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
            return true;
        }
        return false;
    }
}
