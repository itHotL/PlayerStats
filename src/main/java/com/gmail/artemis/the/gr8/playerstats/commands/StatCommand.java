package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.StatThread;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OutputFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class StatCommand implements CommandExecutor {

    private final ConfigHandler config;
    private final OfflinePlayerHandler offlinePlayerHandler;
    private final OutputFormatter outputFormatter;
    private final Main plugin;

    public StatCommand(ConfigHandler c, OfflinePlayerHandler of, OutputFormatter o, Main p) {
        config = c;
        offlinePlayerHandler = of;
        outputFormatter = o;
        plugin = p;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        //part 1: collecting all relevant information from the args
        if (args.length >= 2) {
            StatRequest request = new StatRequest(sender);

            for (String arg : args) {
                if (EnumHandler.isStatistic(arg) && request.getStatName() == null) {
                    request.setStatName(arg);
                }
                else if (EnumHandler.isSubStatEntry(arg)) {
                    if (arg.equalsIgnoreCase("player")) {
                        if (request.playerFlag()) {
                            if (request.getSubStatEntry() == null) request.setSubStatEntry(arg);
                        }
                        else {
                            request.setPlayerFlag(true);
                        }
                    }

                    else {
                        if (request.getSubStatEntry() == null) request.setSubStatEntry(arg);
                    }
                }

                else if (arg.equalsIgnoreCase("top")) {
                    request.setTopFlag(true);
                }
                else if (arg.equalsIgnoreCase("me") && sender instanceof Player) {
                    request.setPlayerName(sender.getName());
                }
                else if (offlinePlayerHandler.isOfflinePlayerName(arg) && request.getPlayerName() == null) {
                    request.setPlayerName(arg);
                }
            }

            //part 2: sending the information to the StatThread
            if (isValidStatRequest(request)) {
                StatThread statThread = new StatThread(request, config, offlinePlayerHandler, outputFormatter, plugin);
                statThread.start();
                return true;
            }
        }
        return false;
    }

    //check whether all necessary ingredients are present to proceed with a lookup
    private boolean isValidStatRequest(StatRequest request) {
        if (request.getStatName() != null) {
            if (request.topFlag() || request.getPlayerName() != null) {
                validatePlayerFlag(request);
                return EnumHandler.isValidStatEntry(request.getStatName(), request.getSubStatEntry());
            }
        }
        return false;
    }

    //account for the fact that "player" could be either a subStatEntry or a flag to indicate the target for the lookup, and correct the request if necessary
    private void validatePlayerFlag(StatRequest request) {
        if (!EnumHandler.isValidStatEntry(request.getStatName(), request.getSubStatEntry()) && request.playerFlag()) {
            request.setSubStatEntry("player");
        }
    }
}
