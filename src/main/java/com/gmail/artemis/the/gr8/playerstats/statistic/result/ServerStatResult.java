package com.gmail.artemis.the.gr8.playerstats.statistic.result;

import com.gmail.artemis.the.gr8.playerstats.msg.components.ComponentUtils;
import net.kyori.adventure.text.TextComponent;

public record ServerStatResult(long value, TextComponent formattedValue) implements StatResult<Long> {

    @Override
    public Long getNumericalValue() {
        return value;
    }

    @Override
    public TextComponent getFormattedTextComponent() {
        return formattedValue;
    }

    @Override
    public String getFormattedString() {
        return ComponentUtils.getTranslatableComponentSerializer()
                .serialize(formattedValue);
    }
}
