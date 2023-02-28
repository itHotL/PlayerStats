package com.artemis.the.gr8.playerstats.core.msg.msgutils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * This class is just for fun and adds some silly names for
 * players on my server. It does not impact the rest of the plugin,
 * and will only be used for the players mentioned in here.
 */
public final class EasterEggProvider {

    private static final Random random;

    static {
        random = new Random();
    }

    public static @Nullable Component getPlayerName(@NotNull Player player) {
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
                if (sillyNumberIsBetween(sillyNumber, 0, 100)) {
                    playerName = "<gradient:#f73bdb:#fc8bec:#f73bdb>an UwU sister</gradient>";
                }
            }
            case "10dd9f02-5ec2-4f60-816c-48bb9e2ddf47" -> {
                if (sillyNumberIsBetween(sillyNumber, 0, 20)) {
                    playerName = "<gradient:gold:#fc7f03:-1>gottem</gradient>";
                }
            }
            case "e4c5dfef-bbcc-4012-9f74-879d28fff431" -> {
                if (sillyNumberIsBetween(sillyNumber, 0, 15)) {
                    playerName = "<gradient:blue:#03befc:blue>big bad admin</gradient>";
                }
                else if (sillyNumberIsBetween(sillyNumber, 15, 20)) {
                    playerName = "<gradient:#03b6fc:#f73bdb>zombie fucker</gradient>";
                }
            }
            case "29c0911d-695a-4c31-817f-3a065a7144b7" -> {
                if (sillyNumberIsBetween(sillyNumber, 0, 20)) {
                    playerName = "<gradient:gold:#00ff7b:#03b6fc>Tzzzzzzzzz</gradient>";
                }
            }
            case "0410f9c7-f042-479c-ac80-49d46be655e9" -> {
                if (sillyNumberIsBetween(sillyNumber, 0, 20)) {
                    playerName = "<gradient:gold:#ff245e:#a511f0:#7c0aff>SamanthaCation</gradient>";
                }
            }
            case "0bd803b6-f6c2-41bd-9872-74d8754a29fd" -> {
                if (sillyNumberIsBetween(sillyNumber, 0, 20)) {
                    playerName = "<gradient:#14f7a0:#4287f5>Bradwurst</gradient>";
                }
            }
            case "de8891b3-ab99-4e63-934f-1a5571c42057" -> {
                if (sillyNumberIsBetween(sillyNumber, 0, 10)) {
                    playerName = "<gradient:#00ff7b:#03befc:blue>JahWeeeeee</gradient>";
                }
                else if (sillyNumberIsBetween(sillyNumber, 10, 20)) {
                    playerName = "<gradient:gold:#00ff7b:#03b6fc>he plays guitar I dunno - Tz</gradient>";
                }
            }
        }
        if (playerName == null) {
            return null;
        } else {
            return MiniMessage.miniMessage().deserialize(playerName, papiTag(player));
        }
    }

    private static int getSillyNumber() {
        return random.nextInt(100);
    }

    private static boolean sillyNumberIsBetween(int sillyNumber, int lowerBound, int upperBound) {
        return sillyNumber >= lowerBound && sillyNumber <= upperBound;
    }

    @Contract("_ -> new")
    private static @NotNull TagResolver papiTag(final @NotNull Player player) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            final String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();
            final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + papiPlaceholder + '%');
            TextComponent componentPlaceholder = LegacyComponentSerializer.legacyAmpersand().deserialize(parsedPlaceholder);
            if (!componentPlaceholder.content().isEmpty()) {
                componentPlaceholder = componentPlaceholder.toBuilder().append(Component.space()).build();
            }
            return Tag.selfClosingInserting(componentPlaceholder);
        });
    }
}