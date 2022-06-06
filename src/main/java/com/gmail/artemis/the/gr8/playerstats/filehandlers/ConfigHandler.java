package com.gmail.artemis.the.gr8.playerstats.filehandlers;

import com.gmail.artemis.the.gr8.playerstats.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;


import java.io.File;

public class ConfigHandler {

    private File configFile;
    private FileConfiguration config;
    private final Main plugin;

    public ConfigHandler(Main p) {
        plugin = p;
        saveDefaultConfig();
    }

    /** Reloads the config from file, or creates a new file with default values if there is none. */
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

    /** Returns the config setting for include-whitelist-only, or the default value "false". */
    public boolean whitelistOnly() {
        return config.getBoolean("include-whitelist-only", false);
    }

    /** Returns the config setting for exclude-banned-players, or the default value "false". */
    public boolean excludeBanned() {
        return config.getBoolean("exclude-banned-players", false);
    }

    /** Returns the number of maximum days since a player has last been online, or the default value of 0 to not use this constraint. */
    public int lastPlayedLimit() {
        return config.getInt("number-of-days-since-last-joined", 0);
    }

    /** Returns the config setting for top-list-max-size, or the default value of 10 if no value can be retrieved. */
    public int getTopListMaxSize() {
        return config.getInt("top-list-max-size", 10);
    }

    /** Returns the config setting for use-dots, or the default value "true" if no value can be retrieved. */
    public boolean useDots() {
        return config.getBoolean("use-dots", true);
    }

    /** Returns the specified server name, or "this server" if no value can be retrieved. */
    public String getServerName() {
        return config.getString("your-server-name", "this server");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are "none" for Style,
     and "green" or "gold" for Color (for top or individual color). */
    public String getPlayerNameFormatting(boolean topStat, boolean isStyle) {
        String def = topStat ? "green" : "gold";
        return getStringFromConfig(topStat, false, isStyle, def, "player-names");
    }

    public boolean playerNameIsBold() {
        ConfigurationSection style = getRelevantSection(true, true);

        if (style != null) {
            String styleString = style.getString("player-names");
            return styleString != null && styleString.equalsIgnoreCase("bold");
        }
        return false;
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are "none" for Style,
     and "yellow" for Color. */
    public String getStatNameFormatting(boolean topStat, boolean serverStat, boolean isStyle) {
        return getStringFromConfig(topStat, serverStat, isStyle, "yellow", "stat-names");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are "none" for Style,
     and "#FFD52B" for Color. */
    public String getSubStatNameFormatting(boolean topStat, boolean serverStat, boolean isStyle) {
        return getStringFromConfig(topStat, serverStat, isStyle, "#FFD52B", "sub-stat-names");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are "none" for Style,
     and "#55AAFF" or "#ADE7FF" for Color (for the top or individual color). */
    public String getStatNumberFormatting(boolean topStat, boolean serverStat, boolean isStyle) {
        String def = topStat ? "#55AAFF" : "#ADE7FF";
        return getStringFromConfig(topStat, serverStat, isStyle, def,"stat-numbers");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are "none" for Style,
     and "yellow" for Color. */
    public String getTitleFormatting(boolean topStat, boolean isStyle) {
        return getStringFromConfig(topStat, (!topStat), isStyle, "yellow", "title");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are "none" for Style,
     and "gold" for Color. */
    public String getTitleNumberFormatting(boolean isStyle) {
        return getStringFromConfig(true, false, isStyle, "gold", "title-number");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are "none" for Style,
     and "#FFB80E" for Color. */
    public String getServerNameFormatting(boolean isStyle) {
        return getStringFromConfig(false, true, isStyle, "#FFB80E", "server-name");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are "none" for Style,
     and "gold" for Color. */
    public String getRankNumberFormatting(boolean isStyle) {
        return getStringFromConfig(true, false, isStyle, "gold", "rank-numbers");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are "none" for Style,
     and "dark_gray" for Color. */
    public String getDotsFormatting(boolean isStyle) {
        return getStringFromConfig(true, false, isStyle, "dark_gray", "dots");
    }

    /** Returns the config value for a color or style option in string-format, the supplied default value, or null if no configSection was found. */
    private @Nullable String getStringFromConfig(boolean topStat, boolean serverStat, boolean isStyle, String def, String pathName){
        String path = isStyle ? pathName + "-style" : pathName;
        String defaultValue = isStyle ? "none" : def;

        ConfigurationSection section = getRelevantSection(topStat, serverStat);
        return section != null ? section.getString(path, defaultValue) : null;
    }

    /** Returns the config section that contains the relevant color or style option. */
    private @Nullable ConfigurationSection getRelevantSection(boolean topStat, boolean serverStat) {
        if (topStat) {
            return config.getConfigurationSection("top-list");
        }
        else if (serverStat) {
            return config.getConfigurationSection("total-server");
        }
        else {
            return config.getConfigurationSection("individual-statistics");
        }
    }

    /** Create a config file if none exists yet (from the config.yml in the plugin's resources). */
    private void saveDefaultConfig() {
        config = plugin.getConfig();
        plugin.saveDefaultConfig();
        configFile = new File(plugin.getDataFolder(), "config.yml");
    }
}
