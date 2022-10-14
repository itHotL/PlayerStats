package com.artemis.the.gr8.playerstats.commands;

import com.artemis.the.gr8.playerstats.ThreadManager;
import com.artemis.the.gr8.playerstats.enums.StandardMessage;
import com.artemis.the.gr8.playerstats.enums.Target;
import com.artemis.the.gr8.playerstats.msg.OutputManager;
import com.artemis.the.gr8.playerstats.statistic.InternalStatRequest;
import com.artemis.the.gr8.playerstats.statistic.PlayerStatRequest;
import com.artemis.the.gr8.playerstats.statistic.StatRequest;
import com.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatCommand implements CommandExecutor {

    private static ThreadManager threadManager;
    private static OutputManager outputManager;
    private OfflinePlayerHandler offlinePlayerHandler;
    private EnumHandler enumHandler;

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
            StatRequest<TextComponent> request = new InternalStatRequest(sender, args);
            if (request.isValid()) {
                threadManager.startStatThread(request);
            } else {
                sendFeedback(sender, request);
                return false;
            }
        }
        return true;
    }

    private final class ArgProcessor {

        private String[] argsToProcess;
        private Statistic statistic;
        private String subStatistic;

        private ArgProcessor(CommandSender sender, String[] args) {
            argsToProcess = args;
            process(sender);
        }

        private StatRequest<?> process(CommandSender sender) {
            Pattern pattern = Pattern.compile("top|server|me|player");
            extractStatistic();

            String playerName = tryToFindPlayerName(argsToProcess);

            for (String arg : argsToProcess) {
                Matcher matcher = pattern.matcher(arg);
                if (matcher.find()) {
                    switch (matcher.group()) {
                        case "player" -> {
                            if (playerName != null || containsPlayerTwice(argsToProcess)) {
                                new PlayerStatRequest(playerName);
                            }
                        }
                    }
                }
            }
        }

        private void extractStatistic() {
            String statName = null;
            for (String arg : argsToProcess) {
                if (enumHandler.isStatistic(arg)) {
                    statName = arg;
                    break;
                }
            }
            if (statName != null) {
                statistic = EnumHandler.getStatEnum(statName);
                argsToProcess = removeArg(argsToProcess, statName);
            }
        }

        private void extractSubStatistic() {
            if (statistic == null ||
                statistic.getType() == Statistic.Type.UNTYPED ||
                argsToProcess.length == 0) {
                return;
            }

            for (String arg : argsToProcess) {

            }
        }

        @Contract(pure = true)
        private @Nullable String tryToFindPlayerName(@NotNull String[] args) {
            for (String arg : args) {
                if (offlinePlayerHandler.isRelevantPlayer(arg)) {
                    return arg;
                }
            }
            return null;
        }

        private boolean containsPlayerTwice(String[] args) {
            return Arrays.stream(args)
                    .filter(arg -> arg.equalsIgnoreCase("player"))
                    .toList()
                    .size() >= 2;
        }

        private String[] removeArg(@NotNull String[] args, String argToRemove) {
            ArrayList<String> currentArgs = new ArrayList<>(Arrays.asList(args));
            currentArgs.remove(argToRemove);
            return currentArgs.toArray(String[]::new);
        }

    }

    /**
     * If a given {@link StatRequest} object does not result in a valid
     * statistic look-up, this will send a feedback message to the CommandSender
     * that made the request. The following is checked:
     * <ul>
     * <li>Is a <code>statistic</code> set?
     * <li>Is a <code>subStatEntry</code> needed, and if so, is a corresponding Material/EntityType present?
     * <li>If the <code>target</code> is Player, is a valid <code>playerName</code> provided?
     * </ul>
     *
     * @param sender the CommandSender to send feedback to
     * @param request the StatRequest to give feedback on
     */
    private void sendFeedback(CommandSender sender, StatRequest<?> request) {
        StatRequest.Settings settings = request.getSettings();

        if (settings.getStatistic() == null) {
            outputManager.sendFeedbackMsg(sender, StandardMessage.MISSING_STAT_NAME);
        }
        else if (settings.getTarget() == Target.PLAYER && settings.getPlayerName() == null) {
            outputManager.sendFeedbackMsg(sender, StandardMessage.MISSING_PLAYER_NAME);
        }
        else {
            Statistic.Type type = settings.getStatistic().getType();
            if (type != Statistic.Type.UNTYPED && settings.getSubStatEntryName() == null) {
                outputManager.sendFeedbackMsgMissingSubStat(sender, type);
            } else {
                outputManager.sendFeedbackMsgWrongSubStat(sender, type, settings.getSubStatEntryName());
            }
        }
    }
}