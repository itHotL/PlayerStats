package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.ShareManager;
import com.gmail.artemis.the.gr8.playerstats.enums.PluginMessage;
import com.gmail.artemis.the.gr8.playerstats.msg.MessageSender;
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
    private static MessageSender messageSender;

    public ShareCommand(ShareManager s, MessageSender m) {
        adventure = Main.adventure();
        shareManager = s;
        messageSender = m;
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
                messageSender.send(sender, PluginMessage.RESULTS_ALREADY_SHARED);
            }
            else if (shareManager.isOnCoolDown(sender.getName())) {
                messageSender.send(sender, PluginMessage.STILL_ON_SHARE_COOLDOWN);
            }
            else {
                TextComponent result = shareManager.getStatResult(sender.getName(), shareCode);
                if (result == null) {  //at this point the only possible cause of statResult being null is the request being older than 25 player-requests ago
                    messageSender.send(sender, PluginMessage.STAT_RESULTS_TOO_OLD);
                } else {
                    //TODO add shared-by signature
                    adventure.all().sendMessage(result);
                }
            }
        }
        return true;
    }
}
