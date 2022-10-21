package com.artemis.the.gr8.playerstats.commands;


import com.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class ExcludeCommand implements CommandExecutor {

    private final OfflinePlayerHandler offlinePlayerHandler;

    public ExcludeCommand() {
        this.offlinePlayerHandler = OfflinePlayerHandler.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        return false;
    }
}
