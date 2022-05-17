package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OutputFormatter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    private final ConfigHandler config;
    private final OutputFormatter outputFormatter;
    private final Main plugin;

    public ReloadCommand(ConfigHandler c, OutputFormatter o, Main p) {
        outputFormatter = o;
        config = c;
        plugin = p;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (config.reloadConfig()) {
            long time = System.currentTimeMillis();

            outputFormatter.updateOutputColors();
            time = plugin.logTimeTaken("ReloadCommand", "onCommand", time, 33);

            OfflinePlayerHandler.updateOfflinePlayers();
            time = plugin.logTimeTaken("ReloadCommand", "onCommand", time, 36);

            sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
            plugin.logTimeTaken("ReloadCommand", "onCommand", time, 39);
            return true;
        }
        return false;
    }
}
