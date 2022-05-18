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
        ConfigurationSection ranked = config.getConfigurationSection("top-list");
        try {
            return ranked == null || ranked.getBoolean("use-dots");
        }
        catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public HashMap<String, ChatColor> getStylingOptions() {
        HashMap<String, ChatColor> styling = new HashMap<>();


    }

    //returns a HashMap with all the available (Bukkit) color choices (entries contain ChatColor.RESET if no colors were found)
    public HashMap<String, ChatColor> getChatColors() {
        HashMap<String, ChatColor> chatColors = new HashMap<>();

        ConfigurationSection individual = config.getConfigurationSection("individual-statistics");
        chatColors.put("player-names", getChatColor(individual, "player-names"));
        chatColors.put("stat-names", getChatColor(individual, "stat-names"));
        chatColors.put("sub-stat-names", getChatColor(individual, "sub-stat-names"));
        chatColors.put("stat-numbers", getChatColor(individual, "stat-numbers"));

        ConfigurationSection top = config.getConfigurationSection("top-list");
        chatColors.put("player-names-top", getChatColor(top, "player-names"));
        chatColors.put("stat-names-top", getChatColor(top, "stat-names"));
        chatColors.put("sub-stat-names-top", getChatColor(top, "sub-stat-names"));
        chatColors.put("stat-numbers-top", getChatColor(top, "stat-numbers"));
        chatColors.put("list-numbers-top", getChatColor(top, "list-numbers"));
        chatColors.put("dots-top", getChatColor(top, "dots"));
        return chatColors;
    }

    //returns a HashMap with all the available (Spigot) color choices (entries contain ChatColor.RESET if no colors were found)
    public HashMap<String, net.md_5.bungee.api.ChatColor> getHexChatColors() {
        HashMap<String, net.md_5.bungee.api.ChatColor> chatColors = new HashMap<>();
        fillSpigotHashMap(chatColors, config.getConfigurationSection("individual-statistics"), false);
        /*
        ConfigurationSection individual = config.getConfigurationSection("individual-statistics");
        chatColors.put("player-names", getHexChatColor(individual, "player-names"));
        chatColors.put("stat-names", getHexChatColor(individual, "stat-names"));
        chatColors.put("sub-stat-names", getHexChatColor(individual, "sub-stat-names"));
        chatColors.put("stat-numbers", getHexChatColor(individual, "stat-numbers"));

        ConfigurationSection top = config.getConfigurationSection("top-list");
        chatColors.put("player-names-top", getHexChatColor(top, "player-names"));
        chatColors.put("stat-names-top", getHexChatColor(top, "stat-names"));
        chatColors.put("sub-stat-names-top", getHexChatColor(top, "sub-stat-names"));
        chatColors.put("stat-numbers-top", getHexChatColor(top, "stat-numbers"));
        chatColors.put("list-numbers-top", getHexChatColor(top, "list-numbers"));
        chatColors.put("dots-top", getHexChatColor(top, "dots"));
         */
        return chatColors;
    }

    //fill the provided HashMap with either Bukkit or Spigot ChatColors for the given ConfigurationSection (individual or top)
    private void fillSpigotHashMap(HashMap<String, net.md_5.bungee.api.ChatColor> hashMap, ConfigurationSection section, boolean isTopSection) {
        if (section != null) {
            section.getKeys(false).forEach(path -> {
                String hashMapKey = isTopSection ? path + "-top" : path;
                hashMap.put(hashMapKey, getHexChatColor(section, path));
            });
        }
    }

    //fill the provided HashMap with either Bukkit or Spigot ChatColors for the given ConfigurationSection (individual or top)
    private void fillBukkitHashMap(HashMap<String, ChatColor> hashMap, ConfigurationSection section, boolean isTopSection) {
        if (section != null) {
            section.getKeys(false).forEach(path -> {
                String hashMapKey = isTopSection ? path + "-top" : path;
                hashMap.put(hashMapKey, getChatColor(section, path));
            });
        }
    }

    //turns the requested entry from the provided configuration section into a (Bukkit) ChatColor
    //returns null if section does not exist, and ChatColor.RESET if there is no entry
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

    //turns the requested entry from the provided configuration section into a (Spigot) ChatColor
    //returns null if section does not exist, and ChatColor.RESET if there is no entry
    private net.md_5.bungee.api.ChatColor getHexChatColor(ConfigurationSection section, String path) {
        net.md_5.bungee.api.ChatColor color;
        try {
            String colorText = section.getString(path);
            if (colorText != null) {
                color = net.md_5.bungee.api.ChatColor.of(colorText);
            }
            else {
                color = net.md_5.bungee.api.ChatColor.RESET;
            }
        }
        catch (IllegalArgumentException | NullPointerException exception) {
            plugin.getLogger().warning(exception.toString());
            color = net.md_5.bungee.api.ChatColor.RESET;
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
