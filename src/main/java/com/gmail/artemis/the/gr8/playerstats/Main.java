package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.commands.ReloadCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.StatCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.TabCompleter;
import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.listeners.JoinListener;
import com.gmail.artemis.the.gr8.playerstats.msg.LanguageKeyHandler;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageFactory;
import com.gmail.artemis.the.gr8.playerstats.msg.PrideMessageFactory;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.Month;


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
        //initialize the Adventure library
        adventure = BukkitAudiences.create(this);

        //first get an instance of the ConfigHandler and LanguageKeyHandler
        ConfigHandler config = new ConfigHandler(this);
        LanguageKeyHandler language = new LanguageKeyHandler();

        //then determine if we need a regular MessageFactory or a festive one
        MessageFactory messageFactory;
        if (config.useFestiveFormatting()) {
            if (LocalDate.now().getMonth().equals(Month.JUNE)) {
                messageFactory = new PrideMessageFactory(config, language);
            }
            else {
                messageFactory = new MessageFactory(config, language);
            }
        }
        else {
            messageFactory = new MessageFactory(config, language);
        }

        //initialize the threadManager
        ThreadManager threadManager = new ThreadManager(adventure(), config, messageFactory, this);

        //register all commands and the tabCompleter
        PluginCommand statcmd = this.getCommand("statistic");
        if (statcmd != null) {
            statcmd.setExecutor(new StatCommand(adventure(), messageFactory, threadManager));
            statcmd.setTabCompleter(new TabCompleter());
        }
        PluginCommand reloadcmd = this.getCommand("statisticreload");
        if (reloadcmd != null) reloadcmd.setExecutor(new ReloadCommand(threadManager));

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

    public void logTimeTaken(String className, String methodName, long previousTime) {
        getLogger().info(className + ", " + methodName + ": " + (System.currentTimeMillis() - previousTime) + "ms");
    }
}
