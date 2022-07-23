package com.gmail.artemis.the.gr8.playerstats.config;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import com.gmail.artemis.the.gr8.playerstats.enums.Unit;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class ConfigHandler {

    private static Main plugin;
    private static int configVersion;

    private File configFile;
    private FileConfiguration config;

    public ConfigHandler(Main p) {
        plugin = p;
        configVersion = 6;

        saveDefaultConfig();
        config = YamlConfiguration.loadConfiguration(configFile);
        checkConfigVersion();

        MyLogger.setDebugLevel(getDebugLevel());
    }

    /** Checks the number that "config-version" returns to see if the config needs updating, and if so, send it to the {@link ConfigUpdateHandler}.
     <p>PlayerStats 1.1: "config-version" doesn't exist.</p>
     <p>PlayerStats 1.2: "config-version" is 2.</p>
     <p>PlayerStats 1.3: "config-version" is 3. </P>
     <p>PlayerStats 1.4: "config-version" is 4.</p>*/
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
     <p>1 = low (only show unexpected errors)</p>
     <p>2 = medium (detail all encountered exceptions, log main tasks and show time taken)</p>
     <p>3 = high (log all tasks and time taken)</p>
     <p>Default: 1</p>*/
    public int getDebugLevel() {
        return config.getInt("debug-level", 1);
    }

    /** Returns true if command-senders should be limited to one stat-request at a time.
     <p>Default: true</p>*/
    public boolean limitStatRequests() {
        return config.getBoolean("only-allow-one-lookup-at-a-time-per-player", true);
    }

    /** Returns true if stat-sharing is allowed.
     <p>Default: true</p>*/
    public boolean allowStatSharing() {
        return config.getBoolean("enable-stat-sharing", true);
    }

    /** Returns the number of minutes a player has to wait before being able to
     share another stat-result.
     <p>Default: 0</p>*/
    public int getStatShareWaitingTime() {
        return config.getInt("waiting-time-before-sharing-again", 0);
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
    public int getLastPlayedLimit() {
        return config.getInt("number-of-days-since-last-joined", 0);
    }

    /** Whether to use TranslatableComponents wherever possible.
     Currently supported: statistic, block, item and entity names.
     <p>Default: true</p>*/
    public boolean useTranslatableComponents() {
        return config.getBoolean("translate-to-client-language", true);
    }

    /** Whether to use HoverComponents for additional information.
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
    public boolean useRainbowMode() {
        return config.getBoolean("rainbow-mode", false);
    }

    /** Whether to use enters before the statistic output in chat.
     Enters create some separation between the previous things that have been said in chat and the stat-result.
     <p>Default: true for non-shared top statistics, false for everything else</p>*/
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
        MyLogger.logMsg("Config settings for use-enters could not be retrieved! " +
                "Please check your file if you want to use custom settings. " +
                "Using default values...", true);
        return def;
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

    /** Returns the unit that should be used for distance-related statistics.
     <p>Default: Blocks for plain text, km for hover-text</p>*/
    public String getDistanceUnit(boolean isHoverText) {
        return getUnitString(isHoverText, "blocks", "km", "distance-unit");
    }

    /** Returns the unit that should be used for damage-based statistics.
     <p>Default: Hearts for plain text, HP for hover-text.</p>*/
    public String getDamageUnit(boolean isHoverText) {
        return getUnitString(isHoverText, "hearts", "hp", "damage-unit");
    }

    /** Whether PlayerStats should automatically detect the most suitable unit to use for time-based statistics.
     <p>Default: true</p>*/
    public boolean autoDetectTimeUnit(boolean isHoverText) {
        String path = "auto-detect-biggest-time-unit";
        if (isHoverText) {
            path = path + "-for-hover-text";
        }
        boolean defaultValue = !isHoverText;
        return config.getBoolean(path, defaultValue);
    }

    /** How many additional units should be displayed next to the most suitable largest unit for time-based statistics.
     <p>Default: 1 for plain text, 0 for hover-text</p>*/
    public int getNumberOfExtraTimeUnits(boolean isHoverText) {
        String path = "number-of-extra-units";
        if (isHoverText) {
            path = path + "-for-hover-text";
        }
        int defaultValue = isHoverText ? 0 : 1;
        return config.getInt(path, defaultValue);
    }

    /** Returns the unit that should be used for time-based statistics.
     (this will return the largest unit that should be used).
     <p>Default: days for plain text, hours for hover-text</p>*/
    public String getTimeUnit(boolean isHoverText) {
        return getTimeUnit(isHoverText, false);
    }

    /** Returns the unit that should be used for time-based statistics. If the optional smallUnit flag is true,
     this will return the smallest unit (and otherwise the largest).
     <p>Default: hours for plain text, seconds for hover-text</p>*/
    public String getTimeUnit(boolean isHoverText, boolean smallUnit) {
        if (smallUnit) {
            return getUnitString(isHoverText, "hours", "seconds", "smallest-time-unit");
        }
        return getUnitString(isHoverText, "days", "hours", "biggest-time-unit");
    }

    /** Returns an integer between 0 and 100 that represents how much lighter a hoverColor should be.
     So 20 would mean 20% lighter.
     <p>Default: 20</p>*/
    public int getHoverTextAmountLighter() {
        return config.getInt("hover-text-amount-lighter", 20);
    }

    /** Returns a String that represents either a Chat Color, hex color code, or a Style. Default values are:
     * <p>Style: "italic"</p>
     * <p>Color: "gray"</p>*/
    public String getSharedByTextDecoration(boolean getStyleSetting) {
        String def = getStyleSetting ? "italic" : "gray";
        return getDecorationString(null, getStyleSetting, def, "shared-by");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or a Style. Default values are:
     * <p>Style: "none"</p>
     * <p>Color: "#845EC2"</p>*/
    public String getSharerNameDecoration(boolean getStyleSetting) {
       return getDecorationString(null, getStyleSetting, "#845EC2", "player-name");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or a Style. Default values are:
     <p>Style: "none"</p>
     <p>Color Top: "green"</p>
     <p>Color Individual/Server: "gold"</p>*/
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
    public String getStatNameDecoration(Target selection, boolean getStyleSetting) {
        return getDecorationString(selection, getStyleSetting, "yellow", "stat-names");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or a Style. Default values are:
     <p>Style: "none"</p>
     <p>Color: "#FFD52B"</p>*/
    public String getSubStatNameDecoration(Target selection, boolean getStyleSetting) {
        return getDecorationString(selection, getStyleSetting, "#FFD52B", "sub-stat-names");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <p>Style: "none"</p>
     <p>Color Top: "#55AAFF"</p>
     <p>Color Individual/Server: "#ADE7FF"</p> */
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
     <p>Style: "none"</p>
     <p>Color Top: "yellow"</p>
     <p>Color Server: "gold"</p>*/
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
     <p>Style: "none"</p>
     <p>Color: "gold"</p>*/
    public String getTitleNumberDecoration(boolean getStyleSetting) {
        return getDecorationString(Target.TOP, getStyleSetting, "gold", "title-number");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <p>Style: "none"</p>
     <p>Color: "#FFB80E"</p>*/
    public String getServerNameDecoration(boolean getStyleSetting) {
        return getDecorationString(Target.SERVER, getStyleSetting, "#FFB80E", "server-name");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <p>Style: "none"</p>
     <p>Color: "gold"</p>*/
    public String getRankNumberDecoration(boolean getStyleSetting) {
        return getDecorationString(Target.TOP, getStyleSetting, "gold", "rank-numbers");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <p>Style: "none"</p>
     <p>Color: "dark_gray"</p> */
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