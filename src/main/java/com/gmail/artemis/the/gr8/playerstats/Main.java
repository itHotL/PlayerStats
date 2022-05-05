package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.commands.StatCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {

        ConfigHandler config = new ConfigHandler(this);
        this.getCommand("statistic").setExecutor(new StatCommand());
        this.getCommand("statistic").setTabCompleter(new TabCompleter(this));
        this.getLogger().info("Enabled PlayerStats!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Disabled PlayerStats!");
    }


}
