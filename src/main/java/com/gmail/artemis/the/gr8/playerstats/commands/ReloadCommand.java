package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.ComponentFactory;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OutputFormatter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    private final BukkitAudiences adventure;
    private final ConfigHandler config;
    private final OfflinePlayerHandler offlinePlayerHandler;
    private final OutputFormatter outputFormatter;
    private final Main plugin;


    public ReloadCommand(BukkitAudiences b, ConfigHandler c, OfflinePlayerHandler of, OutputFormatter o, Main p) {
        adventure = b;
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

            TextComponent t = Component.text("Hello :D").color(TextColor.fromHexString("#fc4e03"));
            TextComponent subt = Component.text("Red ").color(NamedTextColor.RED)
                    .append(Component.text(" - for comparison - ").color(TextColor.fromHexString("#fc4e03")))
                    .append(Component.text(" Gold").color(NamedTextColor.GOLD));
            Title title = Title.title(t, subt);
            adventure.player(offlinePlayerHandler.getOfflinePlayerUUID("Artemis_the_gr8")).showTitle(title);
            adventure.sender(sender).sendMessage(ComponentFactory.helpMsg());
            sender.sendMessage(OutputFormatter.getPluginPrefix() + ChatColor.GREEN + "Config reloaded!");
            return true;
        }
        return false;
    }
}
