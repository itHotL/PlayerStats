package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    private final EnumHandler enumHandler;
    private final List<String> commandOptions;


    public TabCompleter(EnumHandler e) {
        enumHandler = e;

        commandOptions = new ArrayList<>();
        commandOptions.add("top");
        commandOptions.add("player");
        commandOptions.add("me");
    }

    //args[0] = statistic                                                                        (length = 1)
    //args[1] = commandOption (top/player/me)   OR substatistic (block/item/entitytype)          (length = 2)
    //args[2] = playerName                      OR commandOption (top/player/me)                 (length = 3)
    //args[3] =                                    playerName                                    (length = 4)

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> tabSuggestions = new ArrayList<>();

        //after typing "stat", suggest a list of viable statistics
        if (args.length >= 1) {
            if (args.length == 1) {
                tabSuggestions = enumHandler.getStatNames().stream().filter(stat ->
                        stat.contains(args[0].toLowerCase())).collect(Collectors.toList());
            }

            //after checking if args[0] is a viable statistic, suggest substatistic OR commandOptions
            else {
                if (enumHandler.isStatistic(args[args.length-2])) {
                        tabSuggestions = switch (enumHandler.getStatType(args[args.length-2])) {
                            case UNTYPED -> commandOptions;
                            case BLOCK -> enumHandler.getBlockNames().stream().filter(block ->
                                    block.contains(args[args.length - 1])).collect(Collectors.toList());
                            case ITEM -> enumHandler.getItemNames().stream().filter(item ->
                                    item.contains(args[args.length - 1])).collect(Collectors.toList());
                            case ENTITY -> enumHandler.getEntityTypeNames().stream().filter(entity ->
                                    entity.contains(args[args.length - 1])).collect(Collectors.toList());
                        };
                }

                //if previous arg = "player", suggest playerNames
                else if (args[args.length-2].equalsIgnoreCase("player")) {
                    if (args.length >= 3 && enumHandler.getEntityStatNames().contains(args[args.length-3].toLowerCase())) {
                        tabSuggestions = commandOptions;

                    }
                    else {
                        tabSuggestions = OfflinePlayerHandler.getAllOfflinePlayerNames().stream().filter(player ->
                                player.toLowerCase().contains(args[args.length-1].toLowerCase())).collect(Collectors.toList());
                    }
                }

                //after a substatistic, suggest commandOptions
                else if (enumHandler.isSubStatEntry(args[args.length-2])) {
                    tabSuggestions = commandOptions;
                }
            }
        }
        return tabSuggestions;
    }
}
