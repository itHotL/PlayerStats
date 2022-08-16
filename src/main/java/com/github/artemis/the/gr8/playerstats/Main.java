package com.github.artemis.the.gr8.playerstats;

import com.github.artemis.the.gr8.playerstats.api.PlayerStats;
import com.github.artemis.the.gr8.playerstats.msg.OutputManager;
import com.github.artemis.the.gr8.playerstats.api.PlayerStatsAPI;
import com.github.artemis.the.gr8.playerstats.commands.ReloadCommand;
import com.github.artemis.the.gr8.playerstats.commands.ShareCommand;
import com.github.artemis.the.gr8.playerstats.commands.StatCommand;
import com.github.artemis.the.gr8.playerstats.commands.TabCompleter;
import com.github.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.github.artemis.the.gr8.playerstats.listeners.JoinListener;
import com.github.artemis.the.gr8.playerstats.msg.InternalFormatter;
import com.github.artemis.the.gr8.playerstats.msg.MessageBuilder;
import com.github.artemis.the.gr8.playerstats.msg.msgutils.LanguageKeyHandler;
import com.github.artemis.the.gr8.playerstats.statistic.StatCalculator;
import com.github.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.github.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
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

    private static Main instance;
    private static BukkitAudiences adventure;

    private static ConfigHandler config;
    private static LanguageKeyHandler languageKeyHandler;
    private static OfflinePlayerHandler offlinePlayerHandler;
    private static EnumHandler enumHandler;

    private static OutputManager outputManager;
    private static ShareManager shareManager;
    private static StatCalculator statCalculator;
    private static ThreadManager threadManager;

    private static PlayerStats playerStatsAPI;


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
     * @return PlayerStats' ConfigHandler
     * @throws IllegalStateException if PlayerStats is not enabled
     */
    public static @NotNull ConfigHandler getConfigHandler() throws IllegalStateException {
        if (config == null) {
            throw new IllegalStateException("PlayerStats does not seem to be loaded!");
        }
        return config;
    }

    public static @NotNull OfflinePlayerHandler getOfflinePlayerHandler() throws IllegalStateException {
        if (offlinePlayerHandler == null) {
            throw new IllegalStateException("PlayerStats does not seem to be loaded!");
        }
        return offlinePlayerHandler;
    }

    public static @NotNull LanguageKeyHandler getLanguageKeyHandler() {
        if (languageKeyHandler == null) {
            languageKeyHandler = new LanguageKeyHandler(instance);
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
        instance = this;
        adventure = BukkitAudiences.create(this);

        config = new ConfigHandler(this);
        enumHandler = new EnumHandler();
        languageKeyHandler = new LanguageKeyHandler(instance);
        offlinePlayerHandler = new OfflinePlayerHandler();

        shareManager = new ShareManager(config);
        statCalculator = new StatCalculator(offlinePlayerHandler);
        outputManager = new OutputManager(adventure, config, shareManager);
        threadManager = new ThreadManager(config, statCalculator, outputManager);

        MessageBuilder apiMessageBuilder = MessageBuilder.defaultBuilder(config);
        playerStatsAPI = new PlayerStatsAPI(apiMessageBuilder, offlinePlayerHandler);
    }

    private void setupMetrics() {
        new BukkitRunnable() {
            @Override
            public void run() {
                final Metrics metrics = new Metrics(instance, 15923);
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