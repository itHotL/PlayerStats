package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.api.PlayerStats;
import com.gmail.artemis.the.gr8.playerstats.api.PlayerStatsAPI;
import com.gmail.artemis.the.gr8.playerstats.commands.ReloadCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.ShareCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.StatCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.TabCompleter;
import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.listeners.JoinListener;
import com.gmail.artemis.the.gr8.playerstats.msg.OutputManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.RequestManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.StatManager;
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
    private static PlayerStats playerStatsAPI;
    private static OutputManager outputManager;
    private static RequestManager requestManager;
    private static ShareManager shareManager;
    private static ThreadManager threadManager;


    @Override
    public void onEnable() {
        //TODO fix (move these two into initializeMainClasses also, and remove all the Main.get... methods)
        new Metrics(this, 15923);

        //first get an instance of all the classes that need to be passed along to different classes
        ConfigHandler config = new ConfigHandler(this);
        EnumHandler enumHandler = new EnumHandler();
        OfflinePlayerHandler offlinePlayerHandler = new OfflinePlayerHandler();

        //initialize all the Managers and the API
        initializeMainClasses(config, enumHandler, offlinePlayerHandler);

        //register all commands and the tabCompleter
        PluginCommand statcmd = this.getCommand("statistic");
        if (statcmd != null) {
            statcmd.setExecutor(new StatCommand(outputManager, threadManager, requestManager));
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

    public static @NotNull PlayerStats getPlayerStatsAPI() throws IllegalStateException {
        if (playerStatsAPI == null) {
            throw new IllegalStateException("PlayerStats does not seem to be loaded!");
        }
        return playerStatsAPI;
    }

    private void initializeMainClasses(ConfigHandler config, EnumHandler enumHandler, OfflinePlayerHandler offlinePlayerHandler) {
        adventure = BukkitAudiences.create(this);

        shareManager = new ShareManager(config);
        StatManager statManager = new StatManager(offlinePlayerHandler, config.getTopListMaxSize());
        outputManager = new OutputManager(getAdventure(), config, shareManager);
        requestManager = new RequestManager(enumHandler, offlinePlayerHandler, outputManager);
        threadManager = new ThreadManager(config, statManager, outputManager);

        playerStatsAPI = new PlayerStatsAPI(requestManager, statManager, outputManager);
    }
}