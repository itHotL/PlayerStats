package com.gmail.artemis.the.gr8.playerstats.models.datamodel;

import com.gmail.artemis.the.gr8.playerstats.msg.MessageWriter;
import net.kyori.adventure.text.TextComponent;

import java.util.function.Function;

public record FunctionType<M, T>(Function<MessageWriter, TextComponent> function) implements Type {

    public TextComponent apply(MessageWriter messageWriter) {
        return function.apply(messageWriter);
    }
}