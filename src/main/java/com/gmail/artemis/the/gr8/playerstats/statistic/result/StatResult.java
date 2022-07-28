package com.gmail.artemis.the.gr8.playerstats.statistic.result;

import net.kyori.adventure.text.TextComponent;

public interface StatResult<T> {

    T getNumericalValue();

    TextComponent getFormattedTextComponent();

    String getFormattedString();
}