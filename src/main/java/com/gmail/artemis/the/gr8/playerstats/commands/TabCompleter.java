package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.Main;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    private final Main plugin;
    public TabCompleter(Main p) {
        plugin = p;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, String label, String[] args) {

        List<String> allStats = new ArrayList<>();
        int i = 0;
        int block = 0;
        int entity = 0;
        int item = 0;
        int untyped = 0;

        if (label.equalsIgnoreCase("statistic")) {
            for (Statistic stat : Statistic.values()) {
                i++;
                if (stat.getType().equals(Statistic.Type.BLOCK)) block++;
                if (stat.getType().equals(Statistic.Type.ENTITY)) entity++;
                if (stat.getType().equals(Statistic.Type.ITEM)) item++;
                if (stat.getType().equals(Statistic.Type.UNTYPED)) untyped++;

                if (args.length == 1 && stat.toString().toLowerCase().startsWith(args[0])) {
                    allStats.add(stat.toString().toLowerCase());
                }
            }
        }
        plugin.getLogger().info("Total number of statistics: " + i);
        plugin.getLogger().info("Block type: " + block);
        plugin.getLogger().info("Entity type: " + entity);
        plugin.getLogger().info("Item type: " + item);
        plugin.getLogger().info("Untyped: " + untyped);
        return allStats;
    }
}
