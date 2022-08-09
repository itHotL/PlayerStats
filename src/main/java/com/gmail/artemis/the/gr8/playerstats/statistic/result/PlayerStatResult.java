package com.gmail.artemis.the.gr8.playerstats.statistic.result;

import net.kyori.adventure.text.TextComponent;

public record PlayerStatResult(int value, TextComponent formattedComponent, String formattedString) implements StatResult<Integer> {

    @Override
    public Integer getNumericalValue() {
        return value;
    }

    @Override
    public TextComponent getFormattedTextComponent() {
        return formattedComponent;
    }

    @Override
    public String getFormattedString() {
        return formattedString;
    }
}