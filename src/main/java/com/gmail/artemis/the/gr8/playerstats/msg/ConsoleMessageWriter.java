package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.ExampleMessage;
import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.HelpMessage;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;

import java.time.LocalDate;
import java.time.Month;


public class ConsoleMessageWriter extends MessageWriter {

    private final boolean isBukkit;

    private static ComponentFactory componentFactory;

    public ConsoleMessageWriter(ConfigHandler c) {
        super(c);

        isBukkit = Bukkit.getName().equalsIgnoreCase("CraftBukkit");
    }

    @Override
    protected void getComponentFactory() {
        if (isBukkit) {
            componentFactory = new BukkitConsoleComponentFactory(config);
        }
        else if (config.enableRainbowMode() ||
                (config.enableFestiveFormatting() && LocalDate.now().getMonth().equals(Month.JUNE))) {
            componentFactory = new PrideComponentFactory(config);
        }
        else {
            componentFactory = new ComponentFactory(config);
        }
    }

    @Override
    public TextComponent usageExamples() {
        return new ExampleMessage(componentFactory);
    }

    @Override
    public TextComponent helpMsg() {
        return new HelpMessage(componentFactory,
                false,
                config.getTopListMaxSize());
    }
}