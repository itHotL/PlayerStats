package com.artemis.the.gr8.playerstats.core.commands;

import com.artemis.the.gr8.playerstats.core.utils.EnumHandler;
import com.artemis.the.gr8.playerstats.core.utils.OfflinePlayerHandler;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class TabCompleter implements org.bukkit.command.TabCompleter {

    private final OfflinePlayerHandler offlinePlayerHandler;
    private final EnumHandler enumHandler;

    private List<String> statCommandTargets;
    private List<String> excludeCommandOptions;

    public TabCompleter() {
        offlinePlayerHandler = OfflinePlayerHandler.getInstance();
        enumHandler = EnumHandler.getInstance();
        prepareLists();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("statistic")) {
            return getStatCommandSuggestions(args);
        }
        else if (command.getName().equalsIgnoreCase("statisticexclude")) {
            return getExcludeCommandSuggestions(args);
        }
        return null;
    }

    private @Nullable List<String> getExcludeCommandSuggestions(@NotNull String[] args) {
        if (args.length == 0) {
            return null;
        }

        List<String> tabSuggestions = new ArrayList<>();
        if (args.length == 1) {
            tabSuggestions = excludeCommandOptions;
        }
        else if (args.length == 2) {
            tabSuggestions = switch (args[0]) {
                case "add" -> offlinePlayerHandler.getIncludedOfflinePlayerNames();
                case "remove" -> offlinePlayerHandler.getExcludedPlayerNames();
                default -> tabSuggestions;
            };
        }
        return getDynamicTabSuggestions(tabSuggestions, args[args.length-1]);
    }

    private @Nullable List<String> getStatCommandSuggestions(@NotNull String[] args) {
        if (args.length == 0) {
            return null;
        }

        List<String> tabSuggestions = new ArrayList<>();
        if (args.length == 1) {
            tabSuggestions = firstStatCommandArgSuggestions();
        }
        else {
            String previousArg = args[args.length-2];

            //after checking if args[0] is a viable statistic, suggest sub-stat or targets
            if (enumHandler.isStatistic(previousArg)) {
                Statistic stat = enumHandler.getStatEnum(previousArg);
                if (stat != null) {
                    tabSuggestions = suggestionsAfterFirstStatCommandArg(stat);
                }
            }
            else if (previousArg.equalsIgnoreCase("player")) {
                if (args.length >= 3 && enumHandler.isEntityStatistic(args[args.length-3])) {
                    tabSuggestions = statCommandTargets;  //if arg before "player" was entity-sub-stat, suggest targets
                }
                else {  //otherwise "player" is the target: suggest playerNames
                    tabSuggestions = offlinePlayerHandler.getIncludedOfflinePlayerNames();
                }
            }

            //after a substatistic, suggest targets
            else if (enumHandler.isSubStatEntry(previousArg)) {
                tabSuggestions = statCommandTargets;
            }
        }
        return getDynamicTabSuggestions(tabSuggestions, args[args.length-1]);
    }

    /**
     * These tabSuggestions take into account that the commandSender
     * will have been typing, so they are filtered for the letters
     * that have already been typed.
     */
    private List<String> getDynamicTabSuggestions(@NotNull List<String> completeList, String currentArg) {
        return completeList.stream()
                .filter(item -> item.toLowerCase(Locale.ENGLISH).contains(currentArg.toLowerCase(Locale.ENGLISH)))
                .collect(Collectors.toList());
    }

    private @NotNull List<String> firstStatCommandArgSuggestions() {
        List<String> suggestions = enumHandler.getAllStatNames();
        suggestions.add("examples");
        suggestions.add("info");
        suggestions.add("help");
        return suggestions;
    }

    private List<String> suggestionsAfterFirstStatCommandArg(@NotNull Statistic stat) {
        switch (stat.getType()) {
            case BLOCK -> {
                return enumHandler.getAllBlockNames();
            }
            case ITEM -> {
                if (stat == Statistic.BREAK_ITEM) {
                    return enumHandler.getAllItemsThatCanBreak();
                } else {
                    return enumHandler.getAllItemNames();
                }
            }
            case ENTITY -> {
                return enumHandler.getAllEntitiesThatCanDie();
            }
            default -> {
                return statCommandTargets;
            }
        }
    }

    private void prepareLists() {
        statCommandTargets = List.of("top", "player", "server", "me");
        excludeCommandOptions = List.of("add", "list", "remove", "info");
    }
}