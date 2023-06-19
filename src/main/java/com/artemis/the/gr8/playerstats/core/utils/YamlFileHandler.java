package com.artemis.the.gr8.playerstats.core.utils;

import com.artemis.the.gr8.playerstats.core.Main;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class YamlFileHandler implements Reloadable {

    private final String fileName;
    private File file;
    private FileConfiguration fileConfiguration;

    public YamlFileHandler(String fileName) {
        this.fileName = fileName;
        loadFile();
    }

    private void loadFile() {
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

    public void addValues(@NotNull Map<String, Object> keyValuePairs) {
        keyValuePairs.forEach(this::setValue);
        save();
        updateFile();
    }

    /**
     * @param key the Key under which the List will be stored
     *            (or expanded if it already exists)
     * @param value the value(s) to expand the List with
     */
    public void writeEntryToList(@NotNull String key, @NotNull String value) {
        List<String> existingList = fileConfiguration.getStringList(key);

        List<String> updatedList = existingList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        updatedList.add(value);

        setValue(key, updatedList);
        save();
        updateFile();
    }

    public void removeEntryFromList(@NotNull String key, @NotNull String value) {
        List<String> currentValues = fileConfiguration.getStringList(key);

        if (currentValues.remove(value)) {
            setValue(key, currentValues);
            save();
            updateFile();
        }
    }

    private void setValue(String key, Object value) {
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