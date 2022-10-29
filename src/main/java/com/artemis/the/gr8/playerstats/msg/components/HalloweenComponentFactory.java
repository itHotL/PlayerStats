package com.artemis.the.gr8.playerstats.msg.components;

import net.kyori.adventure.text.TextComponent;

public class HalloweenComponentFactory extends ComponentFactory {


    public HalloweenComponentFactory() {
        super();
    }

    @Override
    public TextComponent pluginPrefixAsTitle() {
        return miniMessageToComponent(
                "<gradient:#f74040:gold:#FF6600:#f74040>" +
                "<white>\u2620</white> __________    [PlayerStats]    __________ " +
                "<white>\u2620</white></gradient>");
    }

    @Override
    public TextComponent pluginPrefix() {
        return miniMessageToComponent(
                "<gradient:#f74040:gold:#f74040>[PlayerStats]</gradient>");
    }
}