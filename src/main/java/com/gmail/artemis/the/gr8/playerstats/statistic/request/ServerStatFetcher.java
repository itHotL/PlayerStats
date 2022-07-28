package com.gmail.artemis.the.gr8.playerstats.statistic.request;

import com.gmail.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import org.bukkit.Bukkit;

public final class ServerStatFetcher extends RequestGenerator {

    public ServerStatFetcher() {
        super.statRequest = generateBaseRequest();
    }

    @Override
    protected StatRequestCore generateBaseRequest() {
        StatRequestCore request = new StatRequestCore(Bukkit.getConsoleSender(), true);
        request.setTarget(Target.SERVER);
        return request;
    }
}
