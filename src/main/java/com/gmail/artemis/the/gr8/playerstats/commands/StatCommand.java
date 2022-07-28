package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.StatRequestCore;
import com.gmail.artemis.the.gr8.playerstats.msg.OutputManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.InternalStatFetcher;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;


public final class StatCommand implements CommandExecutor {

    private static ThreadManager threadManager;
    private static OutputManager outputManager;
    private final InternalStatFetcher internalStatFetcher;

    public StatCommand(OutputManager m, ThreadManager t, InternalStatFetcher r) {
        threadManager = t;
        outputManager = m;
        internalStatFetcher = r;
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
        else {
            StatRequestCore statRequestCore = internalStatFetcher.generateRequest(sender, args);
            if (internalStatFetcher.validateRequest(statRequestCore)) {
                threadManager.startStatThread(statRequestCore);
            } else {
                return false;
            }
        }
        return true;
    }
}