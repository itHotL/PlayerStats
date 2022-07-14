package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.ShareManager;
import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.enums.PluginMessage;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.models.datamodel.BiFunctionType;
import com.gmail.artemis.the.gr8.playerstats.models.datamodel.FunctionType;
import com.gmail.artemis.the.gr8.playerstats.models.datamodel.Type;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.function.BiFunction;

import static com.gmail.artemis.the.gr8.playerstats.enums.PluginMessage.*;

public class MessageSender {

    private static BukkitAudiences adventure;
    private static ShareManager shareManager;
    private static MessageWriter msg;
    private static ConsoleMessageWriter consoleMsg;

    private static EnumMap<PluginMessage, Type> pluginMessages;

    public MessageSender(ConfigHandler conf) {
        adventure = Main.adventure();
        shareManager = ShareManager.getInstance(conf);

        msg = new MessageWriter(conf);
        consoleMsg = new ConsoleMessageWriter(conf);

        pluginMessages = new EnumMap<>(PluginMessage.class);
        prepareFunctions();
    }

    public void updateComponentFactories(ConfigHandler config) {
        msg = new MessageWriter(config);
        consoleMsg = new ConsoleMessageWriter(config);
    }

    /**
     Takes a function out of a private EnumMap with functions, and executes it.
     The functions all call a method in MessageWriter, either directly or through
     a private method in this class.
     */
    public void send(CommandSender sender, PluginMessage message) {
        send(sender, message, null, null, null, 0, 0, null);
    }

    public void send(CommandSender sender, PluginMessage message, boolean longWait) {
        send(sender, message, longWait, null, null, 0, 0, null);
    }

    public void send(CommandSender sender, PluginMessage message, Statistic.Type statType) {
        send(sender, message, null, statType, null, 0, 0, null);
    }

    public void send(StatRequest request, int playerStat, boolean saveResult) {
        if (!saveResult) {
            send(request.getCommandSender(), FORMATTED_PLAYER_STAT, null, null, null, playerStat, 0, null);
        } else {  //we are never adding a share-button to console-sender-messages, so I can use msg

        }
    }

    public void send(StatRequest request, long serverStat, boolean saveResult) {
        if (!saveResult) {
            send(request.getCommandSender(), FORMATTED_SERVER_STAT, null, null, null, 0, serverStat, null);
        } else {  //we are never adding a share-button to console-sender-messages, so I can use msg

        }
    }

    public void send(StatRequest request, LinkedHashMap<String, Integer> topStats, boolean saveResult) {
        if (!saveResult) {
            send(request.getCommandSender(), FORMATTED_TOP_STAT, null, null, request, 0, 0, topStats);
        } else {  //we are never adding a share-button to console-sender-messages, so I can use msg

        }
    }

    private void send(@NotNull CommandSender sender, @NotNull PluginMessage message, Boolean longWait, Statistic.Type statType,
                      StatRequest request, int playerStat, long serverStat, LinkedHashMap<String, Integer> topStats) {

        Type customFunction = pluginMessages.get(message);
        TextComponent result = null;
        MessageWriter writer = messageWriter(sender instanceof ConsoleCommandSender);

        if (customFunction instanceof FunctionType<?,?> function) {
            result = function.apply(writer);

        } else if (customFunction instanceof BiFunctionType.MsgBoolean<?,?,?> biFunction) {
            if (longWait != null) result = biFunction.apply(writer, longWait);

        } else if (customFunction instanceof BiFunctionType.MsgStatType<?,?,?> biFunction) {
            if (statType != null) result = biFunction.apply(writer, statType);

        } else if (request != null) {
            if (customFunction instanceof BiFunctionType.StatRequestInt<?,?,?> biFunction) {
                result = biFunction.apply(request, playerStat);
            } else if (customFunction instanceof BiFunctionType.StatRequestLong<?,?,?> biFunction) {
                result = biFunction.apply(request, serverStat);
            } else if (customFunction instanceof BiFunctionType.StatRequestMap<?,?,?> biFunction) {
                if (topStats != null) result = biFunction.apply(request, topStats);
            }
        }

        if (result != null) {
            adventure.sender(sender).sendMessage(result);
        }
    }

    private void prepareFunctions() {
        pluginMessages.put(RELOADED_CONFIG, new FunctionType<>(MessageWriter::reloadedConfig));
        pluginMessages.put(STILL_RELOADING, new FunctionType<>(MessageWriter::stillReloading));
        pluginMessages.put(WAIT_A_MOMENT, new BiFunctionType.MsgBoolean<>(MessageWriter::waitAMoment));
        pluginMessages.put(MISSING_STAT_NAME, new FunctionType<>(MessageWriter::missingStatName));
        pluginMessages.put(MISSING_SUB_STAT_NAME, new BiFunctionType.MsgStatType<>(MessageWriter::missingSubStatName));
        pluginMessages.put(MISSING_PLAYER_NAME, new FunctionType<>(MessageWriter::missingPlayerName));
        pluginMessages.put(WRONG_SUB_STAT_TYPE, new BiFunctionType.MsgStatType<>(MessageWriter::wrongSubStatType));
        pluginMessages.put(REQUEST_ALREADY_RUNNING, new FunctionType<>(MessageWriter::requestAlreadyRunning));
        pluginMessages.put(STILL_ON_SHARE_COOLDOWN, new FunctionType<>(MessageWriter::stillOnShareCoolDown));
        pluginMessages.put(RESULTS_ALREADY_SHARED, new FunctionType<>(MessageWriter::resultsAlreadyShared));
        pluginMessages.put(STAT_RESULTS_TOO_OLD, new FunctionType<>(MessageWriter::statResultsTooOld));
        pluginMessages.put(UNKNOWN_ERROR, new FunctionType<>(MessageWriter::unknownError));
        pluginMessages.put(USAGE_EXAMPLES, new FunctionType<>(MessageWriter::usageExamples));
        pluginMessages.put(HELP_MSG, new FunctionType<>(MessageWriter::helpMsg));
        pluginMessages.put(FORMATTED_PLAYER_STAT, new BiFunctionType.StatRequestInt<>((StatRequest s, Integer i) -> messageWriter(s.isConsoleSender()).formattedPlayerStat(i, s)));
        pluginMessages.put(FORMATTED_SERVER_STAT, new BiFunctionType.StatRequestLong<>((StatRequest s, Long l) -> messageWriter(s.isConsoleSender()).formattedServerStat(l, s)));
        pluginMessages.put(FORMATTED_TOP_STAT, new BiFunctionType.StatRequestMap<>((StatRequest s, LinkedHashMap<String, Integer> l) -> messageWriter(s.isConsoleSender()).formattedTopStats(l, s)));
    }

    private BiFunction<TextComponent, UUID, TextComponent> addShareButton; {

    }

    private MessageWriter messageWriter(boolean isConsoleSender) {
        return isConsoleSender ? consoleMsg : msg;
    }
}