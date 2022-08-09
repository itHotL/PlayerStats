package com.gmail.artemis.the.gr8.playerstats.statistic.result;

import net.kyori.adventure.text.TextComponent;

public record ServerStatResult(long value, TextComponent formattedComponent, String formattedString) implements StatResult<Long> {

    @Override
    public Long getNumericalValue() {
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
