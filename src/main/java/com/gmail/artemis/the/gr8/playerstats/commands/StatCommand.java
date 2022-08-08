package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.enums.StandardMessage;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.RequestSettings;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.RequestHandler;
import com.gmail.artemis.the.gr8.playerstats.msg.OutputManager;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class StatCommand implements CommandExecutor {

    private static ThreadManager threadManager;
    private static OutputManager outputManager;

    public StatCommand(OutputManager m, ThreadManager t) {
        threadManager = t;
        outputManager = m;
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
            RequestSettings baseRequest = RequestHandler.getBasicInternalStatRequest(sender);
            RequestHandler requestHandler = new RequestHandler(baseRequest);

            RequestSettings completedRequest = requestHandler.getRequestFromArgs(args);
            if (completedRequest.isValid()) {
                threadManager.startStatThread(completedRequest);
            } else {
                sendFeedback(completedRequest);
                return false;
            }
        }
        return true;
    }

    /** If a given {@link RequestSettings} does not result in a valid statistic look-up,
     this will send a feedback message to the CommandSender that made the request.
     <br> The following is checked:
     <ul>
     <li>Is a <code>statistic</code> set?
     <li>Is a <code>subStatEntry</code> needed, and if so, is a corresponding Material/EntityType present?
     <li>If the <code>target</code> is Player, is a valid <code>playerName</code> provided?
     </ul>
     @param requestSettings the RequestSettings to give feedback on
     */
    private void sendFeedback(RequestSettings requestSettings) {
        CommandSender sender = requestSettings.getCommandSender();

        if (requestSettings.getStatistic() == null) {
            outputManager.sendFeedbackMsg(sender, StandardMessage.MISSING_STAT_NAME);
        }
        else if (requestSettings.getTarget() == Target.PLAYER && requestSettings.getPlayerName() == null) {
            outputManager.sendFeedbackMsg(sender, StandardMessage.MISSING_PLAYER_NAME);
        }
        else {
            Statistic.Type type = requestSettings.getStatistic().getType();
            if (type != Statistic.Type.UNTYPED && requestSettings.getSubStatEntryName() == null) {
                outputManager.sendFeedbackMsgMissingSubStat(sender, type);
            } else {
                outputManager.sendFeedbackMsgWrongSubStat(sender, type, requestSettings.getSubStatEntryName());
            }
        }
    }
}