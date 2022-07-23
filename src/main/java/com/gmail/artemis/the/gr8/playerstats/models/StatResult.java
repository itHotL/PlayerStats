package com.gmail.artemis.the.gr8.playerstats.models;

import net.kyori.adventure.text.TextComponent;

import java.util.UUID;

/** This Record is used to store stat-results internally, so Players can share them by clicking a share-button.*/
public record StatResult(String playerName, TextComponent statResult, int ID, UUID uuid) {
}
