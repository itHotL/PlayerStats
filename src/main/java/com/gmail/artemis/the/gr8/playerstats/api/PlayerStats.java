package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

public interface PlayerStats {

    static PlayerStats getAPI() {
        return Main.getPlayerStatsAPI();
    }

    TextComponent getFancyStat(Target selection, CommandSender sender, String[] args) throws IllegalArgumentException;
}