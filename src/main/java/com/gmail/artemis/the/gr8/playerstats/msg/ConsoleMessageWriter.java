package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.enums.DebugLevel;
import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.ExampleMessage;
import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.HelpMessage;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;

import java.time.LocalDate;
import java.time.Month;

/** Composes messages to send to a Console. This class is responsible
 for constructing a final Component with the text content of the desired message.
 The component parts (with appropriate formatting) are supplied by a ComponentFactory.*/
public class ConsoleMessageWriter extends MessageWriter {

    private final ComponentFactory componentFactory;

    public ConsoleMessageWriter(ConfigHandler c) {
        super(c);

        boolean isBukkit = Bukkit.getName().equalsIgnoreCase("CraftBukkit");
        MyLogger.logMsg("Bukkit name: " + Bukkit.getName(), DebugLevel.MEDIUM);

        if (isBukkit) {
            componentFactory = new BukkitConsoleComponentFactory(config);
            MyLogger.logMsg("ConsoleMessageWriter is using Bukkit-Factory", DebugLevel.MEDIUM);
        }
        else if (config.useRainbowMode() ||
                (config.useFestiveFormatting() && LocalDate.now().getMonth().equals(Month.JUNE))) {
            componentFactory = new PrideComponentFactory(config);
            MyLogger.logMsg("ConsoleMessageWriter is using Pride-Factory", DebugLevel.MEDIUM);
        }
        else {
            componentFactory = new ComponentFactory(config);
            MyLogger.logMsg("ConsoleMessageWriter is using Default-Factory", DebugLevel.MEDIUM);
        }
    }

    @Override
    protected ComponentFactory componentFactory() {
        return componentFactory;
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