package com.gmail.artemis.the.gr8.playerstats.listeners;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.MessageFactory;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class JoinListener implements Listener {

    private static ConfigHandler config;
    private final ThreadManager threadManager;

    public JoinListener(ConfigHandler c, ThreadManager t) {
        config = c;
        threadManager = t;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent joinEvent) {
        if (!joinEvent.getPlayer().hasPlayedBefore()) {
            threadManager.startReloadThread(null, false);
        }

        else if (joinEvent.getPlayer().isOp() && !config.isConfigUpdated()) {
            joinEvent.getPlayer().sendMessage(MessageFactory.getPluginPrefix() + ChatColor.GRAY + ChatColor.ITALIC +
                    "Your config version is outdated! " +
                    "Please delete your current config.yml (or rename it/copy it to another folder) and do /statreload");
        }
    }
}
