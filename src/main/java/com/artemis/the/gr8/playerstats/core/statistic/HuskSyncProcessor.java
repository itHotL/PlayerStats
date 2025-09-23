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
import net.william278.husksync.api.HuskSyncAPI;
import net.william278.husksync.data.Data;
import net.william278.husksync.data.DataSnapshot;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

final class HuskSyncProcessor extends RequestProcessor {

    private final OutputManager outputManager;
    private final ConfigHandler config;
    private final ShareManager shareManager;
    private final OfflinePlayerHandler offlinePlayerHandler;

    private final HuskSyncAPI huskSyncAPI;

    public HuskSyncProcessor(OutputManager outputManager) {
        this.outputManager = outputManager;

        config = ConfigHandler.getInstance();
        shareManager = ShareManager.getInstance();
        offlinePlayerHandler = OfflinePlayerHandler.getInstance();
        huskSyncAPI = HuskSyncAPI.getInstance();
    }

    @Override
    public @NotNull CompletableFuture<StatResult<Integer>> processPlayerRequest(StatRequest<?> playerStatRequest) {
        StatRequest.Settings requestSettings = playerStatRequest.getSettings();

        CompletableFuture<StatResult<Integer>> future = new CompletableFuture<>();

        getPlayerStatHs(requestSettings).thenAccept(stat -> {
            FormattingFunction formattingFunction = outputManager.formatPlayerStat(requestSettings, stat);
            TextComponent formattedResult = processFunction(requestSettings.getCommandSender(), formattingFunction);
            String resultAsString = outputManager.textComponentToString(formattedResult);

            future.complete(new StatResult<>(stat, formattedResult, resultAsString));
        });

        return future;
    }

    @Override
    public @NotNull CompletableFuture<StatResult<Long>> processServerRequest(StatRequest<?> serverStatRequest) {
        StatRequest.Settings requestSettings = serverStatRequest.getSettings();
        long stat = getServerStat(requestSettings);
        FormattingFunction formattingFunction = outputManager.formatServerStat(requestSettings, stat);
        TextComponent formattedResult = processFunction(requestSettings.getCommandSender(), formattingFunction);
        String resultAsString = outputManager.textComponentToString(formattedResult);

        return CompletableFuture.completedFuture(new StatResult<>(stat, formattedResult, resultAsString));
    }

    @Override
    public @NotNull CompletableFuture<StatResult<LinkedHashMap<String, Integer>>> processTopRequest(StatRequest<?> topStatRequest) {
        StatRequest.Settings requestSettings = topStatRequest.getSettings();
        LinkedHashMap<String, Integer> stats = getTopStats(requestSettings);
        FormattingFunction formattingFunction = outputManager.formatTopStats(requestSettings, stats);
        TextComponent formattedResult = processFunction(requestSettings.getCommandSender(), formattingFunction);
        String resultAsString = outputManager.textComponentToString(formattedResult);

        return CompletableFuture.completedFuture(new StatResult<>(stats, formattedResult, resultAsString));
    }

    private CompletableFuture<Integer> getPlayerStatHs(@NotNull StatRequest.Settings requestSettings) {
        OfflinePlayer player;
        if (offlinePlayerHandler.isExcludedPlayer(requestSettings.getPlayerName()) &&
                config.allowPlayerLookupsForExcludedPlayers()) {
            player = offlinePlayerHandler.getExcludedOfflinePlayer(requestSettings.getPlayerName());
        } else {
            player = offlinePlayerHandler.getIncludedOfflinePlayer(requestSettings.getPlayerName());
        }

        UUID uuid = player.getUniqueId();

        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();

        huskSyncAPI.getUser(uuid).thenAccept(optionalUser -> {
            if (optionalUser.isEmpty()) {
                completableFuture.complete(-1);
                return;
            }

            huskSyncAPI.getCurrentData(optionalUser.get()).thenAccept(optionalSnapshot -> {
                if (optionalSnapshot.isEmpty()) {
                    completableFuture.complete(-1);
                    return;
                }

                DataSnapshot.Unpacked snapshot = optionalSnapshot.get();
                Optional<Data.Statistics> optionalStatistics = snapshot.getStatistics();
                if (optionalStatistics.isEmpty()) {
                    completableFuture.complete(-1);
                    return;
                }

                Data.Statistics statistics = optionalStatistics.get();
                completableFuture.complete(huskStatProcessor(statistics, requestSettings));
            });

        });

        return completableFuture;
    }


    private int huskStatProcessor(Data.Statistics huskStats, StatRequest.Settings requestSettings) {
        NamespacedKey nsKey = requestSettings.getStatistic().getKey();
        String key = nsKey.getKey();

        MyLogger.logLowLevelMsg("Requested main-stat: " + key);
        MyLogger.logLowLevelMsg("Statistic type: " + requestSettings.getStatistic().getType().name());

        return switch (requestSettings.getStatistic().getType()) {
            case UNTYPED -> huskStats.getGenericStatistics().get(key);
            case ENTITY ->
                    getStatisticsSafetyValue(huskStats.getEntityStatistics().get(key), requestSettings.getEntity().getName());
            case BLOCK ->
                    getStatisticsSafetyValue(huskStats.getBlockStatistics().get(key), requestSettings.getBlock().name().toLowerCase(Locale.ROOT));
            case ITEM ->
                    getStatisticsSafetyValue(huskStats.getItemStatistics().get(key), requestSettings.getItem().name().toLowerCase(Locale.ROOT));
        };
    }

    private int getStatisticsSafetyValue(Map<String, Integer> statistics, String nameKey) {
        return statistics.getOrDefault(nameKey, 0);
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
