package com.gmail.artemis.the.gr8.playerstats.statistic.request;

import com.gmail.artemis.the.gr8.playerstats.statistic.result.StatResult;

public record StatRequest(StatRequestCore requestCore) {

    public <T> StatResult<T> execute() {
        return null;
    }

    public void eeeeeeh() {
        StatResult<StringBuffer> lod = execute();
    }
}

//TODO fix >:(