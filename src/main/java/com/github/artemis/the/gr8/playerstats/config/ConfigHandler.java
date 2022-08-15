package com.github.artemis.the.gr8.playerstats.config;

import com.github.artemis.the.gr8.playerstats.Main;
import com.github.artemis.the.gr8.playerstats.enums.Target;
import com.github.artemis.the.gr8.playerstats.enums.Unit;
import com.github.artemis.the.gr8.playerstats.utils.MyLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public final class ConfigHandler {

    private static Main plugin;
    private static int configVersion;

    private File configFile;
    private FileConfiguration config;

    public ConfigHandler(Main plugin) {
        ConfigHandler.plugin = plugin;
        configVersion = 6;

        saveDefaultConfig();
        config = YamlConfiguration.loadConfiguration(configFile);
        checkConfigVersion();

        MyLogger.setDebugLevel(getDebugLevel());
    }

    /** Checks the number that "config-version" returns to see if the config needs updating, and if so, send it to the {@link ConfigUpdateHandler}.
     <br></br>
     <br>PlayerStats 1.1: "config-version" doesn't exist.</br>
     <br>PlayerStats 1.2: "config-version" is 2.</br>
     <br>PlayerStats 1.3: "config-version" is 3. </br>
     <br>PlayerStats 1.4: "config-version" is 4.</br>*/
    private void checkConfigVersion() {
        if (!config.contains("config-version") || config.getInt("config-version") != configVersion) {
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
     Also reads the value for debug-level and passes it on to {@link MyLogger}. */
    public boolean reloadConfig() {
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            return true;
        }
        catch (IllegalArgumentException e) {
            MyLogger.logException(e, "ConfigHandler", "reloadConfig");
            return false;
        }
    }

    /** Returns the desired debugging level.
     <br></br>
     <br>1 = low (only show unexpected errors)</br>
     <br>2 = medium (detail all encountered exceptions, log main tasks and show time taken)</br>
     <br>3 = high (log all tasks and time taken)</br>
     <br></br>
     <br>Default: 1</br>*/
    public int getDebugLevel() {
        return config.getInt("debug-level", 1);
    }

    /** Returns true if command-senders should be limited to one stat-request at a time.
     <br>Default: true</br>*/
    public boolean limitStatRequests() {
        return config.getBoolean("only-allow-one-lookup-at-a-time-per-player", true);
    }

    /** Returns true if stat-sharing is allowed.
     <br>Default: true</br>*/
    public boolean allowStatSharing() {
        return config.getBoolean("enable-stat-sharing", true);
    }

    /** Returns the number of minutes a player has to wait before being able to
     share another stat-result.
     <br>Default: 0</br>*/
    public int getStatShareWaitingTime() {
        return config.getInt("waiting-time-before-sharing-again", 0);
    }

    /** Returns the config setting for include-whitelist-only.
     <br>Default: false</br>*/
    public boolean whitelistOnly() {
        return config.getBoolean("include-whitelist-only", false);
    }

    /** Returns the config setting for exclude-banned-players.
     <br>Default: false</br>*/
    public boolean excludeBanned() {
        return config.getBoolean("exclude-banned-players", false);
    }

    /** Returns the number of maximum days since a player has last been online.
     <br>Default: 0 (which signals not to use this limit)</br>*/
    public int getLastPlayedLimit() {
        return config.getInt("number-of-days-since-last-joined", 0);
    }

    /** Whether to use TranslatableComponents wherever possible.
     Currently supported: statistic, block, item and entity names.
     <br>Default: true</br>*/
    public boolean useTranslatableComponents() {
        return config.getBoolean("translate-to-client-language", true);
    }

    /** Whether to use HoverComponents for additional information.
     <br>Default: true</br>*/
    public boolean useHoverText() {
        return config.getBoolean("enable-hover-text", true);
    }

    /** Whether to use festive formatting, such as pride colors.
     <br>Default: true</br> */
    public boolean useFestiveFormatting() {
        return config.getBoolean("enable-festive-formatting", true);
    }

    /** Whether to use rainbow colors for the [PlayerStats] prefix rather than the default gold/purple.
     <br>Default: false</br> */
    public boolean useRainbowMode() {
        return config.getBoolean("rainbow-mode", false);
    }

    /** Whether to use enters before the statistic output in chat.
     Enters create some separation between the previous things that have been said in chat and the stat-result.
     <br>Default: true for non-shared top statistics, false for everything else</br>*/
    public boolean useEnters(Target selection, boolean getSharedSetting) {
        ConfigurationSection section = config.getConfigurationSection("use-enters");
        boolean def = selection == Target.TOP && !getSharedSetting;
        if (section != null) {
            String path = switch (selection) {
                case TOP -> getSharedSetting ? "top-stats-shared" : "top-stats";
                case PLAYER -> getSharedSetting ? "player-stats-shared" : "player-stats";
                case SERVER -> getSharedSetting ? "server-stats-shared" : "server-stats";
            };
            return section.getBoolean(path, def);
        }
        MyLogger.logWarning("Config settings for use-enters could not be retrieved! " +
                "Please check your file if you want to use custom settings. " +
                "Using default values...");
        return def;
    }

    /** Returns the config setting for use-dots.
     <br>Default: true</br>*/
    public boolean useDots() {
        return config.getBoolean("use-dots", true);
    }

    /** Returns the config setting for top-list-max-size.
     <br>Default: 10</br> */
    public int getTopListMaxSize() {
        return config.getInt("top-list-max-size", 10);
    }

    /** Returns a String that represents the title for a top statistic.
     <br>Default: "Top"</br>*/
    public String getTopStatsTitle() {
        return config.getString("top-list-title", "Top");
    }

    /** Returns a String that represents the title for a server stat.
     <br>Default: "Total on"</br> */
    public String getServerTitle() {
        return config.getString("total-server-stat-title", "Total on");
    }

    /** Returns the specified server name for a server stat title.
     <br>Default: "this server"</br>*/
    public String getServerName() {
        return config.getString("your-server-name", "this server");
    }

    /** Returns the unit that should be used for distance-related statistics.
     <br>Default: Blocks for plain text, km for hover-text</br>*/
    public String getDistanceUnit(boolean isUnitForHoverText) {
        return getUnitString(isUnitForHoverText, "blocks", "km", "distance-unit");
    }

    /** Returns the unit that should be used for damage-based statistics.
     <br>Default: Hearts for plain text, HP for hover-text.</br>*/
    public String getDamageUnit(boolean isUnitForHoverText) {
        return getUnitString(isUnitForHoverText, "hearts", "hp", "damage-unit");
    }

    /** Whether PlayerStats should automatically detect the most suitable unit to use for time-based statistics.
     <br>Default: true</br>*/
    public boolean autoDetectTimeUnit(boolean isUnitForHoverText) {
        String path = "auto-detect-biggest-time-unit";
        if (isUnitForHoverText) {
            path = path + "-for-hover-text";
        }
        boolean defaultValue = !isUnitForHoverText;
        return config.getBoolean(path, defaultValue);
    }

    /** How many additional units should be displayed next to the most suitable largest unit for time-based statistics.
     <br>Default: 1 for plain text, 0 for hover-text</br>*/
    public int getNumberOfExtraTimeUnits(boolean isUnitForHoverText) {
        String path = "number-of-extra-units";
        if (isUnitForHoverText) {
            path = path + "-for-hover-text";
        }
        int defaultValue = isUnitForHoverText ? 0 : 1;
        return config.getInt(path, defaultValue);
    }

    /** Returns the unit that should be used for time-based statistics.
     (this will return the largest unit that should be used).
     <br>Default: days for plain text, hours for hover-text</br>*/
    public String getTimeUnit(boolean isUnitForHoverText) {
        return getTimeUnit(isUnitForHoverText, false);
    }

    /** Returns the unit that should be used for time-based statistics. If the optional smallUnit flag is true,
     this will return the smallest unit (and otherwise the largest).
     <br>Default: hours for plain text, seconds for hover-text</br>*/
    public String getTimeUnit(boolean isUnitForHoverText, boolean smallUnit) {
        if (smallUnit) {
            return getUnitString(isUnitForHoverText, "hours", "seconds", "smallest-time-unit");
        }
        return getUnitString(isUnitForHoverText, "days", "hours", "biggest-time-unit");
    }

    /** Returns an integer between 0 and 100 that represents how much lighter a hoverColor should be.
     So 20 would mean 20% lighter.
     <br>Default: 20</br>*/
    public int getHoverTextAmountLighter() {
        return config.getInt("hover-text-amount-lighter", 20);
    }

    /** Returns a String that represents either a Chat Color, hex color code, or a Style. Default values are:
     * <br>Style: "italic"</br>
     * <br>Color: "gray"</br>*/
    public String getSharedByTextDecoration(boolean getStyleSetting) {
        String def = getStyleSetting ? "italic" : "gray";
        return getDecorationString(null, getStyleSetting, def, "shared-by");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or a Style. Default values are:
     * <br>Style: "none"</br>
     * <br>Color: "#845EC2"</br>*/
    public String getSharerNameDecoration(boolean getStyleSetting) {
       return getDecorationString(null, getStyleSetting, "#845EC2", "player-name");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or a Style. Default values are:
     <br>Style: "none"</br>
     <br>Color Top: "green"</br>
     <br>Color Individual/Server: "gold"</br>*/
    public String getPlayerNameDecoration(Target selection, boolean getStyleSetting) {
        String def;
        if (selection == Target.TOP) {
            def = "green";
        }
        else {
            def = "gold";
        }
        return getDecorationString(selection, getStyleSetting, def, "player-names");
    }

    /** Returns true if playerNames Style is "bold" for a top-stat, false if it is not.
     <br>Default: false</br>*/
    public boolean playerNameIsBold() {
        ConfigurationSection style = getRelevantSection(Target.TOP);

        if (style != null) {
            String styleString = style.getString("player-names");
            return styleString != null && styleString.equalsIgnoreCase("bold");
        }
        return false;
    }

    /** Returns a String that represents either a Chat Color, hex color code, or a Style. Default values are:
     <br>Style: "none"</br>
     <br>Color: "yellow"</br>*/
    public String getStatNameDecoration(Target selection, boolean getStyleSetting) {
        return getDecorationString(selection, getStyleSetting, "yellow", "stat-names");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or a Style. Default values are:
     <br>Style: "none"</br>
     <br>Color: "#FFD52B"</br>*/
    public String getSubStatNameDecoration(Target selection, boolean getStyleSetting) {
        return getDecorationString(selection, getStyleSetting, "#FFD52B", "sub-stat-names");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <br>Style: "none"</br>
     <br>Color Top: "#55AAFF"</br>
     <br>Color Individual/Server: "#ADE7FF"</br> */
    public String getStatNumberDecoration(Target selection, boolean getStyleSetting) {
        String def;
        if (selection == Target.TOP) {
            def = "#55AAFF";
        }
        else {
            def = "#ADE7FF";
        }
        return getDecorationString(selection, getStyleSetting, def,"stat-numbers");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <br>Style: "none"</br>
     <br>Color Top: "yellow"</br>
     <br>Color Server: "gold"</br>*/
    public String getTitleDecoration(Target selection, boolean getStyleSetting) {
        String def;
        if (selection == Target.TOP) {
            def = "yellow";
        }
        else {
            def = "gold";
        }
        return getDecorationString(selection, getStyleSetting, def, "title");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <br>Style: "none"</br>
     <br>Color: "gold"</br>*/
    public String getTitleNumberDecoration(boolean getStyleSetting) {
        return getDecorationString(Target.TOP, getStyleSetting, "gold", "title-number");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <br>Style: "none"</br>
     <br>Color: "#FFB80E"</br>*/
    public String getServerNameDecoration(boolean getStyleSetting) {
        return getDecorationString(Target.SERVER, getStyleSetting, "#FFB80E", "server-name");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <br>Style: "none"</br>
     <br>Color: "gold"</br>*/
    public String getRankNumberDecoration(boolean getStyleSetting) {
        return getDecorationString(Target.TOP, getStyleSetting, "gold", "rank-numbers");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <br>Style: "none"</br>
     <br>Color: "dark_gray"</br> */
    public String getDotsDecoration(boolean getStyleSetting) {
        return getDecorationString(Target.TOP, getStyleSetting, "dark_gray", "dots");
    }

    /** Returns a String representing the {@link Unit} that should be used for a certain {@link Unit.Type}.
     If no String can be retrieved from the config, the supplied defaultValue will be returned.
     If the defaultValue is different for hoverText, an optional String defaultHoverValue can be supplied.
     @param isHoverText if true, the unit for hovering text is returned, otherwise the unit for plain text
     @param defaultValue the default unit for plain text
     @param defaultHoverValue the default unit for hovering text
     @param pathName the config path to retrieve the value from*/
    private String getUnitString(boolean isHoverText, String defaultValue, String defaultHoverValue, String pathName) {
        String path = isHoverText ? pathName + "-for-hover-text" : pathName;
        String def = defaultValue;
        if (isHoverText && defaultHoverValue != null) {
            def = defaultHoverValue;
        }
        return config.getString(path, def);
    }

    /** Returns the config value for a color or style option in string-format, the supplied default value,
     or null if no configSection was found.
     @param selection the Target this decoration is meant for (Player, Server or Top)
     @param getStyleSetting if true, the result will be a style String, otherwise a color String
     @param defaultColor the default color to return if the config value cannot be found (for style, the default is always "none")
     @param pathName the config path to retrieve the value from*/
    private @Nullable String getDecorationString(Target selection, boolean getStyleSetting, String defaultColor, String pathName){
        String path = getStyleSetting ? pathName + "-style" : pathName;
        String defaultValue = getStyleSetting ? "none" : defaultColor;

        ConfigurationSection section = getRelevantSection(selection);
        return section != null ? section.getString(path, defaultValue) : null;
    }

    /** Returns the config section that contains the relevant color or style option. */
    private @Nullable ConfigurationSection getRelevantSection(Target selection) {
        if (selection == null) {  //rather than rework the whole Target enum, I have added shared-stats as the null-option for now
            return config.getConfigurationSection("shared-stats");
        }
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