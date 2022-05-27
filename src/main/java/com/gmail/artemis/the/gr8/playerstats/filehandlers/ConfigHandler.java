package com.gmail.artemis.the.gr8.playerstats.filehandlers;

import com.gmail.artemis.the.gr8.playerstats.Main;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


import java.io.File;

public class ConfigHandler {

    private File configFile;
    private FileConfiguration config;
    private final Main plugin;

    public ConfigHandler(Main p) {
        plugin = p;
        saveDefaultConfig();
    }

    //reload the config after changes have been made to it
    public boolean reloadConfig() {
        try {
            if (!configFile.exists()) {
                saveDefaultConfig();
            }
            config = YamlConfiguration.loadConfiguration(configFile);
            return true;
        }
        catch (Exception e) {
            plugin.getLogger().warning(e.toString());
            return false;
        }
    }

    //returns the config setting for include-whitelist-only, or the default value "false"
    public boolean whitelistOnly() {
        try {
            return config.getBoolean("include-whitelist-only");
        }
        catch (Exception e) {
            plugin.getLogger().warning(e.toString());
            return false;
        }
    }

    //returns the config setting for exclude-banned-players, or the default value "false"
    public boolean excludeBanned() {
        try {
            return config.getBoolean("exclude-banned-players");
        }
        catch (Exception e) {
            plugin.getLogger().warning(e.toString());
            return false;
        }
    }

    //returns the number of maximum days since a player has last been online, or the default value of 0 to not use this constraint
    public int lastPlayedLimit() {
        try {
            return config.getInt("number-of-days-since-last-joined");
        }
        catch (Exception e) {
            plugin.getLogger().warning(e.toString());
            return 0;
        }
    }

    //returns the config setting for top-list-max-size, or the default value of 10 if no value can be retrieved
    public int getTopListMaxSize() {
        try {
            return config.getInt("top-list-max-size");
        }
        catch (Exception e) {
            plugin.getLogger().warning(e.toString());
            return 10;
        }
    }

    //returns the config setting for use-dots, or the default value "true" if no value can be retrieved
    public boolean useDots() {
        try {
            return config.getBoolean("use-dots");
        }
        catch (Exception e) {
            plugin.getLogger().warning(e.toString());
            return true;
        }
    }

    public String getPlayerNameFormatting(boolean topStat, boolean isStyle) {
        ConfigurationSection section = getRelevantSection(topStat, isStyle);
        return section != null ? section.getString("player-names") : null;
    }

    public boolean playerNameIsBold() {
        ConfigurationSection style = getRelevantSection(true, true);

        if (style != null) {
            String styleString = style.getString("player-names");
            return styleString != null && styleString.equalsIgnoreCase("bold");
        }
        return false;
    }

    public String getStatNameFormatting(boolean topStat, boolean isStyle) {
        ConfigurationSection section = getRelevantSection(topStat, isStyle);
        return section != null ? section.getString("stat-names") : null;
    }

    public String getSubStatNameFormatting(boolean topStat, boolean isStyle) {
        ConfigurationSection section = getRelevantSection(topStat, isStyle);
        return section != null ? section.getString("sub-stat-names") : null;
    }

    public String getStatNumberFormatting(boolean topStat, boolean isStyle) {
        ConfigurationSection section = getRelevantSection(topStat, isStyle);
        return section != null ? section.getString("stat-numbers") : null;
    }

    public String getListNumberFormatting(boolean isStyle) {
        ConfigurationSection section = getRelevantSection(true, isStyle);
        return section != null ? section.getString("list-numbers") : null;
    }

    public String getDotsColor() {
        ConfigurationSection section = getRelevantSection(true, false);
        return section != null ? section.getString("dots") : null;
    }

    private ConfigurationSection getRelevantSection(boolean topStat, boolean isStyle) {
        ConfigurationSection section;
        try {
            if (!topStat) {
                if (!isStyle) section = config.getConfigurationSection("individual-statistics-color");
                else section = config.getConfigurationSection("individual-statistics-style");
            }
            else {
                if (!isStyle) section = config.getConfigurationSection("top-list-color");
                else section = config.getConfigurationSection("top-list-style");
            }
            return section;
        }
        catch (IllegalArgumentException | NullPointerException exception) {
            plugin.getLogger().warning(exception.toString());
            return null;
        }
    }

    //create a config file if none exists yet (from the config.yml in the plugin's resources)
    private void saveDefaultConfig() {
        config = plugin.getConfig();
        plugin.saveDefaultConfig();
        configFile = new File(plugin.getDataFolder(), "config.yml");

    }
}
