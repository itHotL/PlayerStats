package com.artemis.the.gr8.playerstats.core.msg.components;

import net.kyori.adventure.text.TextComponent;

public class BirthdayComponentFactory extends ComponentFactory {

    public BirthdayComponentFactory() {
        super();
    }

    @Override
    public TextComponent pluginPrefixAsTitle() {
        return miniMessageToComponent(
                "<gradient:#a405e3:#f74040:red:#ff9300:#f74040:#a405e3>" +
                        "<white>\ud83d\udd25</white> __________    [PlayerStats]    __________ " +
                        "<white>\ud83d\udd25</white></gradient>");
    }
}
