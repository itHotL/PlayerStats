package com.artemis.the.gr8.playerstats.statistic.request;

import com.artemis.the.gr8.playerstats.Main;
import com.artemis.the.gr8.playerstats.statistic.result.StatResult;
import com.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
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

public final class InternalStatRequest extends StatRequest<Object> {

    private final OfflinePlayerHandler offlinePlayerHandler;
    private final EnumHandler enumHandler;
    private final Pattern targetPattern;

    public InternalStatRequest(CommandSender sender, String[] args) {
        super(sender);
        offlinePlayerHandler = Main.getOfflinePlayerHandler();
        enumHandler = Main.getEnumHandler();
        targetPattern = Pattern.compile("top|server|me|player");

        String[] argsMinusTarget = extractAndStoreTarget(sender, args);
        String[] argsMinusStatistic = extractAndStoreStatistic(argsMinusTarget);
        findAndStoreSubStat(argsMinusStatistic);
    }

    @Override
    public StatResult<Object> execute() {
        return null;
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
                            super.settings.configureForPlayer(playerName);
                            String[] extractedPlayerName = removeArg(leftoverArgs, playerName);
                            return removeArg(extractedPlayerName, arg);
                        }
                    }
                    case "me" -> {
                        if (sender instanceof Player) {
                            super.settings.configureForPlayer(sender.getName());
                        } else {
                            super.settings.configureForServer();
                        }
                    }
                    case "server" -> super.settings.configureForServer();
                    case "top" -> super.settings.configureForTop();
                }
                return removeArg(leftoverArgs, arg);
            }
        }
        //if no target is found, but there is a playerName, assume target = Target.PLAYER
        if (playerName != null) {
            super.settings.configureForPlayer(playerName);
            return removeArg(leftoverArgs, playerName);
        }
        //otherwise, assume target = Target.TOP
        super.settings.configureForTop();
        return leftoverArgs;
    }

    private String[] extractAndStoreStatistic(@NotNull String[] leftoverArgs) {
        for (String arg : leftoverArgs) {
            if (enumHandler.isStatistic(arg)) {
                super.settings.setStatistic(EnumHandler.getStatEnum(arg));
                return removeArg(leftoverArgs, arg);
            }
        }
        return leftoverArgs;
    }

    private void findAndStoreSubStat(@NotNull String[] leftoverArgs) {
        Statistic statistic = super.settings.getStatistic();
        if (statistic == null || leftoverArgs.length == 0) {
            return;
        }

        for (String arg : leftoverArgs) {
            if (enumHandler.isSubStatEntry(arg)) {
                switch (statistic.getType()) {
                    case UNTYPED -> super.configureUntyped(statistic);
                    case ITEM -> {
                        Material item = EnumHandler.getItemEnum(arg);
                        if (item != null) {
                            super.configureBlockOrItemType(statistic, item);
                        }
                    }
                    case BLOCK -> {
                        Material block = EnumHandler.getBlockEnum(arg);
                        if (block != null) {
                            super.configureBlockOrItemType(statistic, block);
                        }
                    }
                    case ENTITY -> {
                        EntityType entityType = EnumHandler.getEntityEnum(arg);
                        if (entityType != null) {
                            super.configureEntityType(statistic, entityType);
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
