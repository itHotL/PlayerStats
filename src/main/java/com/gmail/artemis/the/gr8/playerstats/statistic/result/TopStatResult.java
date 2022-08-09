package com.gmail.artemis.the.gr8.playerstats.statistic.result;

import net.kyori.adventure.text.TextComponent;

import java.util.LinkedHashMap;

public record TopStatResult(LinkedHashMap<String, Integer> value, TextComponent formattedComponent, String formattedString) implements StatResult<LinkedHashMap<String,Integer>> {

    @Override
    public LinkedHashMap<String, Integer> getNumericalValue() {
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