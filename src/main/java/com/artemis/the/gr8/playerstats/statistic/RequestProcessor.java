package com.artemis.the.gr8.playerstats.statistic;

import com.artemis.the.gr8.playerstats.ThreadManager;
import com.artemis.the.gr8.playerstats.msg.FormattingFunction;
import com.artemis.the.gr8.playerstats.msg.OutputManager;
import com.artemis.the.gr8.playerstats.share.ShareManager;
import com.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.artemis.the.gr8.playerstats.utils.MyLogger;
import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public final class RequestProcessor {

    private final OfflinePlayerHandler offlinePlayerHandler;
    private static OutputManager outputManager;
    private static ShareManager shareManager;

    public RequestProcessor(OfflinePlayerHandler offlinePlayerHandler, OutputManager outputManager, ShareManager shareManager) {
        this.offlinePlayerHandler = offlinePlayerHandler;
        RequestProcessor.outputManager = outputManager;
        RequestProcessor.shareManager = shareManager;
    }

    public @NotNull StatResult<Integer> processPlayerRequest(StatRequest.Settings requestSettings) {
        int stat = getPlayerStat(requestSettings);
        FormattingFunction formattingFunction = outputManager.formatPlayerStat(requestSettings, stat);
        TextComponent formattedResult = processFunction(requestSettings.getCommandSender(), formattingFunction);
        String resultAsString = outputManager.textComponentToString(formattedResult);

        return new StatResult<>(stat, formattedResult, resultAsString);
    }

    public @NotNull StatResult<Long> processServerRequest(StatRequest.Settings requestSettings) {
        long stat = getServerStat(requestSettings);
        FormattingFunction formattingFunction = outputManager.formatServerStat(requestSettings, stat);
        TextComponent formattedResult = processFunction(requestSettings.getCommandSender(), formattingFunction);
        String resultAsString = outputManager.textComponentToString(formattedResult);

        return new StatResult<>(stat, formattedResult, resultAsString);
    }

    public @NotNull StatResult<LinkedHashMap<String, Integer>> processTopRequest(StatRequest.Settings requestSettings) {
        LinkedHashMap<String, Integer> stats = getTopStats(requestSettings);
        FormattingFunction formattingFunction = outputManager.formatTopStats(requestSettings, stats);
        TextComponent formattedResult = processFunction(requestSettings.getCommandSender(), formattingFunction);
        String resultAsString = outputManager.textComponentToString(formattedResult);

        return new StatResult<>(stats, formattedResult, resultAsString);
    }

    private int getPlayerStat(@NotNull StatRequest.Settings requestSettings) {
        OfflinePlayer player = offlinePlayerHandler.getOfflinePlayer(requestSettings.getPlayerName());
        return switch (requestSettings.getStatistic().getType()) {
            case UNTYPED -> player.getStatistic(requestSettings.getStatistic());
            case ENTITY -> player.getStatistic(requestSettings.getStatistic(), requestSettings.getEntity());
            case BLOCK -> player.getStatistic(requestSettings.getStatistic(), requestSettings.getBlock());
            case ITEM -> player.getStatistic(requestSettings.getStatistic(), requestSettings.getItem());
        };
    }

    private long getServerStat(StatRequest.Settings requestSettings) {
        List<Integer> numbers = getAllStatsAsync(requestSettings)
                .values()
                .parallelStream()
                .toList();
        return numbers.parallelStream().mapToLong(Integer::longValue).sum();
    }

    private LinkedHashMap<String, Integer> getTopStats(StatRequest.Settings requestSettings) {
        return getAllStatsAsync(requestSettings).entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(requestSettings.getTopListSize())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private TextComponent processFunction(CommandSender sender, FormattingFunction function) {
        if (outputShouldBeStored(sender)) {
            int shareCode = shareManager.saveStatResult(sender.getName(), function.getResultWithSharerName(sender));
            return function.getResultWithShareButton(shareCode);
        }
        return function.getDefaultResult();
    }

    private boolean outputShouldBeStored(CommandSender sender) {
        return !(sender instanceof ConsoleCommandSender) &&
                ShareManager.isEnabled() &&
                shareManager.senderHasPermission(sender);
    }

    /**
     * Invokes a bunch of worker pool threads to get the statistics for
     * all players that are stored in the {@link OfflinePlayerHandler}).
     */
    private @NotNull ConcurrentHashMap<String, Integer> getAllStatsAsync(StatRequest.Settings requestSettings) {
        long time = System.currentTimeMillis();

        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        ConcurrentHashMap<String, Integer> allStats;

        try {
            allStats = commonPool.invoke(getStatTask(requestSettings));
        } catch (ConcurrentModificationException e) {
            MyLogger.logWarning("The requestSettings could not be executed due to a ConcurrentModificationException. " +
                    "This likely happened because Bukkit hasn't fully initialized all player-data yet. " +
                    "Try again and it should be fine!");
            throw new ConcurrentModificationException(e.toString());
        }

        MyLogger.actionFinished();
        ThreadManager.recordCalcTime(System.currentTimeMillis() - time);
        MyLogger.logMediumLevelTask("Calculated all stats", time);

        return allStats;
    }

    private @NotNull StatAction getStatTask(StatRequest.Settings requestSettings) {
        int size = offlinePlayerHandler.getOfflinePlayerCount() != 0 ? offlinePlayerHandler.getOfflinePlayerCount() : 16;
        ConcurrentHashMap<String, Integer> allStats = new ConcurrentHashMap<>(size);
        ImmutableList<String> playerNames = ImmutableList.copyOf(offlinePlayerHandler.getOfflinePlayerNames());

        StatAction task = new StatAction(offlinePlayerHandler, playerNames, requestSettings, allStats);
        MyLogger.actionCreated(playerNames.size());

        return task;
    }
}