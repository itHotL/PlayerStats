package com.gmail.artemis.the.gr8.playerstats.msg.msgutils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.Random;

//This class is just for fun, and adds some silly names for players on my server.
//It does not impact the rest of the plugin, and will only be used for the players mentioned in here.
public class EasterEggProvider {

    private static final Random random;

    static{
        random = new Random();
    }

    public static Component getPlayerName(Player player) {
        int sillyNumber = getSillyNumber();
        String playerName = null;
        switch (player.getUniqueId().toString()) {
            case "8fb811dc-2ceb-4528-9951-cf803e0550a1" -> {
                if (sillyNumberIsBetween(sillyNumber, 0, 20)) {
                    playerName = "<bold><#7d330e><#D17300>b</#D17300>e<#D17300>e</#D17300> b<#D17300>o</#D17300>i";
                }
            }
            case "b7d2e46f-cc89-434c-9757-f71a681e168a" -> {
                if (sillyNumberIsBetween(sillyNumber, 0, 20)) {
                    playerName = "<gradient:#7402d1:#e31bc5:#7402d1>purple slime</gradient>";
                }
            }
            case "46dd0c5a-2b51-4ee6-80e8-29deca6dedc1" -> {
                if (sillyNumberIsBetween(sillyNumber, 0, 20)) {
                    playerName = "<gradient:#f74040:#FF6600:#f74040>fire demon</gradient>";
                }
                else if (sillyNumberIsBetween(sillyNumber, 69, 69)) {
                    playerName = "<gradient:blue:#b01bd1:blue>best admin</gradient>";
                }
            }
            case "0dc5336b-acd2-4dc3-a5e9-0aa9b8f113f7" -> {
                if (sillyNumberIsBetween(sillyNumber, 0, 20)) {
                    playerName = "<gradient:#f73bdb:#fc8bec:#f73bdb>an UwU sister</gradient>";
                }
            }
            case "10dd9f02-5ec2-4f60-816c-48bb9e2ddf47" -> {
                if (sillyNumberIsBetween(sillyNumber, 0, 20)) {
                    playerName = "<gradient:gold:#fc7f03:-1>gottem</gradient>";
                }
            }
            case "e4c5dfef-bbcc-4012-9f74-879d28fff431" -> {
                if (sillyNumberIsBetween(sillyNumber, 69, 69)) {
                    playerName = "<gradient:blue:#03befc:blue>nice admin</gradient>";
                }
            }
        }
        if (playerName == null) {
            return null;
        } else {
            return MiniMessage.miniMessage().deserialize(playerName);
        }
    }

    private static int getSillyNumber() {
        return random.nextInt(100);
    }

    private static boolean sillyNumberIsBetween(int sillyNumber, int lowerBound, int upperBound) {
        return sillyNumber >= lowerBound && sillyNumber <= upperBound;
    }
}
