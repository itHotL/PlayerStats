package com.gmail.artemis.the.gr8.playerstats.statistic.result;

import com.gmail.artemis.the.gr8.playerstats.msg.components.ComponentUtils;
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
    public String toString() {
        return ComponentUtils.getTranslatableComponentSerializer()
                .serialize(formattedValue);
    }
}