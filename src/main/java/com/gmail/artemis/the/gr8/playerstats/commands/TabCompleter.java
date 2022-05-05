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

    public TabCompleter(Main p) {
        plugin = p;

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
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, String label, String[] args) {

        List<String> tabSuggestions = new ArrayList<>();

        //after typing "stat", suggest a list of viable statistics
        if (label.equalsIgnoreCase("statistic") || command.getAliases().contains(label)) {
            if (args.length == 1) {
                for (Statistic stat : Statistic.values()) {
                    if (stat.name().toLowerCase().startsWith(args[0])) {
                        tabSuggestions.add(stat.name().toLowerCase());
                    }
                }
            }

            //after checking if args[0] is a viable statistic, suggest substatistic OR commandOption
            if (args.length >= 2) {
                try {
                    Statistic stat = Statistic.valueOf(args[args.length-2].toUpperCase());
                    if (stat.getType() == Statistic.Type.UNTYPED) {
                        tabSuggestions = commandOptions;
                    }
                    else if (stat.getType() == Statistic.Type.BLOCK) {
                        tabSuggestions = Arrays.stream(Material.values()).filter(material ->
                                material.isBlock() && material.toString().startsWith(args[1])).map(Material::toString).collect(Collectors.toList());
                    }
                    else if (stat.getType() == Statistic.Type.ITEM) {
                        tabSuggestions = Arrays.stream(Material.values()).filter(material ->
                                material.isItem() && material.toString().startsWith(args[1])).map(Material::toString).collect(Collectors.toList());
                    }
                    else if (stat.getType() == Statistic.Type.ENTITY) {
                        tabSuggestions = Arrays.stream(EntityType.values()).map(EntityType::toString).filter(entityType ->
                                entityType.startsWith(args[1])).collect(Collectors.toList());
                    }


                    if (args.length >= 3) {
                        //if previous arg = "player", suggest playerNames
                        if (args[args.length-2].equalsIgnoreCase("player")) {
                            List<String> playerNames = OfflinePlayerHandler.getAllOfflinePlayerNames();
                            for (String name : playerNames) {
                                if (name.toLowerCase().startsWith(args[args.length-1].toLowerCase())) {
                                    tabSuggestions.add(name);
                                }
                            }
                        }

                        //after typing a valid substatistic entry, suggest commandOptions
                        else {
                            Material material = Material.matchMaterial(args[1]) == null ? null : Material.matchMaterial(args[1]);
                            if (material != null && (material.isBlock() || material.isItem())) {
                                return commandOptions;
                            }

                            EntityType entity = null;
                            try {
                                entity = EntityType.valueOf(args[1].toUpperCase());
                            }

                            catch (IllegalArgumentException | NullPointerException e) {
                                e.printStackTrace();
                            }

                            if (entity != null) {
                                return commandOptions;
                            }
                        }
                    }
                }

                catch (IllegalArgumentException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        return tabSuggestions;
    }
}
