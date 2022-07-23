package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.msg.OutputManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatManager;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;


public class StatCommand implements CommandExecutor {

    private static ThreadManager threadManager;
    private static OutputManager outputManager;
    private final StatManager statManager;

    public StatCommand(OutputManager m, ThreadManager t, StatManager s) {
        threadManager = t;
        outputManager = m;
        statManager = s;
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
            StatRequest request = statManager.generateRequest(sender, args);
            if (statManager.requestIsValid(request)) {
                threadManager.startStatThread(request);
            } else {
                return false;
            }
        }
        return true;
    }
}