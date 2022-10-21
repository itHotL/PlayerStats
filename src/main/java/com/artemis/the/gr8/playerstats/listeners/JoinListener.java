package com.artemis.the.gr8.playerstats.listeners;

import com.artemis.the.gr8.playerstats.multithreading.ThreadManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.ApiStatus;

/**
 * Listens for new Players that join, and reloads PlayerStats
 * if someone joins that hasn't joined before.
 */
@ApiStatus.Internal
public class JoinListener implements Listener {

    private static ThreadManager threadManager;

    public JoinListener(ThreadManager t) {
        threadManager = t;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent joinEvent) {
        if (!joinEvent.getPlayer().hasPlayedBefore()) {
            threadManager.startReloadThread(null);
        }
    }
}