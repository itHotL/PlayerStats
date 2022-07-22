package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashMap;

public class PlayerStatsAPI implements PlayerStats {


    @Override
    public StatRequest generateRequest(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean requestIsValid(StatRequest request) {
        return false;
    }

    @Override
    public String toString(TextComponent component) {
        return null;
    }

    @Override
    public TextComponent formatPlayerStat(StatRequest request, int playerStat) {
        return null;
    }

    @Override
    public TextComponent formatServerStat(StatRequest request, long serverStat) {
        return null;
    }

    @Override
    public TextComponent formatTopStat(StatRequest request, LinkedHashMap<String, Integer> topStats) {
        return null;
    }

    @Override
    public LinkedHashMap<String, Integer> getTopStats(StatRequest request) {
        return null;
    }

    @Override
    public long getServerStat(StatRequest request) {
        return 0;
    }

    @Override
    public int getPlayerStat(StatRequest request) {
        return 0;
    }
}