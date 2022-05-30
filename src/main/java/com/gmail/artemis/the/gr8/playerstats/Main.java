package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.commands.ReloadCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.StatCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.TabCompleter;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.listeners.JoinListener;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.MessageFactory;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


public class Main extends JavaPlugin {

    private BukkitAudiences adventure;

    public @NotNull BukkitAudiences adventure() {
        if (adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();

        //initialize the Adventure library
        adventure = BukkitAudiences.create(this);

        //get instances of the classes that should be initialized
        ConfigHandler config = new ConfigHandler(this);
        MessageFactory messageFactory = new MessageFactory(config, this);
        OfflinePlayerHandler offlinePlayerHandler = new OfflinePlayerHandler(config);
        getLogger().info("Amount of offline players: " + offlinePlayerHandler.getOfflinePlayerCount());

        //register the commands
        PluginCommand statcmd = this.getCommand("statistic");
        if (statcmd != null) {
            statcmd.setExecutor(new StatCommand(adventure(), config, offlinePlayerHandler, messageFactory, this));
            statcmd.setTabCompleter(new TabCompleter(offlinePlayerHandler));
        }
        PluginCommand reloadcmd = this.getCommand("statisticreload");
        if (reloadcmd != null) reloadcmd.setExecutor(new ReloadCommand(config, offlinePlayerHandler, this));

        //register the listener
        Bukkit.getPluginManager().registerEvents(new JoinListener(offlinePlayerHandler), this);
        logTimeTaken("Time", "taken", time);
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

    public long logTimeTaken(String className, String methodName, long previousTime) {
        getLogger().info(className + " " + methodName + ": " + (System.currentTimeMillis() - previousTime) + "ms");
        return System.currentTimeMillis();
    }
}
