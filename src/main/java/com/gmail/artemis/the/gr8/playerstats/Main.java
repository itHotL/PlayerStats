package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.commands.StatCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.TabCompleter;
import com.gmail.artemis.the.gr8.playerstats.listeners.JoinListener;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {

        ConfigHandler config = new ConfigHandler(this);
        EnumHandler enumHandler = new EnumHandler();
        OfflinePlayerHandler offlinePlayerHandler = new OfflinePlayerHandler();
        StatManager statManager = new StatManager(enumHandler, this);

        this.getCommand("statistic").setExecutor(new StatCommand(enumHandler, statManager));
        this.getCommand("statistic").setTabCompleter(new TabCompleter(
                enumHandler, offlinePlayerHandler, statManager,this));
        this.getLogger().info("Enabled PlayerStats!");

        Bukkit.getPluginManager().registerEvents(new JoinListener(offlinePlayerHandler), this);
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Disabled PlayerStats!");
    }


}
