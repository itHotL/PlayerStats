package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.commands.ReloadCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.StatCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.TabCompleter;
import com.gmail.artemis.the.gr8.playerstats.listeners.JoinListener;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OutputFormatter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {

        ConfigHandler config = new ConfigHandler(this);
        EnumHandler enumHandler = new EnumHandler();

        OutputFormatter outputFormatter = new OutputFormatter(config, this);
        StatManager statManager = new StatManager(enumHandler, this);

        this.getCommand("statistic").setExecutor(new StatCommand(outputFormatter, statManager, this));
        this.getCommand("statistic").setTabCompleter(new TabCompleter(
                enumHandler, statManager,this));
        this.getCommand("statisticreload").setExecutor(new ReloadCommand(config, outputFormatter, this));

        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        this.getLogger().info("Enabled PlayerStats!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Disabled PlayerStats!");
    }

    public long logTimeTaken(String className, String methodName, long previousTime, int lineNumber) {
        getLogger().info(className + " " + methodName + " " + lineNumber + ": " + (System.currentTimeMillis() - previousTime));
        return System.currentTimeMillis();
    }
}
