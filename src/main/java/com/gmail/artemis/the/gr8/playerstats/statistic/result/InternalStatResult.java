package com.gmail.artemis.the.gr8.playerstats.statistic.result;

import com.gmail.artemis.the.gr8.playerstats.api.StatFormatter;
import net.kyori.adventure.text.TextComponent;

/** This Record is used to store stat-results internally, so Players can share them by clicking a share-button.*/
public record InternalStatResult(String executorName, TextComponent statResult, int ID) implements StatResult<Integer> {

    /** Gets the ID number for this StatResult. Unlike for the other {@link StatResult} implementations,
     this one does not return the actual statistic data, because this implementation is meant for internal
     saving-and-sharing only. This method is only for Interface-consistency, InternalStatResult#ID is better.

     @return Integer that represents this StatResult's ID number
     */
    @Override
    public Integer getNumericalValue() {
        return ID;
    }

    @Override
    public TextComponent getFormattedTextComponent() {
        return statResult;
    }

    @Override
    public String getFormattedString() {
        return StatFormatter.TextComponentToString(statResult);
    }
}
