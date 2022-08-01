package com.gmail.artemis.the.gr8.playerstats.statistic.request;

import com.gmail.artemis.the.gr8.playerstats.api.PlayerStats;
import com.gmail.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.gmail.artemis.the.gr8.playerstats.api.RequestExecutor;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

public class RequestManager implements RequestGenerator {

    private final StatRequest statRequest;

    public RequestManager(StatRequest request) {

        this.statRequest = request;
    }



    @Override
    public StatRequest untyped(Statistic statistic) {
        statRequest.setStatistic(statistic);
        return statRequest;
    }

    @Override
    public StatRequest blockOrItemType(Statistic statistic, Material material) {
        statRequest.setSubStatEntryName(material.toString());
        if (statistic.getType() == Statistic.Type.BLOCK) {
            statRequest.setBlock(material);
        } else {
            statRequest.setItem(material);
        }
        return statRequest;
    }

    @Override
    public StatRequest entityType(Statistic statistic, EntityType entityType) {
        statRequest.setSubStatEntryName(entityType.toString());
        statRequest.setEntity(entityType);
        return statRequest;
    }

    public static StatRequest generateBasicPlayerRequest(String playerName) {
        StatRequest request = new StatRequest(Bukkit.getConsoleSender(), true);
        request.setTarget(Target.PLAYER);
        request.setPlayerName(playerName);
        return request;
    }

    public static StatRequest generateBasicServerRequest() {
        StatRequest request = new StatRequest(Bukkit.getConsoleSender(), true);
        request.setTarget(Target.SERVER);
        return request;
    }

    public static StatRequest generateBasicTopRequest(int topListSize) {
        StatRequest request = new StatRequest(Bukkit.getConsoleSender(), true);
        request.setTarget(Target.TOP);
        request.setTopListSize(topListSize != 0 ? topListSize : 10);
        return request;
    }

    private <T> RequestExecutor<Integer> getExecutor() {
        return switch (statRequest.getTarget()) {
            case PLAYER -> new PlayerStatRequest(statRequest);
            case SERVER -> null;
            case TOP -> null;
        };
    }
}