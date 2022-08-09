package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.api.ApiOutputManager;
import com.gmail.artemis.the.gr8.playerstats.api.PlayerStats;
import com.gmail.artemis.the.gr8.playerstats.api.PlayerStatsAPI;
import com.gmail.artemis.the.gr8.playerstats.msg.InternalFormatter;
import com.gmail.artemis.the.gr8.playerstats.commands.ReloadCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.ShareCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.StatCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.TabCompleter;
import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.listeners.JoinListener;
import com.gmail.artemis.the.gr8.playerstats.msg.OutputManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatCalculator;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Main extends JavaPlugin {

    private static BukkitAudiences adventure;

    private static ConfigHandler config;
    private static OfflinePlayerHandler offlinePlayerHandler;
    private static EnumHandler enumHandler;

    private static OutputManager outputManager;
    private static ShareManager shareManager;
    private static StatCalculator statCalculator;
    private static ThreadManager threadManager;

    private static PlayerStats playerStatsAPI;


    @Override
    public void onEnable() {
        new Metrics(this, 15923);

        //initialize all the Managers, singletons, ConfigHandler and the API
        initializeMainClasses();

        //register all commands and the tabCompleter
        PluginCommand statcmd = this.getCommand("statistic");
        if (statcmd != null) {
            statcmd.setExecutor(new StatCommand(outputManager, threadManager));
            statcmd.setTabCompleter(new TabCompleter(enumHandler, offlinePlayerHandler));
        }
        PluginCommand reloadcmd = this.getCommand("statisticreload");
        if (reloadcmd != null) reloadcmd.setExecutor(new ReloadCommand(threadManager));
        PluginCommand sharecmd = this.getCommand("statisticshare");
        if (sharecmd != null) sharecmd.setExecutor(new ShareCommand(shareManager, outputManager));

        //register the listener
        Bukkit.getPluginManager().registerEvents(new JoinListener(threadManager), this);
        
        //finish up
        this.getLogger().info("Enabled PlayerStats!");
    }

    @Override
    public void onDisable() {
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
        this.getLogger().info("Disabled PlayerStats!");
    }

    public static @NotNull BukkitAudiences getAdventure() throws IllegalStateException {
        if (adventure == null) {
            throw new IllegalStateException("Tried to access Adventure without PlayerStats being enabled!");
        }
        return adventure;
    }

    public static @NotNull ConfigHandler getConfigHandler() throws IllegalStateException {
        if (config == null) {
            throw new IllegalStateException("PlayerStats does not seem to be loaded!");
        }
        return config;
    }

    public static @NotNull OfflinePlayerHandler getOfflinePlayerHandler() throws IllegalStateException {
        if (offlinePlayerHandler == null) {
            throw new IllegalStateException("PlayerStats does not seem to be fully loaded!");
        }
        return offlinePlayerHandler;
    }

    public static @NotNull EnumHandler getEnumHandler() {
        if (enumHandler == null) {
            enumHandler = new EnumHandler();
        }
        return enumHandler;
    }

    public static @NotNull StatCalculator getStatCalculator() throws IllegalStateException {
        if (statCalculator == null) {
            throw new IllegalStateException("PlayerStats does not seem to be loaded!");
        }
        return statCalculator;
    }

    public static @NotNull InternalFormatter getStatFormatter() throws IllegalStateException {
        if (outputManager == null) {
            throw new IllegalStateException("PlayerStats does not seem to be loaded!");
        }
        return outputManager;
    }

    public static @NotNull PlayerStats getPlayerStatsAPI() throws IllegalStateException {
        if (playerStatsAPI == null) {
            throw new IllegalStateException("PlayerStats does not seem to be loaded!");
        }
        return playerStatsAPI;
    }

    private void initializeMainClasses() {
        adventure = BukkitAudiences.create(this);

        config = new ConfigHandler(this);
        enumHandler = new EnumHandler();
        offlinePlayerHandler = new OfflinePlayerHandler();

        shareManager = new ShareManager(config);
        statCalculator = new StatCalculator(offlinePlayerHandler);
        outputManager = new OutputManager(adventure, config, shareManager);
        threadManager = new ThreadManager(config, statCalculator, outputManager);

        ApiOutputManager apiOutputManager = new ApiOutputManager(config);
        playerStatsAPI = new PlayerStatsAPI(apiOutputManager, offlinePlayerHandler);
    }
}