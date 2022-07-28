package com.gmail.artemis.the.gr8.playerstats.models;

import com.gmail.artemis.the.gr8.playerstats.api.StatFormatter;
import net.kyori.adventure.text.TextComponent;

import java.util.LinkedHashMap;

public record TopStatResult(LinkedHashMap<String, Integer> value, TextComponent formattedStatResult) implements StatResult<LinkedHashMap<String,Integer>> {

    @Override
    public LinkedHashMap<String, Integer> getNumericalValue() {
        return value;
    }

    @Override
    public TextComponent getFormattedTextComponent() {
        return formattedStatResult;
    }

    @Override
    public String getFormattedString() {
        return StatFormatter.statResultComponentToString(formattedStatResult);
    }
}