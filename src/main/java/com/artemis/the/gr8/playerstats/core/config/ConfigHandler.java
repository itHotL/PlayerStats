package com.artemis.the.gr8.playerstats.core.config;

import com.artemis.the.gr8.playerstats.api.enums.Target;
import com.artemis.the.gr8.playerstats.api.enums.Unit;
import com.artemis.the.gr8.playerstats.core.utils.YamlFileHandler;
import com.artemis.the.gr8.playerstats.core.utils.MyLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/** Handles all PlayerStats' config-settings. */
public final class ConfigHandler extends YamlFileHandler {

    private static volatile ConfigHandler instance;
    private final int configVersion;
    private FileConfiguration config;

    private ConfigHandler() {
        super("config.yml");
        config = super.getFileConfiguration();

        configVersion = 8;
        checkAndUpdateConfigVersion();
        MyLogger.setDebugLevel(getDebugLevel());
    }

    public static ConfigHandler getInstance() {
        ConfigHandler localVar = instance;
        if (localVar != null) {
            return localVar;
        }

        synchronized (ConfigHandler.class) {
            if (instance == null) {
                instance = new ConfigHandler();
            }
            return instance;
        }
    }

    @Override
    public void reload() {
        super.reload();
        config = super.getFileConfiguration();
        MyLogger.setDebugLevel(getDebugLevel());
    }

    /**
     * Checks the number that "config-version" returns to see if the
     * config needs updating, and if so, updates it.
     * <br>
     * <br>PlayerStats 1.1: "config-version" doesn't exist.
     * <br>PlayerStats 1.2: "config-version" is 2.
     * <br>PlayerStats 1.3: "config-version" is 3.
     * <br>PlayerStats 1.4: "config-version" is 4.
     * <br>PlayerStats 1.5: "config-version" is 5.
     * <br>PlayerStats 1.6 and up: "config-version" is 6.
     */
    private void checkAndUpdateConfigVersion() {
        if (!config.contains("config-version") || config.getInt("config-version") != configVersion) {
            DefaultValueGetter defaultValueGetter = new DefaultValueGetter(config);
            Map<String, Object> defaultValues = defaultValueGetter.getValuesToAdjust();
            defaultValues.put("config-version", configVersion);

            super.addValues(defaultValues);
            reload();

            MyLogger.logLowLevelMsg("Your config has been updated to version " + configVersion +
                    ", but all of your custom settings should still be there!");
        }
    }

    /** Returns the desired debugging level.
     *
     * <br> 1 = low (only show unexpected errors)
     * <br> 2 = medium (detail all encountered exceptions, log main tasks and show time taken)
     * <br> 3 = high (log all tasks and time taken)
     *
     * @return the DebugLevel (default: 1)
     */
    public int getDebugLevel() {
        return config.getInt("debug-level", 1);
    }

    /**
     * Whether command-senders should be limited to one stat-request at a time.
     * @return the config setting (default: true)
     */
    public boolean limitStatRequests() {
        return config.getBoolean("only-allow-one-lookup-at-a-time-per-player", true);
    }

    /**
     * Whether stat-sharing is allowed.
     * @return the config setting (default: true)
     */
    public boolean allowStatSharing() {
        return config.getBoolean("enable-stat-sharing", true);
    }

    /**
     * The number of minutes a player has to wait before being able to
     * share another stat-result.
     * @return the number (default: 0)
     */
    public int getStatShareWaitingTime() {
        return config.getInt("waiting-time-before-sharing-again", 0);
    }

    /**
     * Whether to limit stat-calculations to whitelisted players only.
     * @return the config setting (default: true)
     */
    public boolean whitelistOnly() {
        return config.getBoolean("include-whitelist-only", false);
    }

    /**
     * Whether to exclude banned players from stat-calculations.
     * @return the config setting for exclude-banned-players (default: false)
     */
    public boolean excludeBanned() {
        return config.getBoolean("exclude-banned-players", false);
    }

    /**
     * The number of maximum days since a player has last been online.
     * @return the number (default: 0 - which signals not to use this limit)
     */
    public int getLastPlayedLimit() {
        return config.getInt("number-of-days-since-last-joined", 0);
    }

    /**
     * Whether to allow the /stat player command for excluded players.
     * @return the config setting (default: true)
     */
    public boolean allowPlayerLookupsForExcludedPlayers() {
        return config.getBoolean("allow-player-lookups-for-excluded-players", true);
    }

    /**
     * Whether to use TranslatableComponents wherever possible.
     *
     * @return the config setting (default: true)
     * @implNote Currently supported: statistic, block, item and entity names.
     */
    public boolean useTranslatableComponents() {
        return config.getBoolean("translate-to-client-language", true);
    }

    /**
     * Whether to use HoverComponents for additional information
     * @return the config setting (default: true)
     */
    public boolean useHoverText() {
        return config.getBoolean("enable-hover-text", true);
    }

    /**
     * Whether to use festive formatting, such as pride colors
     * @return the config setting (default: true)
      */
    public boolean useFestiveFormatting() {
        return config.getBoolean("enable-festive-formatting", true);
    }

    /**
     * Whether to use rainbow colors for the [PlayerStats] prefix rather than the
     * default gold/purple
     * @return the config setting (default: false)
     */
    public boolean useRainbowMode() {
        return config.getBoolean("rainbow-mode", false);
    }

    /**
     * Whether to use enters before the statistic output in chat
     *
     * @param selection the Target (Player, Server or Top)
     * @return the config setting (default: true for non-shared top
     * statistics, false for everything else)
     */
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

    /**
     * Whether dots should be used to align the numbers in a top-stat-result.
     * @return the config setting (default: true)
     */
    public boolean useDots() {
        return config.getBoolean("use-dots", true);
    }

    /**
     * The maximum size for the top-stat-list.
     * @return the config setting (default: 10)
     */
    public int getTopListMaxSize() {
        return config.getInt("top-list-max-size", 10);
    }

    /**
     * The title that a top-statistic should start with.
     * @return a String that represents the title for a top statistic
     * (default: "Top")
     */
    public String getTopStatsTitle() {
        return config.getString("top-list-title", "Top");
    }

    /**
     * The title that a server statistic should start with.
     * @return the title (default: "Total on")
     */
    public String getServerTitle() {
        return config.getString("total-server-stat-title", "Total on");
    }

    /**
     * The specified server name for a server stat title.
     * @return the title (default: "this server")
     */
    public String getServerName() {
        return config.getString("your-server-name", "this server");
    }

    /**
     * The unit that should be used for distance-related statistics.
     *
     * @param isUnitForHoverText whether the number formatted with this
     * Unit is inside a HoverComponent
     * @return the Unit (default: Blocks for plain text, km for hover-text)
     */
    public String getDistanceUnit(boolean isUnitForHoverText) {
        return getUnitString(isUnitForHoverText, "blocks", "km", "distance-unit");
    }

    /**
     * The unit that should be used for damage-based statistics.
     *
     * @param isUnitForHoverText whether the number formatted with this
     * Unit is inside a HoverComponent
     * @return the Unit (default: Hearts for plain text, HP for hover-text)
     */
    public String getDamageUnit(boolean isUnitForHoverText) {
        return getUnitString(isUnitForHoverText, "hearts", "hp", "damage-unit");
    }

    /**
     * Whether PlayerStats should automatically detect the most suitable
     * unit to use for time-based statistics
     *
     * @param isUnitForHoverText whether the number formatted with this
     * Unit is inside a HoverComponent
     * @return the config setting (default: true)
     */
    public boolean autoDetectTimeUnit(boolean isUnitForHoverText) {
        String path = "auto-detect-biggest-time-unit";
        if (isUnitForHoverText) {
            path = path + "-for-hover-text";
        }
        boolean defaultValue = !isUnitForHoverText;
        return config.getBoolean(path, defaultValue);
    }

    /**
     * How many additional units should be displayed next to the most
     * suitable largest unit for time-based statistics
     *
     * @param isUnitForHoverText whether the number formatted with this
     * Unit is inside a HoverComponent
     * @return the config setting (default: 1 for plain text,
     * 0 for hover-text)
     */
    public int getNumberOfExtraTimeUnits(boolean isUnitForHoverText) {
        String path = "number-of-extra-units";
        if (isUnitForHoverText) {
            path = path + "-for-hover-text";
        }
        int defaultValue = isUnitForHoverText ? 0 : 1;
        return config.getInt(path, defaultValue);
    }

    /**
     * The largest unit that should be used for time-based statistics.
     *
     * @param isUnitForHoverText whether the number formatted with this
     * Unit is inside a HoverComponent
     * @return a String representation of the largest time-unit
     * (default: days for plain text, hours for hover-text)
     */
    public String getTimeUnit(boolean isUnitForHoverText) {
        return getTimeUnit(isUnitForHoverText, false);
    }

    /**
     * The unit that should be used for time-based statistics.
     * If the optional smallUnit flag is true, this will return
     * the smallest unit (and otherwise the biggest).
     *
     * @param isUnitForHoverText whether the number formatted with this
     * Unit is inside a HoverComponent
     * @param smallUnit if this is true, get the minimum time-unit
     * @return the Unit (default: hours for plain text, seconds for hover-text)
     */
    public String getTimeUnit(boolean isUnitForHoverText, boolean smallUnit) {
        if (smallUnit) {
            return getUnitString(isUnitForHoverText, "hours", "seconds", "smallest-time-unit");
        }
        return getUnitString(isUnitForHoverText, "days", "hours", "biggest-time-unit");
    }

    /**
     * Returns an integer between 0 and 100 that represents how much lighter
     * a hoverColor should be.
     * @return an {@code int} that represents a percentage (default: 20)
     */
    public int getHoverTextAmountLighter() {
        return config.getInt("hover-text-amount-lighter", 20);
    }

    /**
     * Gets a String that represents either a Chat Color, hex color code,
     * or a Style.
     *
     * @param getStyleSetting if true, returns a Style instead of a Color
     * @return the config setting. Default:
     * <br>Style: "italic"
     * <br>Color: "gray"
     */
    public String getSharedByTextDecoration(boolean getStyleSetting) {
        String def = getStyleSetting ? "italic" : "gray";
        return getDecorationString(null, getStyleSetting, def, "shared-by");
    }

    /**
     * Gets a String that represents either a Chat Color, hex color code, or a Style.
     *
     * @param getStyleSetting if true, returns a Style instead of a Color
     * @return the config setting. Default:
     * <br>Style: "none"
     * <br>Color: "#845EC2"
     */
    public String getSharerNameDecoration(boolean getStyleSetting) {
       return getDecorationString(null, getStyleSetting, "#845EC2", "player-name");
    }

    /**
     * Gets a String that represents either a Chat Color, hex color code, or a Style.
     *
     * @param selection the Target (Player, Server or Top)
     * @param getStyleSetting if true, returns a Style instead of a Color
     * @return the config setting. Default:
     * <br>Style: "none"
     * <br>Color Top: "green"
     * <br>Color Individual/Server: "gold"
     */
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

    /**
     * Whether the playerNames Style is "bold" for a top-stat.
     * @return the config setting (default: false)
     */
    public boolean playerNameIsBold() {
        ConfigurationSection style = getRelevantSection(Target.TOP);

        if (style != null) {
            String styleString = style.getString("player-names");
            return styleString != null && styleString.equalsIgnoreCase("bold");
        }
        return false;
    }

    /**
     * Gets a String that represents either a Chat Color, hex color code,
     * or a Style.
     *
     * @param selection the Target (Player, Server or Top)
     * @param getStyleSetting if true, returns a Style instead of a Color
     * @return the config setting. Default:
     * <br>Style: "none"
     * <br>Color: "yellow"
     */
    public String getStatNameDecoration(Target selection, boolean getStyleSetting) {
        return getDecorationString(selection, getStyleSetting, "yellow", "stat-names");
    }

    /**
     * Gets a String that represents either a Chat Color, hex color code,
     * or a Style.
     *
     * @param selection the Target (Player, Server or Top)
     * @param getStyleSetting if true, returns a Style instead of a Color
     * @return the config setting. Default:
     * <br>Style: "none"
     * <br>Color: "#FFD52B"
     */
    public String getSubStatNameDecoration(Target selection, boolean getStyleSetting) {
        return getDecorationString(selection, getStyleSetting, "#FFD52B", "sub-stat-names");
    }

    /**
     * Gets a String that represents either a Chat Color, hex color code,
     * or Style.
     *
     * @param selection the Target (Player, Server or Top)
     * @param getStyleSetting if true, returns a Style instead of a Color
     * @return the config setting. Default:
     * <br>Style: "none"
     * <br>Color Top: "#55AAFF"
     * <br>Color Individual/Server: "#ADE7FF"
     */
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

    /**
     * Gets a String that represents either a Chat Color, hex color code,
     * or Style.
     *
     * @param selection the Target (Player, Server or Top)
     * @param getStyleSetting if true, returns a Style instead of a Color
     * @return the config setting. Default:
     * <br>Style: "none"
     * <br>Color Top: "yellow"
     * <br>Color Server: "gold"
     */
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

    /**
     * Gets a String that represents either a Chat Color, hex color code,
     * or Style.
     *
     * @param getStyleSetting if true, returns a Style instead of a Color
     * @return the config setting. Default:
     * <br>Style: "none"
     * <br>Color: "gold"
     */
    public String getTitleNumberDecoration(boolean getStyleSetting) {
        return getDecorationString(Target.TOP, getStyleSetting, "gold", "title-number");
    }

    /**
     * Gets a String that represents either a Chat Color, hex color code,
     * or Style.
     *
     * @param getStyleSetting if true, returns a Style instead of a Color
     * @return the config setting. Default:
     * <br>Style: "none"
     * <br>Color: "#FFB80E"
     */
    public String getServerNameDecoration(boolean getStyleSetting) {
        return getDecorationString(Target.SERVER, getStyleSetting, "#FFB80E", "server-name");
    }

    /**
     * Gets a String that represents either a Chat Color, hex color code,
     * or Style.
     *
     * @param getStyleSetting if true, returns a Style instead of a Color
     * @return the config setting. Default:
     * <br>Style: "none"
     * <br>Color: "gold"
     */
    public String getRankNumberDecoration(boolean getStyleSetting) {
        return getDecorationString(Target.TOP, getStyleSetting, "gold", "rank-numbers");
    }

    /**
     * Gets a String that represents either a Chat Color, hex color code,
     * or Style.
     *
     * @param getStyleSetting if true, returns a Style instead of a Color
     * @return the config setting. Default:
     * <br>Style: "none"
     * <br>Color: "dark_gray"
     */
    public String getDotsDecoration(boolean getStyleSetting) {
        return getDecorationString(Target.TOP, getStyleSetting, "dark_gray", "dots");
    }

    /**
     * Gets a String representing a {@link Unit}.
     *
     * @return a String representing the {@link Unit} that should be used for a
     * certain {@link Unit.Type}. If no String can be retrieved from the config,
     * the supplied defaultValue will be returned. If the defaultValue is different
     * for hoverText, an optional String defaultHoverValue can be supplied.
     * @param isHoverText if true, the unit for hovering text is returned,
     * otherwise the unit for plain text
     * @param defaultValue the default unit for plain text
     * @param defaultHoverValue the default unit for hovering text
     * @param pathName the config path to retrieve the value from
     */
    private String getUnitString(boolean isHoverText, String defaultValue, String defaultHoverValue, String pathName) {
        String path = isHoverText ? pathName + "-for-hover-text" : pathName;
        String def = defaultValue;
        if (isHoverText && defaultHoverValue != null) {
            def = defaultHoverValue;
        }
        return config.getString(path, def);
    }

    /**
     * @return the config value for a color or style option in string-format,
     * the supplied default value, or null if no configSection was found.
     * @param selection the Target this decoration is meant for (Player, Server or Top)
     * @param getStyleSetting if true, the result will be a style String,
     * otherwise a color String
     * @param defaultColor the default color to return if the config value cannot be found
     * (for style, the default is always "none")
     * @param pathName the config path to retrieve the value from
     */
    private @Nullable String getDecorationString(Target selection, boolean getStyleSetting, String defaultColor, String pathName){
        String path = getStyleSetting ? pathName + "-style" : pathName;
        String defaultValue = getStyleSetting ? "none" : defaultColor;

        ConfigurationSection section = getRelevantSection(selection);
        return section != null ? section.getString(path, defaultValue) : null;
    }

    /**
     * @return the config section that contains the relevant color or style option.
     */
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