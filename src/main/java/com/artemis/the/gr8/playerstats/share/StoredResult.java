package com.artemis.the.gr8.playerstats.share;

import net.kyori.adventure.text.TextComponent;

/**
 * This Record is used to store stat-results internally,
 * so Players can share them by clicking a share-button.
 */
public record StoredResult(String executorName, TextComponent formattedValue, int ID) {
}