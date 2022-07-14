package com.gmail.artemis.the.gr8.playerstats.models.datamodel;

import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageWriter;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Statistic;

import java.util.LinkedHashMap;
import java.util.function.BiFunction;

/** Represents a bunch of BiFunctions that most of the time will have a MessageWriter as its first argument*/
public abstract class BiFunctionType<M, O, T> implements Type {

    public abstract TextComponent apply(MessageWriter messageWriter, Object o);


    public record MsgBoolean<M, B, T>(BiFunction<MessageWriter, Boolean, TextComponent> biFunction) implements Type {
        public TextComponent apply(MessageWriter messageWriter, boolean longWait) {
            return biFunction.apply(messageWriter, longWait);
        }
    }

    public record MsgStatType<M, S, T>(BiFunction<MessageWriter, Statistic.Type, TextComponent> biFunction) implements Type {
        public TextComponent apply(MessageWriter messageWriter, Statistic.Type statType) {
            return biFunction.apply(messageWriter, statType);
        }
    }

    public record StatRequestInt<S, I, T>(BiFunction<StatRequest, Integer, TextComponent> biFunction) implements Type {
        public TextComponent apply(StatRequest request, Integer playerStat) {
            return biFunction.apply(request, playerStat);
        }
    }

    public record StatRequestLong<S, L, T>(BiFunction<StatRequest, Long, TextComponent> biFunction) implements Type {
        public TextComponent apply(StatRequest request, Long serverStat) {
            return biFunction.apply(request, serverStat);
        }
    }

    public record StatRequestMap<S, M, T>(BiFunction<StatRequest, LinkedHashMap<String, Integer>, TextComponent> biFunction) implements Type {
        public TextComponent apply(StatRequest request, LinkedHashMap<String, Integer> topStats) {
            return biFunction.apply(request, topStats);
        }
    }
}