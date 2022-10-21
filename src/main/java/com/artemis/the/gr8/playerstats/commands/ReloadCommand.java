package com.artemis.the.gr8.playerstats.commands;

import com.artemis.the.gr8.playerstats.multithreading.ThreadManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class ReloadCommand implements CommandExecutor {

    private static ThreadManager threadManager;

    public ReloadCommand(ThreadManager t) {
        threadManager = t;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        threadManager.startReloadThread(sender);
        return true;
    }
}