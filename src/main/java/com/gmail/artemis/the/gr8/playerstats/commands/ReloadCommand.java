package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.MessageFactory;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    private final ConfigHandler config;
    private final OfflinePlayerHandler offlinePlayerHandler;
    private final MessageFactory messageFactory;
    private final Main plugin;


    public ReloadCommand(ConfigHandler c, OfflinePlayerHandler of, MessageFactory o, Main p) {
        offlinePlayerHandler = of;
        messageFactory = o;
        config = c;
        plugin = p;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (config.reloadConfig()) {
            offlinePlayerHandler.updateOfflinePlayerList();

            plugin.getLogger().info("Amount of players: " + offlinePlayerHandler.getOfflinePlayerCount());
            sender.sendMessage(MessageFactory.getPluginPrefix() + ChatColor.GREEN + "Config reloaded!");
            return true;
        }
        return false;
    }
}
