package com.gmail.artemis.the.gr8.playerstats.listeners;

import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageFactory;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class JoinListener implements Listener {

    private final BukkitAudiences adventure;
    private final ThreadManager threadManager;
    private static ConfigHandler config;
    private static MessageFactory messageFactory;

    public JoinListener(BukkitAudiences a, ConfigHandler c, MessageFactory m, ThreadManager t) {
        adventure = a;
        config = c;
        messageFactory = m;
        threadManager = t;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent joinEvent) {
        if (!joinEvent.getPlayer().hasPlayedBefore()) {
            threadManager.startReloadThread(null, false);
        }

        else if (joinEvent.getPlayer().isOp() && !config.isConfigUpdated()) {
            adventure.player(joinEvent.getPlayer()).sendMessage(messageFactory.configIsOutdated());
        }
    }
}
