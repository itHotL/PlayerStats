package com.artemis.the.gr8.playerstats.core.msg.components;

import net.kyori.adventure.text.TextComponent;

public final class BirthdayComponentFactory extends ComponentFactory {

    public BirthdayComponentFactory() {
        super();
    }

    @Override
    public TextComponent pluginPrefixAsTitle() {
        return miniMessageToComponent(
                "<gradient:#a405e3:#f74040:#f73b3b:#ff9300:#f74040:#a405e3>" +
                        "<#FF9300>\ud83d\udd25</#FF9300> __________    [PlayerStats]    __________ " +
                        "<#FF9300>\ud83d\udd25</#FF9300></gradient>");
    }

    @Override
    public TextComponent pluginPrefix() {
        return miniMessageToComponent(
                "<gradient:#a405e3:#f74040:#ff9300>[PlayerStats]</gradient>");
    }
}