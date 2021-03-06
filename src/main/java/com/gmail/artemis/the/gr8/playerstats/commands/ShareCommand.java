package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.ShareManager;
import com.gmail.artemis.the.gr8.playerstats.enums.StandardMessage;
import com.gmail.artemis.the.gr8.playerstats.models.StatResult;
import com.gmail.artemis.the.gr8.playerstats.msg.OutputManager;
import com.gmail.artemis.the.gr8.playerstats.utils.MyLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ShareCommand implements CommandExecutor {

    private static ShareManager shareManager;
    private static OutputManager outputManager;

    public ShareCommand(ShareManager s, OutputManager m) {
        shareManager = s;
        outputManager = m;
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
                outputManager.sendFeedbackMsg(sender, StandardMessage.RESULTS_ALREADY_SHARED);
            }
            else if (shareManager.isOnCoolDown(sender.getName())) {
                outputManager.sendFeedbackMsg(sender, StandardMessage.STILL_ON_SHARE_COOLDOWN);
            }
            else {
                StatResult result = shareManager.getStatResult(sender.getName(), shareCode);
                if (result == null) {  //at this point the only possible cause of statResult being null is the request being older than 25 player-requests ago
                    outputManager.sendFeedbackMsg(sender, StandardMessage.STAT_RESULTS_TOO_OLD);
                } else {
                    outputManager.shareStatResults(result.statResult());
                }
            }
        }
        return true;
    }
}
