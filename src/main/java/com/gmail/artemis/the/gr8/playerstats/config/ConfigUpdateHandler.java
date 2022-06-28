package com.gmail.artemis.the.gr8.playerstats.config;

import com.gmail.artemis.the.gr8.playerstats.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import com.tchristofferson.configupdater.ConfigUpdater;

public class ConfigUpdateHandler {

    /** Add new key-value pairs to the config without losing comments, using <a href="https://github.com/tchristofferson/Config-Updater">tchristofferson's Config-Updater</a> */
    public ConfigUpdateHandler(Main plugin, File configFile, double configVersion) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(configFile);
        updateTopListDefault(configuration);
        updateDefaultColors(configuration);
        configuration.set("config-version", configVersion);
        try {
            configuration.save(configFile);
            ConfigUpdater.update(plugin, configFile.getName(), configFile);
            plugin.getLogger().warning("Your config has been updated to version " + configVersion +
                    ". This version includes some slight changes in the default color scheme, but none of your custom settings should have been changed!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Adjusts the value for "top-list" to migrate the config file from versions 1 or 2 to version 3.*/
    private void updateTopListDefault(YamlConfiguration configuration) {
        String oldTitle = configuration.getString("top-list-title");
        if (oldTitle != null && oldTitle.equalsIgnoreCase("Top [x]")) {
            configuration.set("top-list-title", "Top");
        }
    }

    /** Adjusts some of the default colors to migrate from versions 2 or 3 to version 4.*/
    private void updateDefaultColors(YamlConfiguration configuration) {
        updateColor(configuration, "top-list.title", "yellow", "#FFD52B");
        updateColor(configuration, "top-list.stat-names", "yellow", "#FFD52B");
        updateColor(configuration, "top-list.sub-stat-names", "#FFD52B", "yellow");
        updateColor(configuration, "individual-statistics.stat-names", "yellow", "#FFD52B");
        updateColor(configuration, "individual-statistics.sub-stat-names", "#FFD52B", "yellow");
        updateColor(configuration, "total-server.title", "gold", "#55AAFF");
        updateColor(configuration, "total-server.server-name", "gold", "#55AAFF");
        updateColor(configuration, "total-server.stat-names", "yellow", "#FFD52B");
        updateColor(configuration, "total-server.sub-stat-names", "#FFD52B", "yellow");
    }

    private void updateColor(YamlConfiguration configuration, String path, String oldValue, String newValue) {
        String configString = configuration.getString(path);
        if (configString != null && configString.equalsIgnoreCase(oldValue)) {
            configuration.set(path, newValue);
        }
    }
}