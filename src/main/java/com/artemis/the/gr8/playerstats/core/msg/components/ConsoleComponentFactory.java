package com.artemis.the.gr8.playerstats.core.msg.components;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class ConsoleComponentFactory extends ComponentFactory {

    public ConsoleComponentFactory() {
        super();
    }

    @Override
    public boolean isConsoleFactory() {
        return true;
    }

    @Override
    public TextComponent heart() {
        return Component.text()
                .content(String.valueOf('\u2665'))
                .color(HEARTS)
                .build();
    }
}
