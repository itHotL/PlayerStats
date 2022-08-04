package com.gmail.artemis.the.gr8.playerstats.statistic.result;

import com.gmail.artemis.the.gr8.playerstats.msg.components.ComponentUtils;
import net.kyori.adventure.text.TextComponent;

import java.util.LinkedHashMap;

public record TopStatResult(LinkedHashMap<String, Integer> value, TextComponent formattedValue) implements StatResult<LinkedHashMap<String,Integer>> {

    @Override
    public LinkedHashMap<String, Integer> getNumericalValue() {
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