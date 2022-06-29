package com.gmail.artemis.the.gr8.playerstats.config;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class ConfigHandler {

    private File configFile;
    private FileConfiguration config;
    private final Main plugin;
    private final double configVersion;

    public ConfigHandler(Main p) {
        plugin = p;

        saveDefaultConfig();
        config = YamlConfiguration.loadConfiguration(configFile);

        configVersion = 4;
        checkConfigVersion();

        MyLogger.setDebugLevel(debugLevel());
    }

    /** Checks the number that "config-version" returns to see if the config needs updating, and if so, send it to the Updater.
     <p>PlayerStats 1.1: "config-version" doesn't exist.</p>
     <p>PlayerStats 1.2: "config-version" is 2.</p>
     <p>PlayerStats 1.3: "config-version" is 3. </P>
     <p>PlayerStats 1.4: "config-version" is 4.</p>*/
    private void checkConfigVersion() {
        if (!config.contains("config-version") || config.getDouble("config-version") != configVersion) {
            new ConfigUpdateHandler(plugin, configFile, configVersion);
            reloadConfig();
        }
    }

    /** Create a config file if none exists yet (from the config.yml in the plugin's resources). */
    private void saveDefaultConfig() {
        config = plugin.getConfig();
        plugin.saveDefaultConfig();
        configFile = new File(plugin.getDataFolder(), "config.yml");
    }

    /** Reloads the config from file, or creates a new file with default values if there is none.
     Also reads the value for debug-level and passes it on to MyLogger. */
    public boolean reloadConfig() {
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            MyLogger.setDebugLevel(debugLevel());
            return true;
        }
        catch (IllegalArgumentException e) {
            MyLogger.logException(e, "ConfigHandler", "reloadConfig");
            return false;
        }
    }

    /** Returns the desired debugging level.
     <p>1 = low (only show unexpected errors)</p>
     <p>2 = medium (detail all encountered exceptions, log main tasks and show time taken)</p>
     <p>3 = high (log all tasks and time taken)</p>
     <p>Default: 1</p>*/
    public int debugLevel() {
        return config.getInt("debug-level", 1);
    }

    /** Returns the config setting for include-whitelist-only.
     <p>Default: false</p>*/
    public boolean whitelistOnly() {
        return config.getBoolean("include-whitelist-only", false);
    }

    /** Returns the config setting for exclude-banned-players.
     <p>Default: false</p>*/
    public boolean excludeBanned() {
        return config.getBoolean("exclude-banned-players", false);
    }

    /** Returns the number of maximum days since a player has last been online.
     <p>Default: 0 (which signals not to use this limit)</p>*/
    public int lastPlayedLimit() {
        return config.getInt("number-of-days-since-last-joined", 0);
    }

    /** Whether to use TranslatableComponents for statistic, block, item and entity names.
     <p>Default: true</p>*/
    public boolean useTranslatableComponents() {
        return config.getBoolean("translate-to-client-language", true);
    }

    /** Whether to use HoverComponents in the usage explanation.
     <p>Default: true</p>*/
    public boolean useHoverText() {
        return config.getBoolean("enable-hover-text", true);
    }

    /** Whether to use festive formatting, such as pride colors.
     <p>Default: true</p> */
    public boolean useFestiveFormatting() {
        return config.getBoolean("enable-festive-formatting", true);
    }

    /** Whether to use rainbow colors for the [PlayerStats] prefix rather than the default gold/purple.
     <p>Default: false</p> */
    public boolean useRainbowPrefix() {
        return config.getBoolean("rainbow-mode", false);
    }

    /** Returns the config setting for use-dots.
     <p>Default: true</p>*/
    public boolean useDots() {
        return config.getBoolean("use-dots", true);
    }

    /** Returns the config setting for top-list-max-size.
     <p>Default: 10</p> */
    public int getTopListMaxSize() {
        return config.getInt("top-list-max-size", 10);
    }

    /** Returns a String that represents the title for a top statistic.
     <p>Default: "Top"</p>*/
    public String getTopStatsTitle() {
        return config.getString("top-list-title", "Top");
    }

    /** Returns a String that represents the title for a server stat.
     <p>Default: "Total on"</p> */
    public String getServerTitle() {
        return config.getString("total-server-stat-title", "Total on");
    }

    /** Returns the specified server name for a server stat title.
     <p>Default: "this server"</p>*/
    public String getServerName() {
        return config.getString("your-server-name", "this server");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or a Style. Default values are:
     <p>Style: "none"</p>
     <p>Color Top: "green"</p>
     <p>Color Individual/Server: "gold"</p>*/
    public String getPlayerNameFormatting(Target selection, boolean isStyle) {
        String def;
        if (selection == Target.TOP) {
            def = "green";
        }
        else {
            def = "gold";
        }
        return getStringFromConfig(selection, isStyle, def, "player-names");
    }

    /** Returns true if playerNames Style is "bold", false if it is not.
     <p>Default: false</p>*/
    public boolean playerNameIsBold() {
        ConfigurationSection style = getRelevantSection(Target.PLAYER);

        if (style != null) {
            String styleString = style.getString("player-names");
            return styleString != null && styleString.equalsIgnoreCase("bold");
        }
        return false;
    }

    /** Returns a String that represents either a Chat Color, hex color code, or a Style. Default values are:
     <p>Style: "none"</p>
     <p>Color: "yellow"</p>*/
    public String getStatNameFormatting(Target selection, boolean isStyle) {
        return getStringFromConfig(selection, isStyle, "yellow", "stat-names");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or a Style. Default values are:
     <p>Style: "none"</p>
     <p>Color: "#FFD52B"</p>*/
    public String getSubStatNameFormatting(Target selection, boolean isStyle) {
        return getStringFromConfig(selection, isStyle, "#FFD52B", "sub-stat-names");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <p>Style: "none"</p>
     <p>Color Top: "#55AAFF"</p>
     <p>Color Individual/Server: "#ADE7FF"</p> */
    public String getStatNumberFormatting(Target selection, boolean isStyle) {
        String def;
        if (selection == Target.TOP) {
            def = "#55AAFF";
        }
        else {
            def = "#ADE7FF";
        }
        return getStringFromConfig(selection, isStyle, def,"stat-numbers");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <p>Style: "none"</p>
     <p>Color Top: "yellow"</p>
     <p>Color Server: "gold"</p>*/
    public String getTitleFormatting(Target selection, boolean isStyle) {
        String def;
        if (selection == Target.TOP) {
            def = "yellow";
        }
        else {
            def = "gold";
        }
        return getStringFromConfig(selection, isStyle, def, "title");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <p>Style: "none"</p>
     <p>Color: "gold"</p>*/
    public String getTitleNumberFormatting(boolean isStyle) {
        return getStringFromConfig(Target.TOP, isStyle, "gold", "title-number");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <p>Style: "none"</p>
     <p>Color: "#FFB80E"</p>*/
    public String getServerNameFormatting(boolean isStyle) {
        return getStringFromConfig(Target.SERVER, isStyle, "#FFB80E", "server-name");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <p>Style: "none"</p>
     <p>Color: "gold"</p>*/
    public String getRankNumberFormatting(boolean isStyle) {
        return getStringFromConfig(Target.TOP, isStyle, "gold", "rank-numbers");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <p>Style: "none"</p>
     <p>Color: "dark_gray"</p> */
    public String getDotsFormatting(boolean isStyle) {
        return getStringFromConfig(Target.TOP, isStyle, "dark_gray", "dots");
    }

    /** Returns the config value for a color or style option in string-format, the supplied default value, or null if no configSection was found. */
    private @Nullable String getStringFromConfig(Target selection, boolean isStyle, String def, String pathName){
        String path = isStyle ? pathName + "-style" : pathName;
        String defaultValue = isStyle ? "none" : def;

        ConfigurationSection section = getRelevantSection(selection);
        return section != null ? section.getString(path, defaultValue) : null;
    }

    /** Returns the config section that contains the relevant color or style option. */
    private @Nullable ConfigurationSection getRelevantSection(Target selection) {
        switch (selection) {
            case TOP -> {
                return config.getConfigurationSection("top-list");
            }
            case PLAYER -> {
                return config.getConfigurationSection("individual-statistics");
            }
            case SERVER -> {
                return config.getConfigurationSection("total-server");
            }
            default -> {
                return null;
            }
        }
    }
}