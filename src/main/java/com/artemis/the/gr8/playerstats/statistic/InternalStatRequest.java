package com.artemis.the.gr8.playerstats.statistic;

import com.artemis.the.gr8.playerstats.Main;
import com.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class InternalStatRequest extends StatRequest<TextComponent> {

    private final ConfigHandler config;
    private final OfflinePlayerHandler offlinePlayerHandler;
    private final EnumHandler enumHandler;
    private final Pattern targetPattern;

    public InternalStatRequest(CommandSender sender, String[] args) {
        super(sender);
        config = Main.getConfigHandler();
        offlinePlayerHandler = Main.getOfflinePlayerHandler();
        enumHandler = Main.getEnumHandler();
        targetPattern = Pattern.compile("top|server|me|player");

        processArgs(sender, args);
    }

    @Override
    public @NotNull StatResult<TextComponent> execute() {
        return Main.getRequestProcessor().getInternalResult(super.getSettings());
    }

    private void processArgs(CommandSender sender, String[] args) {
        String[] argsMinusTarget = extractAndStoreTarget(sender, args);
        findStatAndSubStat(argsMinusTarget);
    }

    private String[] extractAndStoreTarget(CommandSender sender, @NotNull String[] leftoverArgs) {
        String playerName = tryToFindPlayerName(leftoverArgs);

        for (String arg : leftoverArgs) {
            Matcher targetMatcher = targetPattern.matcher(arg);
            if (targetMatcher.find()) {
                switch (targetMatcher.group()) {
                    case "player" -> {
                        if (playerName == null) {
                            continue;
                        }
                        else {
                            super.getSettings().configureForPlayer(playerName);
                            String[] extractedPlayerName = removeArg(leftoverArgs, playerName);
                            return removeArg(extractedPlayerName, arg);
                        }
                    }
                    case "me" -> {
                        if (sender instanceof Player) {
                            super.getSettings().configureForPlayer(sender.getName());
                        } else {
                            super.getSettings().configureForServer();
                        }
                    }
                    case "server" -> super.getSettings().configureForServer();
                    case "top" -> super.getSettings().configureForTop(config.getTopListMaxSize());
                }
                return removeArg(leftoverArgs, arg);
            }
        }
        //if no target is found, but there is a playerName, assume target = Target.PLAYER
        if (playerName != null) {
            super.getSettings().configureForPlayer(playerName);
            return removeArg(leftoverArgs, playerName);
        }
        //otherwise, assume target = Target.TOP
        super.getSettings().configureForTop(config.getTopListMaxSize());
        return leftoverArgs;
    }

    private void findStatAndSubStat(@NotNull String[] leftoverArgs) {
        for (String arg : leftoverArgs) {
            if (enumHandler.isStatistic(arg)) {
                Statistic stat = EnumHandler.getStatEnum(arg);
                String[] argsWithoutStat = removeArg(leftoverArgs, arg);
                findAndStoreSubStat(argsWithoutStat, stat);
            }
        }
    }

    private void findAndStoreSubStat(String[] leftoverArgs, Statistic statistic) {
        if (statistic == null || leftoverArgs.length == 0) {
            return;
        }

        for (String arg : leftoverArgs) {
            if (enumHandler.isSubStatEntry(arg)) {
                switch (statistic.getType()) {
                    case UNTYPED -> super.getSettings().configureUntyped(statistic);
                    case ITEM -> {
                        Material item = EnumHandler.getItemEnum(arg);
                        if (item != null) {
                            super.getSettings().configureBlockOrItemType(statistic, item);
                        }
                    }
                    case BLOCK -> {
                        Material block = EnumHandler.getBlockEnum(arg);
                        if (block != null) {
                            super.getSettings().configureBlockOrItemType(statistic, block);
                        }
                    }
                    case ENTITY -> {
                        EntityType entityType = EnumHandler.getEntityEnum(arg);
                        if (entityType != null) {
                            super.getSettings().configureEntityType(statistic, entityType);
                        }
                    }
                }
                break;
            }
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

    private String[] removeArg(@NotNull String[] args, String argToRemove) {
        ArrayList<String> currentArgs = new ArrayList<>(Arrays.asList(args));
        currentArgs.remove(argToRemove);
        return currentArgs.toArray(String[]::new);
    }
}