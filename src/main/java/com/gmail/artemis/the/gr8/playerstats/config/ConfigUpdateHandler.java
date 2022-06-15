package com.gmail.artemis.the.gr8.playerstats.config;

import com.gmail.artemis.the.gr8.playerstats.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import com.tchristofferson.configupdater.ConfigUpdater;

public class ConfigUpdateHandler {

    /** Add new key-value pairs to the config without losing comments, using <a href="https://github.com/tchristofferson/Config-Updater">tchristofferson's Config-Updater</a> */
    public ConfigUpdateHandler(Main plugin, File configFile, int configVersion) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(configFile);
        configuration.set("config-version", configVersion);
        try {
            configuration.save(configFile);
            ConfigUpdater.update(plugin, configFile.getName(), configFile);
            plugin.getLogger().info("Your config has been updated to version " + configVersion +
                    "! This should have migrated your settings, but double-check your config.yml if you suspect something went wrong.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
