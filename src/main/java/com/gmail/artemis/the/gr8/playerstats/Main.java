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
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Main extends JavaPlugin {

    private static BukkitAudiences adventure;
    private static PlayerStats playerStatsAPI;

    public static @NotNull BukkitAudiences adventure() {
        if (adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }

    public static @NotNull PlayerStats getPlayerStatsAPI() {
        if (playerStatsAPI == null) {
            playerStatsAPI = new PlayerStatsAPI();
        }
        return playerStatsAPI;
    }

    @Override
    public void onEnable() {
        //initialize the Adventure library
        adventure = BukkitAudiences.create(this);

        //first get an instance of all the classes that need to be passed along to different classes
        ConfigHandler config = new ConfigHandler(this);
        OfflinePlayerHandler offlinePlayerHandler = new OfflinePlayerHandler();

        OutputManager outputManager = OutputManager.getInstance(config);
        ThreadManager threadManager = ThreadManager.getInstance(config, outputManager, offlinePlayerHandler);
        ShareManager shareManager = ShareManager.getInstance(config);

        //register all commands and the tabCompleter
        PluginCommand statcmd = this.getCommand("statistic");
        if (statcmd != null) {
            statcmd.setExecutor(new StatCommand(outputManager, threadManager, offlinePlayerHandler));
            statcmd.setTabCompleter(new TabCompleter(offlinePlayerHandler));
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
}