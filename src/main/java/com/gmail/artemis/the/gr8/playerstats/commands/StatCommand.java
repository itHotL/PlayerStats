package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.StatManager;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OutputFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;


public class StatCommand implements CommandExecutor {

    private final OfflinePlayerHandler offlinePlayerHandler;
    private final OutputFormatter outputFormatter;
    private final StatManager statManager;
    private final Main plugin;

    public StatCommand(OutputFormatter o, StatManager s, Main p) {
        outputFormatter = o;
        statManager = s;
        plugin = p;

        offlinePlayerHandler = OfflinePlayerHandler.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        long time = System.currentTimeMillis();
        long startTime = System.currentTimeMillis();

        if (args.length >= 2) {

            String statName = null;
            String subStatEntry = null;
            String playerName = null;
            boolean playerFlag = false;
            boolean topFlag = false;

            time = plugin.logTimeTaken("StatCommand", time, 44);

            //all args are in lowercase
            for (String arg : args) {
                if (statManager.isStatistic(arg)) {
                    statName = (statName == null) ? arg : statName;
                    time = plugin.logTimeTaken("StatCommand", time, 50);
                }
                else if (statManager.isSubStatEntry(arg)) {
                    if (arg.equalsIgnoreCase("player")) {
                        if (!playerFlag) {
                            subStatEntry = (subStatEntry == null) ? arg : subStatEntry;
                            playerFlag = true;
                            time = plugin.logTimeTaken("StatCommand", time, 57);
                        }
                    }
                    else {
                        subStatEntry = (subStatEntry == null || playerFlag) ? arg : subStatEntry;
                        time = plugin.logTimeTaken("StatCommand", time, 62);
                    }
                }

                else if (arg.equalsIgnoreCase("top")) {
                    topFlag = true;
                }
                else if (arg.equalsIgnoreCase("me") && sender instanceof Player) {
                    playerName = sender.getName();
                    time = plugin.logTimeTaken("StatCommand", time, 71);
                }
                else if (offlinePlayerHandler.isOfflinePlayerName(arg)) {
                    playerName = (playerName == null) ? arg : playerName;
                    time = plugin.logTimeTaken("StatCommand", time, 75);
                }
            }
            if (statName != null) {
                time = plugin.logTimeTaken("StatCommand", time, 79);

                subStatEntry = statManager.isMatchingSubStatEntry(statName, subStatEntry) ? subStatEntry : null;
                time = plugin.logTimeTaken("StatCommand", time, 82);

                if (topFlag) {
                    LinkedHashMap<String, Integer> topStats = statManager.getTopStatistics(statName, subStatEntry);
                    return true;
                }

                else if (playerName != null) {
                    try {
                        time = plugin.logTimeTaken("StatCommand", time, 91);

                        int stat = statManager.getStatistic(statName, subStatEntry, playerName);
                        time = plugin.logTimeTaken("StatCommand", time, 94);

                        String msg = outputFormatter.formatPlayerStat(playerName, statName, subStatEntry, stat);
                        time = plugin.logTimeTaken("StatCommand", time, 97);

                        sender.sendMessage(msg);
                        time = plugin.logTimeTaken("StatCommand", time, 100);
                    }
                    catch (Exception e) {
                        sender.sendMessage(e.toString());
                    }
                }
            }
        }
        time = plugin.logTimeTaken("StatCommand", time, 108);
        plugin.getLogger().info("Total time elapsed: " + (System.currentTimeMillis() - startTime));
        return true;
    }

}
