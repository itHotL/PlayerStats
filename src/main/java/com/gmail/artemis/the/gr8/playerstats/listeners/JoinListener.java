package com.gmail.artemis.the.gr8.playerstats.listeners;

import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class JoinListener implements Listener {

    private final OfflinePlayerHandler offlinePlayerHandler;

    public JoinListener() {
        offlinePlayerHandler = OfflinePlayerHandler.getInstance();
    }

    public void onPlayerJoin(PlayerJoinEvent joinEvent) {
        if (!joinEvent.getPlayer().hasPlayedBefore()) {
            offlinePlayerHandler.updateOfflinePlayers();
        }
    }
}
