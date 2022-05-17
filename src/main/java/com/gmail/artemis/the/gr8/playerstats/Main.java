package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.commands.ReloadCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.StatCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.TabCompleter;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.listeners.JoinListener;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OutputFormatter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        ConfigHandler config = new ConfigHandler(this);
        EnumHandler enumHandler = new EnumHandler(this);
        OutputFormatter outputFormatter = new OutputFormatter(config);

        //prepare private hashMap of offline players
        OfflinePlayerHandler.updateOfflinePlayers();

        this.getCommand("statistic").setExecutor(new StatCommand(outputFormatter, enumHandler, this));
        this.getCommand("statistic").setTabCompleter(new TabCompleter(enumHandler, this));
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
