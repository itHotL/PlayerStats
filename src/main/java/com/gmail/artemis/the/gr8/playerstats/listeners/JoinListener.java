package com.gmail.artemis.the.gr8.playerstats.listeners;

import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class JoinListener implements Listener {

    public JoinListener() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent joinEvent) {
        if (!joinEvent.getPlayer().hasPlayedBefore()) {
            OfflinePlayerHandler.updateOfflinePlayers();
        }
    }
}
