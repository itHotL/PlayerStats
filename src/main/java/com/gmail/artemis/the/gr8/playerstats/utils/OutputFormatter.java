package com.gmail.artemis.the.gr8.playerstats.utils;

import com.gmail.artemis.the.gr8.playerstats.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.Main;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class OutputFormatter {

    //keys for the HashMap are:
        //playerNames(Ranked)
        //statNames(Ranked)
        //subStatNames(Ranked)
        //numbers(Ranked)
    private final ConfigHandler config;
    private final Main plugin;
    private HashMap<String, ChatColor> chatColors;

    public OutputFormatter(ConfigHandler c, Main p) {
        config = c;
        plugin = p;
        updateOutputColors();
    }

    public String formatTopStats(LinkedHashMap<String, Integer> topStats) {
        return "";
    }

    public String formatPlayerStat(String playerName, String statName, int stat) {
        return formatPlayerStat(playerName, statName, null, stat);
    }

    public String formatPlayerStat(String playerName, String statName, String subStatEntryName, int stat) {
        long time = System.currentTimeMillis();
        time = plugin.logTimeTaken("OutputFormatter", time, 37);

        String subStat = subStatEntryName != null ?
                chatColors.get("subStatNames") + " (" + subStatEntryName.toLowerCase().replace("_", " ") + ")" : "";
        time = plugin.logTimeTaken("OutputFormatter", time, 41);

        String msg = chatColors.get("playerNames") + playerName + chatColors.get("numbers") + ": " + stat + " " +
                chatColors.get("statNames") + statName.toLowerCase().replace("_", " ") + subStat;
        time = plugin.logTimeTaken("OutputFormatter", time, 45);
        return msg;
    }

    public void updateOutputColors() {
        chatColors = config.getChatColors();
    }
}
