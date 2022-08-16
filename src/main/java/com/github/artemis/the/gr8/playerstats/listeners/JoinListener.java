package com.github.artemis.the.gr8.playerstats.listeners;

import com.github.artemis.the.gr8.playerstats.ThreadManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/** Listens for new Players that join, and reloads PlayerStats
 * if someone joins that hasn't joined before.
 */
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