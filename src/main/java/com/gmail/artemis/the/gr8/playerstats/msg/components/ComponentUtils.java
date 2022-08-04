package com.gmail.artemis.the.gr8.playerstats.msg.components;

import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.LanguageKeyHandler;
import com.gmail.artemis.the.gr8.playerstats.msg.msgutils.StringUtils;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/** A utility class for handling Adventure's Components.
 Its main function is currently to help serialize Components into String.*/
public final class ComponentUtils {

    /** Returns a LegacyComponentSerializer that is capable of serializing TranslatableComponents,
     and capable of dealing with the custom language-keys I am using to improve the entity-related
     statistic names. This serializer will create a String with hex colors and styles, and it will
     turn language keys into prettified, readable English. */
    public static LegacyComponentSerializer getTranslatableComponentSerializer() {
        LegacyComponentSerializer serializer = getTextComponentSerializer();

        ComponentFlattener flattener = ComponentFlattener.basic().toBuilder()
                .mapper(TranslatableComponent.class, trans -> {
                    StringBuilder totalPrettyName = new StringBuilder();
                    if (LanguageKeyHandler.isKeyForEntityKilledByArg(trans.key())) {
                        return "";
                    }
                    else if (LanguageKeyHandler.isKeyForEntityKilledBy(trans.key()) ||
                            LanguageKeyHandler.isKeyForKillEntity(trans.key())) {

                        TextComponent.Builder temp = Component.text();
                        trans.iterator(ComponentIteratorType.DEPTH_FIRST, ComponentIteratorFlag.INCLUDE_TRANSLATABLE_COMPONENT_ARGUMENTS)
                                .forEachRemaining(component -> {
                                    if (component instanceof TextComponent text) {
                                        if (!text.children().isEmpty()) {
                                            text.iterator(ComponentIteratorType.DEPTH_FIRST).forEachRemaining(component1 -> {
                                                if (component1 instanceof TextComponent text1 && text1.content().contains("(")) {
                                                    temp.style(text.style()).color(text.color());
                                                }
                                            });
                                        }
                                    } else if (component instanceof TranslatableComponent translatable) {
                                        if (translatable.key().contains("entity")) {
                                            temp.append(Component.space())
                                                    .append(Component.text("(")
                                                            .append(Component.text(StringUtils.prettify(LanguageKeyHandler.convertToName(translatable.key()))))
                                                            .append(Component.text(")")));
                                            totalPrettyName.append(
                                                    serializer.serialize(temp.build()));
                                        } else if (!LanguageKeyHandler.isKeyForEntityKilledByArg(translatable.key())) {
                                            totalPrettyName.append(
                                                    StringUtils.prettify(
                                                            LanguageKeyHandler.convertToName(
                                                                    translatable.key())));
                                        }
                                    }
                                });
                    }
                    else {
                        return StringUtils.prettify(
                                LanguageKeyHandler.convertToName(
                                        trans.key()));
                    }
                    return totalPrettyName.toString();
                })
                .build();

        return serializer.toBuilder().flattener(flattener).build();
    }

    private static LegacyComponentSerializer getTextComponentSerializer() {
        return LegacyComponentSerializer
                .builder()
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build();
    }
}