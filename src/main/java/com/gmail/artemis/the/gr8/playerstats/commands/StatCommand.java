package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.ThreadManager;
import com.gmail.artemis.the.gr8.playerstats.msg.OutputManager;
import com.gmail.artemis.the.gr8.playerstats.statistic.RequestManager;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;


public class StatCommand implements CommandExecutor {

    private static ThreadManager threadManager;
    private static OutputManager outputManager;
    private final RequestManager requestManager;

    public StatCommand(OutputManager m, ThreadManager t, RequestManager r) {
        threadManager = t;
        outputManager = m;
        requestManager = r;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {  //in case of less than 1 argument or "help", display the help message
            outputManager.sendHelp(sender);
        }
        else if (args[0].equalsIgnoreCase("examples") ||
                args[0].equalsIgnoreCase("example")) {  //in case of "statistic examples", show examples
            outputManager.sendExamples(sender);
        }
        else if (args[0].equalsIgnoreCase(">:(")) {
            Component msg = MiniMessage.miniMessage().deserialize("<gradient:#f74040:#FF6600:#f74040>fire demon</gradient>");
            String msgString = LegacyComponentSerializer.builder().hexColors().build().serialize(msg);
            sender.sendMessage("LCS.hexColors(): " + msgString);
            MyLogger.logMsg(msgString);

            String msgString2 = LegacyComponentSerializer.legacySection().serialize(msg);
            sender.sendMessage("LCS.legacySection: " + msgString2);
            MyLogger.logMsg(msgString2);

            //only this one works both in-game and in-console
            String msgString3 = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build().serialize(msg);
            sender.sendMessage("LCS.hexColors().spigotformat...: " + msgString3);
            MyLogger.logMsg(msgString3);
        }
        else if (args[0].equalsIgnoreCase("test")) {
            TranslatableComponent msg = Component.translatable(Statistic.ANIMALS_BRED.getKey().getNamespace() + "." + Statistic.ANIMALS_BRED.getKey().getKey());
            TranslatableComponent msg2 = Component.translatable("stat." + Statistic.ANIMALS_BRED.getKey().getNamespace() + "." + Statistic.ANIMALS_BRED.getKey().getKey());
            Main.getAdventure().console().sendMessage(msg);
            Main.getAdventure().console().sendMessage(msg2);

            MyLogger.logMsg("key to String: " + Statistic.KILL_ENTITY.getKey());
            MyLogger.logMsg("key.getNamespace(): " + Statistic.KILL_ENTITY.getKey().getNamespace());
            MyLogger.logMsg("key.getKey(): " + Statistic.KILL_ENTITY.getKey().getKey());
        }
        else {
            StatRequest request = requestManager.generateRequest(sender, args);
            if (requestManager.validateRequest(request)) {
                threadManager.startStatThread(request);
            } else {
                return false;
            }
        }
        return true;
    }
}