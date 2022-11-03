package com.artemis.the.gr8.playerstats.core.msg.components;

import net.kyori.adventure.text.TextComponent;

public class WinterComponentFactory extends ComponentFactory {

    public WinterComponentFactory() {
        super();
    }

    @Override
    public TextComponent pluginPrefixAsTitle() {
        return miniMessageToComponent(
                "<gradient:#4f20f7:#4bc3fa:#05ebb1:#4f20f7>" +
                        "<white>\u2744</white> __________    [PlayerStats]    __________ " +
                        "<white>\u2744</white></gradient>");
    }

    @Override
    public TextComponent pluginPrefix() {
        return miniMessageToComponent(
                "<gradient:#4bc3fa:#05ebb1:#409ef7>[PlayerStats]</gradient>");
    }
}
