package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    private final Main plugin;
    private final List<String> commandOptions;
    private final List<String> blockNames;
    private final List<String> entityNames;
    private final List<String> itemNames;
    private final List<String> statNames;
    private final List<String> subStatNames;
    private final List<String> playerNames;

    public TabCompleter(Main p) {
        plugin = p;

        commandOptions = new ArrayList<>();
        commandOptions.add("top");
        commandOptions.add("player");
        commandOptions.add("me");

        blockNames = Arrays.stream(Material.values()).filter(Material::isBlock).map(Material::toString).map(String::toLowerCase).toList();
        entityNames = Arrays.stream(EntityType.values()).map(EntityType::toString).map(String::toLowerCase).toList();
        itemNames = Arrays.stream(Material.values()).filter(Material::isItem).map(Material::toString).map(String::toLowerCase).toList();
        statNames = Arrays.stream(Statistic.values()).map(Statistic::toString).map(String::toLowerCase).toList();

        subStatNames = new ArrayList<>();
        subStatNames.addAll(blockNames);
        subStatNames.addAll(entityNames);
        subStatNames.addAll(itemNames);

        playerNames = OfflinePlayerHandler.getAllOfflinePlayerNames();

        int no = 1;
        for (String item : itemNames) {
            plugin.getLogger().info("Item " + no + ". " + item);
            no++;
        }

        no = 1;
        for (String block : blockNames) {
            plugin.getLogger().info("Block " + no + ". " + block);
            no++;
        }

        no = 1;
        for (String entity : entityNames) {
            plugin.getLogger().info("Entity: " + no + ". " + entity);
            no++;
        }
    }

    //args[0] = statistic                                                                        (length = 1)
    //args[1] = commandOption (top/player/me)   OR substatistic (block/item/entitytype)          (length = 2)
    //args[2] = playerName                      OR commandOption (top/player/me)                 (length = 3)
    //args[3] =                                    playerName                                    (length = 4)

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, String label, String[] args) {
        List<String> tabSuggestions = new ArrayList<>();

        //after typing "stat", suggest a list of viable statistics
        if ((label.equalsIgnoreCase("statistic") || command.getAliases().contains(label)) && args.length >= 1) {

            if (args.length == 1) {
                tabSuggestions = statNames.stream().filter(stat -> stat.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
            }

            //after checking if args[0] is a viable statistic, suggest substatistic OR commandOptions
            else {
                if (statNames.contains(args[args.length-2].toLowerCase())) {
                    Statistic stat = null;
                    try {
                        stat = Statistic.valueOf(args[args.length-2].toUpperCase());
                    }
                    catch (IllegalArgumentException | NullPointerException e) {
                        e.printStackTrace();
                    }

                    if (stat != null) {
                        if (stat.getType() == Statistic.Type.UNTYPED) {
                            tabSuggestions = commandOptions;
                        }
                        else if (stat.getType() == Statistic.Type.BLOCK) {
                            tabSuggestions = blockNames.stream().filter(block -> block.startsWith(args[args.length-1])).collect(Collectors.toList());
                        }
                        else if (stat.getType() == Statistic.Type.ITEM) {
                            tabSuggestions = itemNames.stream().filter(item -> item.startsWith(args[args.length-1])).collect(Collectors.toList());

                        }
                        else if (stat.getType() == Statistic.Type.ENTITY) {
                            tabSuggestions = entityNames.stream().filter(entity -> entity.startsWith(args[args.length-1])).collect(Collectors.toList());
                        }
                    }
                }

                //after a substatistic, suggest commandOptions
                else if (subStatNames.contains(args[args.length-2].toLowerCase())) {
                    tabSuggestions = commandOptions;
                }

                //if previous arg = "player", suggest playerNames
                else if (args[args.length-2].equalsIgnoreCase("player")) {
                    tabSuggestions = playerNames.stream().filter(player -> player.toLowerCase().startsWith(args[args.length-1].toLowerCase())).collect(Collectors.toList());
                }
            }
        }
        return tabSuggestions;
    }
}
