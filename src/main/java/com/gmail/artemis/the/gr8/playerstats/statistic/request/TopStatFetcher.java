package com.gmail.artemis.the.gr8.playerstats.statistic.request;

import com.gmail.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import org.bukkit.Bukkit;

public class TopStatFetcher extends RequestGenerator {

    private final int topListSize;

    public TopStatFetcher(int topListSize) {
        this.topListSize = topListSize;
        super.statRequest = generateBaseRequest();
    }

    @Override
    protected StatRequestCore generateBaseRequest() {
        StatRequestCore request = new StatRequestCore(Bukkit.getConsoleSender(), true);
        request.setTarget(Target.TOP);
        return request;
    }
}
