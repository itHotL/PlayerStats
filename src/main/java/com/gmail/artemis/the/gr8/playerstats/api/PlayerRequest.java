package com.gmail.artemis.the.gr8.playerstats.api;

public abstract class PlayerRequest implements APIRequest {

    protected final String playerName;

    public PlayerRequest(String playerName) {
        this.playerName = playerName;
    }
}
