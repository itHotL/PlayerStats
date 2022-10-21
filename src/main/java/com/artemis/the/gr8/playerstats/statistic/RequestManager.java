package com.artemis.the.gr8.playerstats.statistic;

import com.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.artemis.the.gr8.playerstats.api.StatManager;
import com.artemis.the.gr8.playerstats.msg.FormattingFunction;
import com.artemis.the.gr8.playerstats.msg.OutputManager;
import com.artemis.the.gr8.playerstats.multithreading.ThreadManager;
import com.artemis.the.gr8.playerstats.share.ShareManager;
import com.artemis.the.gr8.playerstats.utils.MyLogger;
import com.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * Turns user input into a {@link StatRequest} that can be
 * used to get statistic data.
 */
public final class RequestManager implements StatManager {

    private static RequestProcessor processor;
    private final OfflinePlayerHandler offlinePlayerHandler;

    public RequestManager(OutputManager outputManager, ShareManager shareManager) {
        processor = new RequestProcessor(outputManager, shareManager);
        offlinePlayerHandler = OfflinePlayerHandler.getInstance();
    }

    public static StatResult<?> execute(@NotNull StatRequest<?> request) {
        return switch (request.getSettings().getTarget()) {
            case PLAYER -> processor.processPlayerRequest(request.getSettings());
            case SERVER -> processor.processServerRequest(request.getSettings());
            case TOP -> processor.processTopRequest(request.getSettings());
        };
    }

    @Override
    public RequestGenerator<Integer> createPlayerStatRequest(String playerName) {
        return new PlayerStatRequest(playerName);
    }

    @Override
    public StatResult<Integer> executePlayerStatRequest(StatRequest<Integer> request) {
        return processor.processPlayerRequest(request.getSettings());
    }

    @Override
    public RequestGenerator<Long> createServerStatRequest() {
        return new ServerStatRequest();
    }

    @Override
    public StatResult<Long> executeServerStatRequest(StatRequest<Long> request) {
        return processor.processServerRequest(request.getSettings());
    }

    @Override
    public RequestGenerator<LinkedHashMap<String, Integer>> createTopStatRequest(int topListSize) {
        return new TopStatRequest(topListSize);
    }

    @Override
    public RequestGenerator<LinkedHashMap<String, Integer>> createTotalTopStatRequest() {
        int playerCount = offlinePlayerHandler.getOfflinePlayerCount();
        return createTopStatRequest(playerCount);
    }

    @Override
    public StatResult<LinkedHashMap<String, Integer>> executeTopRequest(StatRequest<LinkedHashMap<String, Integer>> request) {
        return processor.processTopRequest(request.getSettings());
    }

    private final class RequestProcessor {

        private static OutputManager outputManager;
        private static ShareManager shareManager;

        public RequestProcessor(OutputManager outputManager, ShareManager shareManager) {
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
}