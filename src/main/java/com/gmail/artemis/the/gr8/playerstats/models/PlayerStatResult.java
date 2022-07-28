package com.gmail.artemis.the.gr8.playerstats.models;

import com.gmail.artemis.the.gr8.playerstats.api.StatFormatter;
import net.kyori.adventure.text.TextComponent;

public record PlayerStatResult(int value, TextComponent formattedValue) implements StatResult<Integer> {

    @Override
    public Integer getNumericalValue() {
        return value;
    }

    @Override
    public TextComponent getFormattedTextComponent() {
        return formattedValue;
    }

    @Override
    public String getFormattedString() {
        return StatFormatter.statResultComponentToString(formattedValue);
    }
}