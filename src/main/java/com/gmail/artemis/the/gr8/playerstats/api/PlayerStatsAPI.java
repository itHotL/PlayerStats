package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatManager;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;


import static org.jetbrains.annotations.ApiStatus.Internal;

/** This class implements the API*/
public final class PlayerStatsAPI extends JavaPlugin implements PlayerStats {

    private final Main plugin;
    private static ThreadManager threadManager;
    private static StatFormatter statFormatter;
    private static StatManager statManager;

    @Internal
    private PlayerStatsAPI(Main plugin, ThreadManager thread, StatFormatter format, StatManager stat) {
        this.plugin = plugin;
        threadManager = thread;
        statFormatter = format;
        statManager = stat;
    }

    @Internal
    public static PlayerStatsAPI load(Main plugin, ThreadManager threadManager, StatFormatter formatter, StatManager statManager) {
        return new PlayerStatsAPI(plugin, threadManager, formatter, statManager);
    }

    @Override
    public TextComponent getFancyStat(Target selection, CommandSender sender, String[] args) throws IllegalArgumentException {
        StatRequest request = statManager.generateRequest(sender, args);
        if (statManager.requestIsValid(request)) {
            switch (selection) {
                case PLAYER -> {
                    int stat = statManager.getPlayerStat(request);
                    return statFormatter.formatPlayerStat(request, stat);
                }
                case SERVER -> {
                    //do something async
                }
                case TOP -> {
                    //also do something async
                }
            }
        } else {
            throw new IllegalArgumentException("This is not a valid stat-request!");
        }
        return null;
    }
}