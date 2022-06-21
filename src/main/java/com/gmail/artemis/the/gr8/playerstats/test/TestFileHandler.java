package com.gmail.artemis.the.gr8.playerstats.test;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class TestFileHandler {

    private File testFile;
    private FileConfiguration testConf;
    private ConfigurationSection number;
    private final Plugin plugin;

    private final String onEnable;
    private final String reload;
    private final String debugging;
    private final String topStat;


    public TestFileHandler() {
        plugin = getPlugin();
        onEnable = "onEnable";
        reload = "reload";
        debugging = "exception-debugging";
        topStat = "top-stat";
    }

    private Plugin getPlugin() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerStats")) {
            Bukkit.getLogger().info("Successfully fetched PlayerStats for testing!");
            return Bukkit.getPluginManager().getPlugin("PlayerStats");
        }
        else {
            Bukkit.getLogger().info("Couldn't retrieve PlayerStats for testing...");
            return null;
        }
    }

    /**
     * Creates a new config section for the given threshold. Only needs to be called once, unless threshold changes.
     * @param count amount of players to calculate statistics with
     * @param threshold how small the subTasks have to become
     */
    public void saveThreshold(int count, int threshold) {
        loadFile(count);
        String path = threshold + " threshold";
        try {
            number = testConf.getConfigurationSection(path);
            if (number == null) {
                number = testConf.createSection(path);
                number.createSection(onEnable);
                number.createSection(reload);
                number.createSection(debugging);
                number.createSection(topStat);
            }
            else {
                number = testConf.getConfigurationSection(path);
            }
            saveFile();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logRunCount(boolean errorEncountered) {
        try {
            ConfigurationSection section = number.getConfigurationSection(debugging);
            if (section != null) {
                int runs = section.getInt("runs");
                section.set("runs", runs +1);

                if (errorEncountered) {
                    int errors = section.getInt("errors");
                    section.set("errors", errors + 1);

                    String path = "error-" + (errors + 1) + "-during-run";
                    int lastError = section.getInt("error-" + errors + "-during-run");

                    int runsUntilError = runs - lastError;
                    String path2 = "until-error-" + (errors + 1);

                    section.set(path2, runsUntilError);
                    section.set(path, runs);
                }
                saveFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs how long a certain method took for the earlier set threshold. Always make sure saveThreshold has been
     * called once before this method is called.
     * @param time how long the given action took
     * @param scenario describes which section to get. 1 means onEnable, 2 means reload, and 3 means top-stat
     */
    public void saveTimeTaken(long time, int scenario) {
        String path = "";
        if (scenario == 1) path = onEnable;
        else if (scenario == 2) path = reload;
        else if (scenario == 3) path = topStat;

        try {
            ConfigurationSection section = number.getConfigurationSection(path);
            if (section != null) {
                saveTimeToSection(time, section);
                saveFile();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveTimeToSection(long time, ConfigurationSection section) {
        if (section.contains("average")) {
            long average = section.getLong("average");
            long newAverage = ((average * (section.getKeys(false).size() -1)) + time)/section.getKeys(false).size();
            section.set(section.getKeys(false).size() + "", time);
            section.set("average", newAverage);
        }

        else {
            section.set("average", time);
            section.set("1", time);
        }
    }

    private void loadFile(int players) {
        String fileName = "test_" + players + ".yml";
        testFile = new File(plugin.getDataFolder(), fileName);
        if (!testFile.exists()) {
            plugin.getLogger().info("Attempting to create testFile...");
            createFile();
        }

        testConf = new YamlConfiguration();
        try {
            testConf.load(testFile);
        }
        catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
        saveFile();
    }

    private void createFile() {
        testFile.getParentFile().mkdirs();
        try {
            testFile.createNewFile();
            plugin.getLogger().info("Even though this would return false, secretly a file has been created anyway");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFile() {
        try {
            testConf.save(testFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

