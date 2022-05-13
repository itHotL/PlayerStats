package com.gmail.artemis.the.gr8.playerstats;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class ConfigHandler {

    private File configFile;
    private FileConfiguration config;
    private final Main plugin;

    public ConfigHandler(Main p) {
        plugin = p;
        saveDefaultConfig();
    }

    //returns a HashMap with all the available color choices, or a ChatColor.RESET if no colors were found
    public HashMap<String, ChatColor> getChatColors() {
        HashMap<String, ChatColor> chatColors = new HashMap<>();

        ConfigurationSection individual = config.getConfigurationSection("individual-statistics");
        chatColors.put("player-names", getChatColor(individual, "player-names"));
        chatColors.put("stat-names", getChatColor(individual, "stat-names"));
        chatColors.put("sub-stat-names", getChatColor(individual, "sub-stat-names"));
        chatColors.put("stat-numbers", getChatColor(individual, "stat-numbers"));

        ConfigurationSection ranked = config.getConfigurationSection("ranked-list");
        chatColors.put("player-names-ranked", getChatColor(ranked, "player-names"));
        chatColors.put("list-title", getChatColor(ranked, "list-title"));
        chatColors.put("sub-stat-names-ranked", getChatColor(ranked, "sub-stat-names"));
        chatColors.put("stat-numbers-ranked", getChatColor(ranked, "stat-numbers"));
        chatColors.put("list-numbers", getChatColor(ranked, "list-numbers"));
        chatColors.put("underscores", getChatColor(ranked, "underscores"));
        return chatColors;
    }

    //returns the requested entry from the provided configuration section, null if section does not exist, and ChatColor.RESET if there is no entry
    private ChatColor getChatColor(ConfigurationSection section, String path) {
        ChatColor color;
        try {
            String colorText = section.getString(path);
            if (colorText != null) {
                color = ChatColor.valueOf(colorText.toUpperCase().replace(" ", "_"));
            }
            else {
                color = ChatColor.RESET;
            }
        }
        catch (IllegalArgumentException | NullPointerException exception) {
            plugin.getLogger().warning(exception.toString());
            color = ChatColor.RESET;
        }
        return color;
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
            e.printStackTrace();
            return false;
        }
    }

    //create a config file if none exists yet (from the config.yml in the plugin's resources)
    private void saveDefaultConfig() {
        config = plugin.getConfig();
        plugin.saveDefaultConfig();
        configFile = new File(plugin.getDataFolder(), "config.yml");
    }
}
