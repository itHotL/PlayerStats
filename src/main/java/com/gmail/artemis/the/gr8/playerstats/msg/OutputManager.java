package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.ShareManager;
import com.gmail.artemis.the.gr8.playerstats.api.StatFormatter;
import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.enums.StandardMessage;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.msg.components.BukkitConsoleComponentFactory;
import com.gmail.artemis.the.gr8.playerstats.msg.components.PrideComponentFactory;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.Month;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.gmail.artemis.the.gr8.playerstats.enums.StandardMessage.*;

/** This class manages all PlayerStats output. It is the only place where messages are sent.
 It gets the messages from a {@link MessageBuilder}, which is different for a Console as for Players
 (mainly to deal with the lack of hover-text, and for Bukkit consoles to make up for the lack of hex-colors).*/
public final class OutputManager implements StatFormatter {

    private static BukkitAudiences adventure;
    private static ShareManager shareManager;
    private static MessageBuilder writer;
    private static MessageBuilder consoleWriter;

    private static EnumMap<StandardMessage, Function<MessageBuilder, TextComponent>> standardMessages;

    public OutputManager(BukkitAudiences adventure, ConfigHandler config, ShareManager shareManager) {
        OutputManager.adventure = adventure;
        OutputManager.shareManager = shareManager;

        getMessageWriters(config);
        prepareFunctions();
    }

    public void updateMessageWriters(ConfigHandler config) {
        getMessageWriters(config);
    }

    @Override
    public TextComponent formatPlayerStat(@NotNull StatRequest request, int playerStat) {
        BiFunction<UUID, CommandSender, TextComponent> playerStatFunction =
                getWriter(request).formattedPlayerStatFunction(playerStat, request);

        return processFunction(request.getCommandSender(), playerStatFunction);
    }

    @Override
    public TextComponent formatServerStat(@NotNull StatRequest request, long serverStat) {
        BiFunction<UUID, CommandSender, TextComponent> serverStatFunction =
                getWriter(request).formattedServerStatFunction(serverStat, request);

        return processFunction(request.getCommandSender(), serverStatFunction);
    }

    @Override
    public TextComponent formatTopStat(@NotNull StatRequest request, @NotNull LinkedHashMap<String, Integer> topStats) {
        BiFunction<UUID, CommandSender, TextComponent> topStatFunction =
                getWriter(request).formattedTopStatFunction(topStats, request);

        return processFunction(request.getCommandSender(), topStatFunction);
    }

    public void sendFeedbackMsg(@NotNull CommandSender sender, StandardMessage message) {
        if (message != null) {
            adventure.sender(sender).sendMessage(standardMessages.get(message)
                    .apply(getWriter(sender)));
        }
    }

    public void sendFeedbackMsgWaitAMoment(@NotNull CommandSender sender, boolean longWait) {
        adventure.sender(sender).sendMessage(getWriter(sender)
                .waitAMoment(longWait));
    }

    public void sendFeedbackMsgMissingSubStat(@NotNull CommandSender sender, Statistic.Type statType) {
        adventure.sender(sender).sendMessage(getWriter(sender)
                .missingSubStatName(statType));
    }

    public void sendFeedbackMsgWrongSubStat(@NotNull CommandSender sender, Statistic.Type statType, @Nullable String subStatName) {
        if (subStatName == null) {
            sendFeedbackMsgMissingSubStat(sender, statType);
        } else {
            adventure.sender(sender).sendMessage(getWriter(sender)
                    .wrongSubStatType(statType, subStatName));
        }
    }

    public void sendExamples(@NotNull CommandSender sender) {
        adventure.sender(sender).sendMessage(getWriter(sender)
                .usageExamples());
    }

    public void sendHelp(@NotNull CommandSender sender) {
        adventure.sender(sender).sendMessage(getWriter(sender)
                .helpMsg(sender instanceof ConsoleCommandSender));
    }

    public void sendToAllPlayers(@NotNull TextComponent component) {
        adventure.players().sendMessage(component);
    }

    public void sendToCommandSender(@NotNull CommandSender sender, @NotNull TextComponent component) {
        adventure.sender(sender).sendMessage(component);
    }

    private TextComponent processFunction(CommandSender sender, @NotNull BiFunction<UUID, CommandSender, TextComponent> statResultFunction) {
        boolean saveOutput = !(sender instanceof ConsoleCommandSender) &&
                ShareManager.isEnabled() &&
                shareManager.senderHasPermission(sender);

        if (saveOutput) {
            UUID shareCode =
                    shareManager.saveStatResult(sender.getName(), statResultFunction.apply(null, sender));
            return statResultFunction.apply(shareCode, null);
        }
        else {
            return statResultFunction.apply(null, null);
        }
    }

    private MessageBuilder getWriter(CommandSender sender) {
        return sender instanceof ConsoleCommandSender ? consoleWriter : writer;
    }

    private MessageBuilder getWriter(StatRequest request) {
        if (request.isAPIRequest() || !request.isConsoleSender()) {
            return writer;
        } else {
            return consoleWriter;
        }
    }

    private void getMessageWriters(ConfigHandler config) {
        boolean isBukkit = Bukkit.getName().equalsIgnoreCase("CraftBukkit");
        if (config.useRainbowMode() ||
                (config.useFestiveFormatting() && LocalDate.now().getMonth().equals(Month.JUNE))) {
            writer = MessageBuilder.fromComponentFactory(config, new PrideComponentFactory(config));
        }
        else {
            writer = MessageBuilder.defaultBuilder(config);
        }

        if (!isBukkit) {
            consoleWriter = writer;
        } else {
            consoleWriter = MessageBuilder.fromComponentFactory(config, new BukkitConsoleComponentFactory(config));
        }
    }

    private void prepareFunctions() {
        standardMessages = new EnumMap<>(StandardMessage.class);

        standardMessages.put(RELOADED_CONFIG, (MessageBuilder::reloadedConfig));
        standardMessages.put(STILL_RELOADING, (MessageBuilder::stillReloading));
        standardMessages.put(MISSING_STAT_NAME, (MessageBuilder::missingStatName));
        standardMessages.put(MISSING_PLAYER_NAME, (MessageBuilder::missingPlayerName));
        standardMessages.put(REQUEST_ALREADY_RUNNING, (MessageBuilder::requestAlreadyRunning));
        standardMessages.put(STILL_ON_SHARE_COOLDOWN, (MessageBuilder::stillOnShareCoolDown));
        standardMessages.put(RESULTS_ALREADY_SHARED, (MessageBuilder::resultsAlreadyShared));
        standardMessages.put(STAT_RESULTS_TOO_OLD, (MessageBuilder::statResultsTooOld));
        standardMessages.put(UNKNOWN_ERROR, (MessageBuilder::unknownError));
    }
}