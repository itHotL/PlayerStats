package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.statistic.ShareManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ShareCommand implements CommandExecutor {

    private static BukkitAudiences adventure;
    private static ShareManager shareManager;

    public ShareCommand(ShareManager s) {
        adventure = Main.adventure();
        shareManager = s;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, String[] args) {
        adventure.all().sendMessage(shareManager.getStatResult(sender.getName(), UUID.fromString(args[0])));
        return true;
    }
}
