package com.gmail.artemis.the.gr8.playerstats;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigHandler {

    private File configFile;
    private FileConfiguration config;
    private final Main plugin;

    public ConfigHandler (Main p) {
        plugin = p;
        saveDefaultConfig();
    }

    //create a config file if none exists yet (from the config.yml in the plugin's resources)
    private void saveDefaultConfig() {
        config = plugin.getConfig();
        plugin.saveDefaultConfig();
        configFile = new File(plugin.getDataFolder(), "config.yml");
    }

    //reload the config after changes have been made to it
    public boolean reloadConfig() {
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
