package com.artemis.the.gr8.playerstats.utils;

import com.artemis.the.gr8.playerstats.Main;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class FileHandler {

    private final String fileName;
    private File file;
    private FileConfiguration fileConfiguration;

    public FileHandler(String fileName) {
        this.fileName = fileName;
        loadFile();
    }

    public void loadFile() {
        JavaPlugin plugin = Main.getPluginInstance();

        file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        if (!file.exists()) {
            loadFile();
        } else {
            fileConfiguration = YamlConfiguration.loadConfiguration(file);
            MyLogger.logLowLevelMsg(fileName + " reloaded!");
        }
    }

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    public void addValuesToFile(@NotNull Map<String, Object> keyValuePairs) {
        keyValuePairs.forEach(this::addValue);
        save();
        updateFile();
    }

    /**
     * @param key the Key under which the List will be stored
     *            (or expanded if it already exists)
     * @param value the value(s) to expand the List with
     */
    public void addValueToListInFile(@NotNull String key, @NotNull Object value) {
        List<?> currentValues = fileConfiguration.getList(key);

        List<Object> updatedValues;
        if (currentValues != null) {
            updatedValues = new ArrayList<>(currentValues);
        } else {
            updatedValues = new ArrayList<>();
        }
        updatedValues.add(value);

        addValue(key, updatedValues);
        save();
        updateFile();
    }

    private void addValue(String key, Object value) {
        fileConfiguration.set(key, value);
    }

    private void save() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add new key-value pairs to the config without losing comments,
     * using <a href="https://github.com/tchristofferson/Config-Updater">tchristofferson's Config-Updater</a>
     */
    private void updateFile() {
        JavaPlugin plugin = Main.getPluginInstance();
        try {
            ConfigUpdater.update(plugin, fileName, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}