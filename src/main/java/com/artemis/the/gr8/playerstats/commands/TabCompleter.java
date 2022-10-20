package com.artemis.the.gr8.playerstats.commands;

import com.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class TabCompleter implements org.bukkit.command.TabCompleter {

    private final EnumHandler enumHandler;
    private final OfflinePlayerHandler offlinePlayerHandler;

    private List<String> targetSuggestions;
    private List<String> itemBrokenSuggestions;
    private List<String> entitySuggestions;

    public TabCompleter(EnumHandler enumHandler, OfflinePlayerHandler offlinePlayerHandler) {
        this.enumHandler = enumHandler;
        this.offlinePlayerHandler = offlinePlayerHandler;
        prepareLists();
    }

    //args[0] = statistic                                                        (length = 1)
    //args[1] = target (player/server/top)    OR sub-stat (block/item/entity)    (length = 2)
    //args[2] = playerName                    OR target (player/server/top)      (length = 3)
    //args[3] =                                  playerName                      (length = 4)
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
        if (args.length == 1) {
            return getDynamicTabSuggestions(offlinePlayerHandler.getOfflinePlayerNames(), args[0]);
        }
        return null;
    }

    private @Nullable List<String> getStatCommandSuggestions(@NotNull String[] args) {
        if (args.length == 1) {
            return getFirstArgSuggestions(args[0]);
        }
        else if (args.length > 1) {
            String currentArg = args[args.length-1];
            String previousArg = args[args.length-2];

            //after checking if args[0] is a viable statistic, suggest sub-stat or targets
            if (enumHandler.isStatistic(previousArg)) {
                Statistic stat = EnumHandler.getStatEnum(previousArg);
                if (stat != null) {
                    return getDynamicTabSuggestions(getSuggestionsAfterStat(stat), currentArg);
                }
            }
            else if (previousArg.equalsIgnoreCase("player")) {
                if (args.length >= 3 && enumHandler.isEntityStatistic(args[args.length-3])) {
                    return targetSuggestions;  //if arg before "player" was entity-sub-stat, suggest targets
                }
                else {  //otherwise "player" is the target: suggest playerNames
                    return getDynamicTabSuggestions(offlinePlayerHandler.getOfflinePlayerNames(), currentArg);
                }
            }

            //after a substatistic, suggest targets
            else if (enumHandler.isSubStatEntry(previousArg)) {
                return targetSuggestions;
            }

        }
        return null;
    }

    private List<String> getFirstArgSuggestions(String currentArg) {
        List<String> suggestions = enumHandler.getStatNames();
        suggestions.add("examples");
        suggestions.add("help");
        return getDynamicTabSuggestions(suggestions, currentArg);
    }

    /**
     * These tabSuggestions take into account that the commandSender
     * will have been typing, so they are filtered for the letters
     * that have already been typed.
     */
    private List<String> getDynamicTabSuggestions(@NotNull List<String> completeList, String currentArg) {
        return completeList.stream()
                .filter(item -> item.toLowerCase().contains(currentArg.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<String> getSuggestionsAfterStat(@NotNull Statistic stat) {
        switch (stat.getType()) {
            case BLOCK -> {
                return getAllBlockNames();
            }
            case ITEM -> {
                if (stat == Statistic.BREAK_ITEM) {
                    return getItemBrokenSuggestions();
                } else {
                    return getAllItemNames();
                }
            }
            case ENTITY -> {
                return getEntitySuggestions();
            }
            default -> {
                return targetSuggestions;
            }
        }
    }

    private List<String> getAllItemNames() {
        return enumHandler.getItemNames();
    }

    private List<String> getItemBrokenSuggestions() {
        return itemBrokenSuggestions;
    }

    private List<String> getAllBlockNames() {
        return enumHandler.getBlockNames();
    }

    private List<String> getEntitySuggestions() {
        return entitySuggestions;
    }

    private void prepareLists() {
        targetSuggestions = new ArrayList<>();
        targetSuggestions.add("top");
        targetSuggestions.add("player");
        targetSuggestions.add("server");
        targetSuggestions.add("me");

        //breaking an item means running its durability negative
        itemBrokenSuggestions = Arrays.stream(Material.values())
                .parallel()
                .filter(Material::isItem)
                .filter(item -> item.getMaxDurability() != 0)
                .map(Material::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        //the only statistics dealing with entities are killed_entity and entity_killed_by
        entitySuggestions = Arrays.stream(EntityType.values())
                .parallel()
                .filter(EntityType::isAlive)
                .map(EntityType::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }
}