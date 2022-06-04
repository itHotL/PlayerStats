package com.gmail.artemis.the.gr8.playerstats.filehandlers;

import com.gmail.artemis.the.gr8.playerstats.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class TestFileHandler {

    private static File testFile;
    private static FileConfiguration testConf;
    private static ConfigurationSection playerCount;
    private final Main plugin;


    public TestFileHandler(Main p) {
        plugin = p;
        loadFile();
    }

    public static void savePlayerCount(int count) {
        try {
            playerCount = testConf.getConfigurationSection(count + " players");
            if (playerCount == null) {
                playerCount = testConf.createSection(count + " players");
                playerCount.createSection("onEnable");
                playerCount.createSection("individual-stat");
                playerCount.createSection("top-stat");
            }
            else {
                playerCount = testConf.getConfigurationSection(count + " players");
            }
            saveFile();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveTimeTaken(long time, String timeDescription) {
        try {
            if (timeDescription.equalsIgnoreCase("onEnable")) {
                saveToSection(time, playerCount.getConfigurationSection("onEnable"));
                saveFile();
            }
            else if (timeDescription.equalsIgnoreCase("individual-stat")) {
                saveToSection(time, playerCount.getConfigurationSection("individual-stat"));
                saveFile();
            }
            else if (timeDescription.equalsIgnoreCase("top-stat")) {
                saveToSection(time, playerCount.getConfigurationSection("top-stat"));
                saveFile();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveToSection(long time, ConfigurationSection section) {
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

    private void loadFile() {
        testFile = new File(plugin.getDataFolder(), "test.yml");
        if (!testFile.exists()) {
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

    private static void createFile() {
        testFile.getParentFile().mkdirs();
        try {
            testFile.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean saveFile() {
        try {
            testConf.save(testFile);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
