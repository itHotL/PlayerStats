package com.artemis.the.gr8.playerstats.utils;

import com.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.artemis.the.gr8.playerstats.multithreading.ThreadManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A utility class that deals with OfflinePlayers. It stores a list
 * of all OfflinePlayer-names that need to be included in statistic
 * calculations, and can retrieve the corresponding OfflinePlayer
 * object for a given player-name.
 */
public final class OfflinePlayerHandler extends FileHandler {

    private static volatile OfflinePlayerHandler instance;
    private final ConfigHandler config;
    private static FileConfiguration excludedPlayers;
    private static ConcurrentHashMap<String, UUID> offlinePlayerUUIDs;

    private OfflinePlayerHandler() {
        super("excluded_players.yml");
        config = ConfigHandler.getInstance();

        excludedPlayers = super.getFileConfiguration();
        loadOfflinePlayers();
    }

    public static OfflinePlayerHandler getInstance() {
        OfflinePlayerHandler localVar = instance;
        if (localVar != null) {
            return localVar;
        }

        synchronized (OfflinePlayerHandler.class) {
            if (instance == null) {
                instance = new OfflinePlayerHandler();
            }
            return instance;
        }
    }

    @Override
    public void reload() {
        super.reload();
        excludedPlayers = super.getFileConfiguration();

        loadOfflinePlayers();
    }

    /**
     * Checks if a given player is currently
     * included for /statistic lookups.
     *
     * @param playerName String (case-sensitive)
     * @return true if this player is included
     */
    public boolean isLoadedPlayer(String playerName) {
        return offlinePlayerUUIDs.containsKey(playerName);
    }

    public void addPlayerToExcludeList(UUID uniqueID) {
        super.addEntryToListInFile("excluded", uniqueID.toString());
    }

    public void removePlayerFromExcludeList(UUID uniqueID) {
        super.removeEntryFromListInFile("excluded", uniqueID.toString());
    }

    public List<String> getListOfExcludedPlayerNames() {
        List<String> excludedUUIDs = excludedPlayers.getStringList("excluded");
        return excludedUUIDs.stream()
                .map(UUID::fromString)
                .map(Bukkit::getOfflinePlayer)
                .map(OfflinePlayer::getName)
                .collect(Collectors.toList());
    }

    public boolean isExcluded(UUID uniqueID) {
        List<String> excluded = excludedPlayers.getStringList("excluded");

        return excluded.stream()
                .filter(Objects::nonNull)
                .map(UUID::fromString)
                .anyMatch(uuid -> uuid.equals(uniqueID));
    }

    /**
     * Gets the number of OfflinePlayers that are
     * currently included in statistic calculations.
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
    @Contract(" -> new")
    public @NotNull ArrayList<String> getOfflinePlayerNames() {
        return Collections.list(offlinePlayerUUIDs.keys());
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

            ForkJoinPool.commonPool().invoke(ThreadManager.getPlayerLoadAction(offlinePlayers, offlinePlayerUUIDs));

            MyLogger.actionFinished();
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