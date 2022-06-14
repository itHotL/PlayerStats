package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.commands.ReloadCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.StatCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.TabCompleter;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.listeners.JoinListener;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageFactory;
import com.gmail.artemis.the.gr8.playerstats.msg.PrideMessageFactory;
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
        MessageFactory messageFactory = new PrideMessageFactory(config);
        ThreadManager threadManager = new ThreadManager(this, adventure(), config, messageFactory);

        //register the commands
        PluginCommand statcmd = this.getCommand("statistic");
        if (statcmd != null) {
            statcmd.setExecutor(new StatCommand(threadManager, adventure(), messageFactory));
            statcmd.setTabCompleter(new TabCompleter());
        }
        PluginCommand reloadcmd = this.getCommand("statisticreload");
        if (reloadcmd != null) reloadcmd.setExecutor(new ReloadCommand(threadManager));

        //register the listener
        Bukkit.getPluginManager().registerEvents(new JoinListener(adventure(), config, messageFactory, threadManager), this);
        logTimeTaken("onEnable", "time taken", time);
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

    public void logTimeTaken(String className, String methodName, long previousTime) {
        getLogger().info(className + ", " + methodName + ": " + (System.currentTimeMillis() - previousTime) + "ms");
    }
}
