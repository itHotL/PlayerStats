package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OutputFormatter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    private final ConfigHandler config;
    private final OfflinePlayerHandler offlinePlayerHandler;
    private final OutputFormatter outputFormatter;
    private final Main plugin;


    public ReloadCommand(ConfigHandler c, OfflinePlayerHandler of, OutputFormatter o, Main p) {
        offlinePlayerHandler = of;
        outputFormatter = o;
        config = c;
        plugin = p;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (config.reloadConfig()) {
            outputFormatter.updateOutputColors();
            offlinePlayerHandler.updateOfflinePlayerList();

            plugin.getLogger().info("Amount of players: " + offlinePlayerHandler.getOfflinePlayerCount());
            sender.sendMessage(outputFormatter.getPluginPrefix() + ChatColor.GREEN + "Config reloaded!");
            return true;
        }
        return false;
    }
}
