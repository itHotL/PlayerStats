package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.msg.OutputManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.RequestManager;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.awt.*;


public class StatCommand implements CommandExecutor {

    private static ThreadManager threadManager;
    private static OutputManager outputManager;
    private final RequestManager requestManager;

    public StatCommand(OutputManager m, ThreadManager t, RequestManager r) {
        threadManager = t;
        outputManager = m;
        requestManager = r;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {  //in case of less than 1 argument or "help", display the help message
            outputManager.sendHelp(sender);
        }
        else if (args[0].equalsIgnoreCase("examples") ||
                args[0].equalsIgnoreCase("example")) {  //in case of "statistic examples", show examples
            outputManager.sendExamples(sender);
        }
        else if (args[0].equalsIgnoreCase(">:(")) {
            java.awt.Color color = new java.awt.Color(178, 102, 255);
            ChatColor one = ChatColor.of(color);
            TextComponent msg = new TextComponent(">:(((((");
            msg.setColor(one);
            sender.spigot().sendMessage(msg);
        }
        else {
            StatRequest request = requestManager.generateRequest(sender, args);
            if (requestManager.validateRequest(request)) {
                threadManager.startStatThread(request);
            } else {
                return false;
            }
        }
        return true;
    }
}