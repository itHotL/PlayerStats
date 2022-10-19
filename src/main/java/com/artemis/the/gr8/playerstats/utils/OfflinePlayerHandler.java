package com.artemis.the.gr8.playerstats.utils;

import com.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.artemis.the.gr8.playerstats.reload.PlayerLoadAction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Predicate;

/**
 * A utility class that deals with OfflinePlayers. It stores a list
 * of all OfflinePlayer-names that need to be included in statistic
 * calculations, and can retrieve the corresponding OfflinePlayer
 * object for a given player-name.
 */
public final class OfflinePlayerHandler extends FileHandler {

    private static ConfigHandler config;
    private static FileConfiguration excludedPlayers;
    private ConcurrentHashMap<String, UUID> offlinePlayerUUIDs;
    private ArrayList<String> playerNames;

    public OfflinePlayerHandler(ConfigHandler configHandler) {
        super("excluded_players.yml");
        excludedPlayers = super.getFileConfiguration();
        config = configHandler;
        loadOfflinePlayers();
    }

    @Override
    public void reload() {
        super.reload();
        excludedPlayers = super.getFileConfiguration();
        loadOfflinePlayers();
    }

    /**
     * Checks if a given playerName is on the private HashMap of players
     * that should be included in statistic calculations.
     *
     * @param playerName String (case-sensitive)
     * @return true if this Player should be included in calculations
     */
    public boolean isRelevantPlayer(String playerName) {
        return offlinePlayerUUIDs.containsKey(playerName);
    }

    public void excludePlayer(UUID uniqueID) {
        super.addValueToListInFile("excluded", uniqueID);
    }

    public static boolean isExcluded(UUID uniqueID) {
        List<?> excluded = excludedPlayers.getList("excluded");
        if (excluded == null) {
            return false;
        }
        for (Object obj : excluded) {
            if (obj.equals(uniqueID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the number of OfflinePlayers that are included in
     * statistic calculations.
     *
     * @return the number of included OfflinePlayers
     */
    public int getOfflinePlayerCount() {
        return offlinePlayerUUIDs.size();
    }

    /**
     * Gets an ArrayList of names from all OfflinePlayers that should
     * be included in statistic calculations.
     *
     * @return the ArrayList
     */
    public ArrayList<String> getOfflinePlayerNames() {
        return playerNames;
    }

    /**
     * Uses the playerName to get the player's UUID from a private HashMap,
     * and uses the UUID to get the corresponding OfflinePlayer Object.
     *
     * @param playerName name of the target player (case-sensitive)
     * @return OfflinePlayer
     * @throws IllegalArgumentException if this player is not on the list
     * of players that should be included in statistic calculations
     */
    public @NotNull OfflinePlayer getOfflinePlayer(String playerName) throws IllegalArgumentException {
        if (offlinePlayerUUIDs.get(playerName) != null) {
            return Bukkit.getOfflinePlayer(offlinePlayerUUIDs.get(playerName));
        }
        else {
            MyLogger.logWarning("Cannot calculate statistics for player-name: " + playerName +
                    "! Double-check if the name is spelled correctly (including capital letters), " +
                    "or if any of your config settings exclude them");
            throw new IllegalArgumentException("Cannot convert this player-name into a valid Player to calculate statistics for");
        }
    }

    private void loadOfflinePlayers() {
        Executors.newSingleThreadExecutor().execute(() -> {
            long time = System.currentTimeMillis();

            OfflinePlayer[] offlinePlayers;
            if (config.whitelistOnly()) {
                offlinePlayers = getWhitelistedPlayers();
            }
            else if (config.excludeBanned()) {
                offlinePlayers = getNonBannedPlayers();
            }
            else {
                offlinePlayers = Bukkit.getOfflinePlayers();
            }

            int size = offlinePlayerUUIDs != null ? offlinePlayerUUIDs.size() : 16;
            offlinePlayerUUIDs = new ConcurrentHashMap<>(size);

            PlayerLoadAction task = new PlayerLoadAction(offlinePlayers, config.getLastPlayedLimit(), offlinePlayerUUIDs);
            MyLogger.actionCreated(offlinePlayers != null ? offlinePlayers.length : 0);
            ForkJoinPool.commonPool().invoke(task);
            MyLogger.actionFinished();

            playerNames = Collections.list(offlinePlayerUUIDs.keys());
            MyLogger.logLowLevelTask(("Loaded " + offlinePlayerUUIDs.size() + " offline players"), time);
        });
    }

    private OfflinePlayer[] getWhitelistedPlayers() {
        return Bukkit.getWhitelistedPlayers().toArray(OfflinePlayer[]::new);
    }

    private @NotNull OfflinePlayer[] getNonBannedPlayers() {
        if (Bukkit.getPluginManager().isPluginEnabled("LiteBans")) {
            return Arrays.stream(Bukkit.getOfflinePlayers())
                    .parallel()
                    .filter(Predicate.not(OfflinePlayer::isBanned))
                    .toArray(OfflinePlayer[]::new);
        }

        Set<OfflinePlayer> banList = Bukkit.getBannedPlayers();
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .parallel()
                .filter(Predicate.not(banList::contains))
                .toArray(OfflinePlayer[]::new);
    }
}