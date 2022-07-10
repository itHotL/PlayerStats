package com.gmail.artemis.the.gr8.playerstats.models;

import net.kyori.adventure.text.TextComponent;

import java.util.UUID;


public record StatResult(String playerName, TextComponent statResult, int ID, UUID uuid) {
}
