package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.ShareManager;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageWriter;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ShareCommand implements CommandExecutor {

    private static BukkitAudiences adventure;
    private static ShareManager shareManager;
    private final MessageWriter messageWriter;

    public ShareCommand(ShareManager s, MessageWriter m) {
        adventure = Main.adventure();
        shareManager = s;
        messageWriter = m;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 1 && shareManager.isEnabled()) {
            UUID shareCode;
            try {
                shareCode = UUID.fromString(args[0]);
            } catch (IllegalArgumentException e) {
                MyLogger.logException(e, "ShareCommand", "/statshare is being called without a valid UUID argument");
                return false;
            }
            if (shareManager.requestAlreadyShared(shareCode)) {
                adventure.sender(sender).sendMessage(messageWriter.resultsAlreadyShared());
            }
            else if (shareManager.isOnCoolDown(sender.getName())) {
                adventure.sender(sender).sendMessage(messageWriter.stillOnShareCoolDown());
            }
            else {
                TextComponent result = shareManager.getStatResult(sender.getName(), shareCode);
                if (result == null) {  //at this point the only possible cause of statResult being null is the request being older than 25 player-requests ago
                    adventure.sender(sender).sendMessage(messageWriter.statResultsTooOld());
                } else {
                    adventure.all().sendMessage(result);
                }
            }
        }
        return true;
    }
}
