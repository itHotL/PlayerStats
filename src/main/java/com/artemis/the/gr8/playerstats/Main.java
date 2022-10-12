package com.artemis.the.gr8.playerstats;

import com.artemis.the.gr8.playerstats.api.PlayerStats;
import com.artemis.the.gr8.playerstats.api.PlayerStatsImpl;
import com.artemis.the.gr8.playerstats.msg.OutputManager;
import com.artemis.the.gr8.playerstats.commands.ReloadCommand;
import com.artemis.the.gr8.playerstats.commands.ShareCommand;
import com.artemis.the.gr8.playerstats.commands.StatCommand;
import com.artemis.the.gr8.playerstats.commands.TabCompleter;
import com.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.artemis.the.gr8.playerstats.listeners.JoinListener;
import com.artemis.the.gr8.playerstats.msg.msgutils.LanguageKeyHandler;
import com.artemis.the.gr8.playerstats.share.ShareManager;
import com.artemis.the.gr8.playerstats.statistic.RequestProcessor;
import com.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


/**
 * PlayerStats' Main class
 */
public final class Main extends JavaPlugin {

    private static JavaPlugin pluginInstance;
    private static BukkitAudiences adventure;

    private static ConfigHandler config;
    private static ThreadManager threadManager;
    private static LanguageKeyHandler languageKeyHandler;
    private static OfflinePlayerHandler offlinePlayerHandler;
    private static EnumHandler enumHandler;

    private static OutputManager outputManager;
    private static ShareManager shareManager;
    private static RequestProcessor requestProcessor;

    private static PlayerStats playerStatsImpl;


    @Override
    public void onEnable() {
        //initialize all the Managers, singletons, ConfigHandler and the API
        initializeMainClasses();
        setupMetrics();

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

    /**
     * @return Adventure's BukkitAudiences object
     * @throws IllegalStateException if PlayerStats is not enabled
     */
    public static @NotNull BukkitAudiences getAdventure() throws IllegalStateException {
        if (adventure == null) {
            throw new IllegalStateException("Tried to access Adventure without PlayerStats being enabled!");
        }
        return adventure;
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
        if (playerStatsImpl == null) {
            throw new IllegalStateException("PlayerStats does not seem to be loaded!");
        }
        return playerStatsImpl;
    }

    public static @NotNull RequestProcessor getRequestProcessor() throws IllegalStateException {
        if (requestProcessor == null) {
            throw new IllegalStateException("PlayerStats does not seem to be loaded!");
        }
        return requestProcessor;
    }

    /**
     * @return PlayerStats' ConfigHandler
     */
    public static @NotNull ConfigHandler getConfigHandler() {
        if (config == null) {
            config = new ConfigHandler();
        }
        return config;
    }

    public static @NotNull OfflinePlayerHandler getOfflinePlayerHandler() {
        if (offlinePlayerHandler == null) {
            offlinePlayerHandler = new OfflinePlayerHandler(getConfigHandler());
        }
        return offlinePlayerHandler;
    }

    public static @NotNull LanguageKeyHandler getLanguageKeyHandler() {
        if (languageKeyHandler == null) {
            languageKeyHandler = new LanguageKeyHandler();
        }
        return languageKeyHandler;
    }

    /**
     * Gets the EnumHandler. If there is no EnumHandler, one will be created.
     * @return PlayerStat's EnumHandler
     */
    public static @NotNull EnumHandler getEnumHandler() {
        if (enumHandler == null) {
            enumHandler = new EnumHandler();
        }
        return enumHandler;
    }

    private void initializeMainClasses() {
        pluginInstance = this;
        adventure = BukkitAudiences.create(this);
        enumHandler = new EnumHandler();
        languageKeyHandler = new LanguageKeyHandler();
        config = new ConfigHandler();

        offlinePlayerHandler = new OfflinePlayerHandler(config);
        shareManager = new ShareManager(config);
        outputManager = new OutputManager(adventure, config);
        requestProcessor = new RequestProcessor(offlinePlayerHandler, outputManager, shareManager);

        threadManager = new ThreadManager(config, outputManager);
        playerStatsImpl = new PlayerStatsImpl(offlinePlayerHandler, outputManager);
    }

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
}