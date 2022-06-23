package com.gmail.artemis.the.gr8.playerstats.listeners;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final ThreadManager threadManager;

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
