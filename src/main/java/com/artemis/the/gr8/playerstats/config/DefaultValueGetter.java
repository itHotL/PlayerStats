package com.artemis.the.gr8.playerstats.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public final class DefaultValueGetter {

    private final FileConfiguration config;
    private final Map<String, Object> defaultValuesToAdjust;

    public DefaultValueGetter(FileConfiguration configuration) {
        config = configuration;
        defaultValuesToAdjust = new HashMap<>();
    }

    public Map<String, Object> getValuesToAdjust() {
        checkTopListDefault();
        checkDefaultColors();
        return defaultValuesToAdjust;
    }

    private void checkTopListDefault() {
        String oldTitle = config.getString("top-list-title");
        if (oldTitle != null && oldTitle.equalsIgnoreCase("Top [x]")) {
            defaultValuesToAdjust.put("top-list-title", "Top");
        }
    }

    /**
     * Adjusts some of the default colors to migrate from versions 2
     * or 3 to version 4 and above.
     */
    private void checkDefaultColors() {
        addValueIfNeeded("top-list.title", "yellow", "#FFD52B");
        addValueIfNeeded("top-list.title", "#FFEA40", "#FFD52B");
        addValueIfNeeded("top-list.stat-names", "yellow", "#FFD52B");
        addValueIfNeeded("top-list.stat-names", "#FFEA40", "#FFD52B");
        addValueIfNeeded("top-list.sub-stat-names", "#FFD52B", "yellow");

        addValueIfNeeded("individual-statistics.stat-names", "yellow", "#FFD52B");
        addValueIfNeeded("individual-statistics.sub-stat-names", "#FFD52B", "yellow");
        addValueIfNeeded("total-server.title", "gold", "#55AAFF");
        addValueIfNeeded("total-server.server-name", "gold", "#55AAFF");
        addValueIfNeeded("total-server.stat-names", "yellow", "#FFD52B");
        addValueIfNeeded("total-server.sub-stat-names", "#FFD52B", "yellow");
    }

    private void addValueIfNeeded(String path, String oldValue, String newValue) {
        String configString = config.getString(path);
        if (configString != null && configString.equalsIgnoreCase(oldValue)) {
            defaultValuesToAdjust.put(path, newValue);
        }
    }
}