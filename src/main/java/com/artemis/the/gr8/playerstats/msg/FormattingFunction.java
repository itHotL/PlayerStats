package com.artemis.the.gr8.playerstats.msg;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.function.BiFunction;

public final class FormattingFunction {

    private final BiFunction<Integer, CommandSender, TextComponent> formattingFunction;

    public FormattingFunction(BiFunction<Integer, CommandSender, TextComponent> formattingFunction) {
        this.formattingFunction = formattingFunction;
    }

    public TextComponent getResultWithShareButton(Integer shareCode) {
        return this.apply(shareCode, null);
    }

    public TextComponent getResultWithSharerName(CommandSender sender) {
        return this.apply(null, sender);
    }

    public TextComponent getDefaultResult() {
        return this.apply(null, null);
    }

    private TextComponent apply(Integer shareCode, CommandSender sender) {
        return formattingFunction.apply(shareCode, sender);
    }
}
