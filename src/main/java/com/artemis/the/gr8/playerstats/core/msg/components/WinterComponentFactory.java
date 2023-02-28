package com.artemis.the.gr8.playerstats.core.msg.components;

import net.kyori.adventure.text.TextComponent;

public final class WinterComponentFactory extends ComponentFactory {

    public WinterComponentFactory() {
        super();
    }

    @Override
    public TextComponent pluginPrefixAsTitle() {
        return miniMessageToComponent(
                "<gradient:#4f20f7:#4bc3fa:#05ebb1:#4f20f7>" +
                        "<#D6F1FE>\u2744</#D6F1FE> __________    [PlayerStats]    __________ " +
                        "<#D6F1FE>\u2744</#D6F1FE></gradient>");
    }

    @Override
    public TextComponent pluginPrefix() {
        return miniMessageToComponent(
                "<gradient:#4CA5F9:#15D6C4:#409ef7:#4F2FF7>[PlayerStats]</gradient>");
    }
}