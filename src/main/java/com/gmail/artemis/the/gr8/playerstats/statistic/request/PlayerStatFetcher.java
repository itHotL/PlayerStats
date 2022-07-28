package com.gmail.artemis.the.gr8.playerstats.statistic.request;

import com.gmail.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import org.bukkit.Bukkit;

public final class PlayerStatFetcher extends RequestGenerator {

    private final String playerName;

    public PlayerStatFetcher(String playerName) {
        super.statRequest = generateBaseRequest();
        this.playerName = playerName;
    }

    @Override
    protected StatRequestCore generateBaseRequest() {
        StatRequestCore request = new StatRequestCore(Bukkit.getConsoleSender(), true);
        request.setTarget(Target.PLAYER);
        request.setPlayerName(playerName);
        return request;
    }
}