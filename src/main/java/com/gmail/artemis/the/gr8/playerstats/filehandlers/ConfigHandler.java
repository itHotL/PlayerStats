package com.gmail.artemis.the.gr8.playerstats.filehandlers;

import com.gmail.artemis.the.gr8.playerstats.Main;
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

    //returns the config setting for use-dots, or the default value "true" if no value can be retrieved
    public boolean useDots() {
        try {
            return config.getBoolean("use-dots");
        }
        catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    //returns a HashMap with the available (Bukkit) style choices, null if no style was chosen, and ChatColor.RESET if the entry was not valid
    public HashMap<String, ChatColor> getStyleOptions() {
        HashMap<String, ChatColor> styling = new HashMap<>();

        ConfigurationSection individual = config.getConfigurationSection("individual-statistics-style");
        if (individual != null) {
            plugin.getLogger().info("individual-statistics-style: " + individual.getKeys(false));
            individual.getKeys(false).forEach(path -> {
               styling.put(path, getStyleOption(individual, path));
            });
        }

        ConfigurationSection top = config.getConfigurationSection("top-list-style");
        if (top != null) {
            plugin.getLogger().info("top-list-style: " + top.getKeys(false));
            top.getKeys(false).forEach(path -> {
                styling.put(path + "-top", getStyleOption(top, path));
            });
        }
        return styling;
    }

    private ChatColor getStyleOption(ConfigurationSection section, String path) {
        ChatColor style;
        try {
            String entry = section.getString(path);
            if (entry == null || entry.equalsIgnoreCase("none")) {
                style = null;
            }
            else {
                style = getChatColor(section, path);
            }
        }
        catch (NullPointerException ignored) {
            style = null;
        }
        catch (IllegalArgumentException e) {
           plugin.getLogger().warning(e.toString());
           style = null;
        }
        return style;
    }

    //returns a HashMap with all the available (Bukkit) color choices (entries contain ChatColor.RESET if no colors were found)
    public HashMap<String, ChatColor> getChatColors() {
        HashMap<String, ChatColor> chatColors = new HashMap<>();

        ConfigurationSection individual = config.getConfigurationSection("individual-statistics");
        if (individual != null) {
            plugin.getLogger().info("individual-statistics: " + individual.getKeys(false));
            individual.getKeys(false).forEach(path -> {
                chatColors.put(path, getChatColor(individual, path));
            });
        }

        ConfigurationSection top = config.getConfigurationSection("top-list");
        if (top != null) {
            plugin.getLogger().info("top-list: " + top.getKeys(false));
            top.getKeys(false).forEach(path -> {
                chatColors.put(path + "-top", getChatColor(top, path));
            });
        }
        return chatColors;
    }

    //turns the requested entry from the provided configuration section into a (Bukkit) ChatColor
    //returns null if section does not exist, and if there is no (or a bad) entry
    private ChatColor getChatColor(ConfigurationSection section, String path) {
        ChatColor color;
        try {
            String colorText = section.getString(path);
            if (colorText != null) {
                color = ChatColor.valueOf(colorText.toUpperCase().replace(" ", "_"));
            }
            else {
                color = null;
            }
        }
        catch (IllegalArgumentException | NullPointerException exception) {
            plugin.getLogger().warning(exception.toString());
            color = null;
        }
        return color;
    }

    //returns a HashMap with all the available (Spigot) color choices (entries contain ChatColor.RESET if no colors were found)
    public HashMap<String, net.md_5.bungee.api.ChatColor> getHexChatColors() {
        HashMap<String, net.md_5.bungee.api.ChatColor> chatColors = new HashMap<>();

        ConfigurationSection individual = config.getConfigurationSection("individual-statistics");
        if (individual != null) {
            plugin.getLogger().info("individual-statistics: " + individual.getKeys(false));
            individual.getKeys(false).forEach(path -> {
                chatColors.put(path, getHexChatColor(individual, path));
            });
        }

        ConfigurationSection top = config.getConfigurationSection("top-list");
        if (top != null) {
            plugin.getLogger().info("top-list: " + top.getKeys(false));
            top.getKeys(false).forEach(path -> {
                chatColors.put(path + "-top", getHexChatColor(top, path));
            });
        }
        return chatColors;
    }

    //turns the requested entry from the provided configuration section into a (Spigot) ChatColor
    //returns null if section does not exist, or if there is no (or a bad) entry
    private net.md_5.bungee.api.ChatColor getHexChatColor(ConfigurationSection section, String path) {
        net.md_5.bungee.api.ChatColor color;
        try {
            String colorText = section.getString(path);
            if (colorText != null) {
                color = net.md_5.bungee.api.ChatColor.of(colorText);
            }
            else {
                color = null;
            }
        }
        catch (IllegalArgumentException | NullPointerException exception) {
            plugin.getLogger().warning(exception.toString());
            color = null;
        }
        return color;
    }

    //create a config file if none exists yet (from the config.yml in the plugin's resources)
    private void saveDefaultConfig() {
        config = plugin.getConfig();
        plugin.saveDefaultConfig();
        configFile = new File(plugin.getDataFolder(), "config.yml");
    }
}
