package com.artemis.the.gr8.playerstats.core;

import com.artemis.the.gr8.playerstats.api.PlayerStats;
import com.artemis.the.gr8.playerstats.api.StatNumberFormatter;
import com.artemis.the.gr8.playerstats.api.StatTextFormatter;
import com.artemis.the.gr8.playerstats.api.StatManager;
import com.artemis.the.gr8.playerstats.core.commands.*;
import com.artemis.the.gr8.playerstats.core.msg.msgutils.NumberFormatter;
import com.artemis.the.gr8.playerstats.core.multithreading.ThreadManager;
import com.artemis.the.gr8.playerstats.core.msg.OutputManager;
import com.artemis.the.gr8.playerstats.core.config.ConfigHandler;
import com.artemis.the.gr8.playerstats.core.listeners.JoinListener;
import com.artemis.the.gr8.playerstats.core.msg.msgutils.LanguageKeyHandler;
import com.artemis.the.gr8.playerstats.core.sharing.ShareManager;
import com.artemis.the.gr8.playerstats.core.statistic.StatRequestManager;
import com.artemis.the.gr8.playerstats.core.utils.Closable;
import com.artemis.the.gr8.playerstats.core.utils.OfflinePlayerHandler;
import com.artemis.the.gr8.playerstats.core.utils.Reloadable;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * PlayerStats' Main class
 */
public final class Main extends JavaPlugin implements PlayerStats {

    private static JavaPlugin pluginInstance;
    private static PlayerStats playerStatsAPI;
    private static ConfigHandler config;

    private static ThreadManager threadManager;
    private static StatRequestManager statManager;

    private static List<Reloadable> reloadables;
    private static List<Closable> closables;

    @Override
    public void onEnable() {
        reloadables = new ArrayList<>();
        closables = new ArrayList<>();

        initializeMainClassesInOrder();
        registerCommands();
        setupMetrics();

        //register the listener
        Bukkit.getPluginManager().registerEvents(new JoinListener(threadManager), this);
        
        //finish up
        this.getLogger().info("Enabled PlayerStats!");
    }

    @Override
    public void onDisable() {
        closables.forEach(Closable::close);
        this.getLogger().info("Disabled PlayerStats!");
    }

    public void reloadPlugin() {
        //config is not registered as reloadable to ensure it can be reloaded before everything else
        config.reload();
        reloadables.forEach(Reloadable::reload);
    }

    public static void registerReloadable(Reloadable reloadable) {
        reloadables.add(reloadable);
    }

    public static void registerClosable(Closable closable) {
        closables.add(closable);
    }

    /**
     *
     * @return the JavaPlugin instance associated with PlayerStats
     * @throws IllegalStateException if PlayerStats is not enabled
     */
    public static @NotNull JavaPlugin getPluginInstance() throws IllegalStateException {
        if (pluginInstance == null) {
            throw new IllegalStateException("PlayerStats is not loaded!");
        }
        return pluginInstance;
    }

    public static @NotNull PlayerStats getPlayerStatsAPI() throws IllegalStateException {
        if (playerStatsAPI == null) {
            throw new IllegalStateException("PlayerStats does not seem to be loaded!");
        }
        return playerStatsAPI;
    }

    /**
     * Initialize all classes that need initializing,
     * and store references to classes that are
     * needed for the Command classes or the API.
     */
    private void initializeMainClassesInOrder() {
        pluginInstance = this;
        playerStatsAPI = this;
        config = ConfigHandler.getInstance();

        LanguageKeyHandler.getInstance();
        OfflinePlayerHandler.getInstance();
        OutputManager.getInstance();
        ShareManager.getInstance();

        statManager = new StatRequestManager();
        threadManager = new ThreadManager(this);
    }

    /**
     * Register all commands and assign the tabCompleter
     * to the relevant commands.
     */
    private void registerCommands() {
        TabCompleter tabCompleter = new TabCompleter();

        PluginCommand statcmd = this.getCommand("statistic");
        if (statcmd != null) {
            statcmd.setExecutor(new StatCommand(threadManager));
            statcmd.setTabCompleter(tabCompleter);
        }
        PluginCommand excludecmd = this.getCommand("statisticexclude");
        if (excludecmd != null) {
            excludecmd.setExecutor(new ExcludeCommand());
            excludecmd.setTabCompleter(tabCompleter);
        }

        PluginCommand reloadcmd = this.getCommand("statisticreload");
        if (reloadcmd != null) {
            reloadcmd.setExecutor(new ReloadCommand(threadManager));
        }
        PluginCommand sharecmd = this.getCommand("statisticshare");
        if (sharecmd != null) {
            sharecmd.setExecutor(new ShareCommand());
        }
    }

    /**
     * Setup bstats
     */
    private void setupMetrics() {
        new BukkitRunnable() {
            @Override
            public void run() {
                final Metrics metrics = new Metrics(pluginInstance, 15923);
                final boolean placeholderExpansionActive;
                if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                    PlaceholderExpansion expansion = PlaceholderAPIPlugin
                            .getInstance()
                            .getLocalExpansionManager()
                            .getExpansion("playerstats");
                    placeholderExpansionActive = expansion != null;
                } else {
                    placeholderExpansionActive = false;
                }
                metrics.addCustomChart(new SimplePie("using_placeholder_expansion", () -> placeholderExpansionActive ? "yes" : "no"));
            }
        }.runTaskLaterAsynchronously(this, 200);
    }

    @Override
    public @NotNull String getVersion() {
        return String.valueOf(this.getDescription().getVersion().charAt(0));
    }

    @Override
    public StatManager getStatManager() {
        return statManager;
    }

    @Override
    public StatTextFormatter getStatTextFormatter() {
        return OutputManager.getInstance().getMainMessageBuilder();
    }

    @Contract(" -> new")
    @Override
    public @NotNull StatNumberFormatter getStatNumberFormatter() {
        return new NumberFormatter();
    }
}