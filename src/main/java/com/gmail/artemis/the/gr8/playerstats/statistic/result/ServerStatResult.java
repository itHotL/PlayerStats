package com.gmail.artemis.the.gr8.playerstats.statistic.result;

import com.gmail.artemis.the.gr8.playerstats.api.StatFormatter;
import net.kyori.adventure.text.TextComponent;

public record ServerStatResult(long value, TextComponent formattedStatResult) implements StatResult<Long> {

    @Override
    public Long getNumericalValue() {
        return value;
    }

    @Override
    public TextComponent getFormattedTextComponent() {
        return formattedStatResult;
    }

    @Override
    public String getFormattedString() {
        return StatFormatter.TextComponentToString(formattedStatResult);
    }
}
