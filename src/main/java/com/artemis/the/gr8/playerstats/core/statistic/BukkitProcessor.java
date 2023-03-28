package com.artemis.the.gr8.playerstats.core.statistic;

import com.artemis.the.gr8.playerstats.api.StatRequest;
import com.artemis.the.gr8.playerstats.api.StatResult;
import com.artemis.the.gr8.playerstats.core.config.ConfigHandler;
import com.artemis.the.gr8.playerstats.core.msg.OutputManager;
import com.artemis.the.gr8.playerstats.core.msg.msgutils.FormattingFunction;
import com.artemis.the.gr8.playerstats.core.multithreading.ThreadManager;
import com.artemis.the.gr8.playerstats.core.sharing.ShareManager;
import com.artemis.the.gr8.playerstats.core.utils.MyLogger;
import com.artemis.the.gr8.playerstats.core.utils.OfflinePlayerHandler;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

final class BukkitProcessor extends RequestProcessor {

    private final OutputManager outputManager;
    private final ConfigHandler config;
    private final ShareManager shareManager;
    private final OfflinePlayerHandler offlinePlayerHandler;

    public BukkitProcessor(OutputManager outputManager) {
        this.outputManager = outputManager;

        config = ConfigHandler.getInstance();
        shareManager = ShareManager.getInstance();
        offlinePlayerHandler = OfflinePlayerHandler.getInstance();
    }

    @Override
    public @NotNull StatResult<Integer> processPlayerRequest(StatRequest<?> playerStatRequest) {
        StatRequest.Settings requestSettings = playerStatRequest.getSettings();
        int stat = getPlayerStat(requestSettings);
        FormattingFunction formattingFunction = outputManager.formatPlayerStat(requestSettings, stat);
        TextComponent formattedResult = processFunction(requestSettings.getCommandSender(), formattingFunction);
        String resultAsString = outputManager.textComponentToString(formattedResult);

        return new StatResult<>(stat, formattedResult, resultAsString);
    }

    @Override
    public @NotNull StatResult<Long> processServerRequest(StatRequest<?> serverStatRequest) {
        StatRequest.Settings requestSettings = serverStatRequest.getSettings();
        long stat = getServerStat(requestSettings);
        FormattingFunction formattingFunction = outputManager.formatServerStat(requestSettings, stat);
        TextComponent formattedResult = processFunction(requestSettings.getCommandSender(), formattingFunction);
        String resultAsString = outputManager.textComponentToString(formattedResult);

        return new StatResult<>(stat, formattedResult, resultAsString);
    }

    @Override
    public @NotNull StatResult<LinkedHashMap<String, Integer>> processTopRequest(StatRequest<?> topStatRequest) {
        StatRequest.Settings requestSettings = topStatRequest.getSettings();
        LinkedHashMap<String, Integer> stats = getTopStats(requestSettings);
        FormattingFunction formattingFunction = outputManager.formatTopStats(requestSettings, stats);
        TextComponent formattedResult = processFunction(requestSettings.getCommandSender(), formattingFunction);
        String resultAsString = outputManager.textComponentToString(formattedResult);

        return new StatResult<>(stats, formattedResult, resultAsString);
    }

    private int getPlayerStat(@NotNull StatRequest.Settings requestSettings) {
        OfflinePlayer player;
        if (offlinePlayerHandler.isExcludedPlayer(requestSettings.getPlayerName()) &&
                config.allowPlayerLookupsForExcludedPlayers()) {
            player = offlinePlayerHandler.getExcludedOfflinePlayer(requestSettings.getPlayerName());
        } else {
            player = offlinePlayerHandler.getIncludedOfflinePlayer(requestSettings.getPlayerName());
        }
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
                shareManager.isEnabled() &&
                shareManager.senderHasPermission(sender);
    }

    /**
     * Invokes a bunch of worker pool threads to get the statistics for all players that are stored in the
     * {@link OfflinePlayerHandler}).
     */
    private @NotNull ConcurrentHashMap<String, Integer> getAllStatsAsync(StatRequest.Settings requestSettings) {
        long time = System.currentTimeMillis();

        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        ConcurrentHashMap<String, Integer> allStats;

        try {
            allStats = commonPool.invoke(ThreadManager.getStatAction(requestSettings));
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
}
