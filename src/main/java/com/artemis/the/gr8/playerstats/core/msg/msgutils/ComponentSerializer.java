package com.artemis.the.gr8.playerstats.core.msg.msgutils;

import net.kyori.adventure.text.*;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A small utility class for turning PlayerStats' custom Components into String.
 */
public final class ComponentSerializer {

    private final LanguageKeyHandler languageKeyHandler;

    public ComponentSerializer() {
        languageKeyHandler = LanguageKeyHandler.getInstance();
    }

    /**
     * Returns a LegacyComponentSerializer that is capable of serializing
     * TranslatableComponents, and capable of dealing with the custom
     * language-keys I am using to improve the entity-related statistic
     * names. This serializer will create a String with hex colors and styles,
     * and it will turn language keys into prettified, readable English.
     *
     * @return the Serializer
     * @see LanguageKeyHandler
     */
    public @NotNull LegacyComponentSerializer getTranslatableComponentSerializer() {
        LegacyComponentSerializer serializer = getTextComponentSerializer();

        ComponentFlattener flattener = ComponentFlattener.basic().toBuilder()
                .mapper(TranslatableComponent.class, trans -> {
                    StringBuilder totalPrettyName = new StringBuilder();
                    if (LanguageKeyHandler.isCustomKeyForEntityKilledByArg(trans.key())) {
                        return "";
                    }
                    else if (LanguageKeyHandler.isNormalKeyForEntityKilledBy(trans.key()) ||
                            LanguageKeyHandler.isCustomKeyForEntityKilledBy(trans.key()) ||
                            LanguageKeyHandler.isNormalKeyForKillEntity(trans.key()) ||
                            LanguageKeyHandler.isCustomKeyForKillEntity(trans.key())) {

                        TextComponent.Builder temp = Component.text();
                        trans.iterator(ComponentIteratorType.DEPTH_FIRST, ComponentIteratorFlag.INCLUDE_TRANSLATABLE_COMPONENT_ARGUMENTS)
                                .forEachRemaining(component -> {
                                    //copy the style to the temp builder, because the translatable component that follows it has no style itself
                                    if (component instanceof TextComponent text) {
                                        if (!text.children().isEmpty()) {
                                            text.iterator(ComponentIteratorType.DEPTH_FIRST).forEachRemaining(component1 -> {
                                                if (component1 instanceof TextComponent text1 && text1.content().contains("(")) {
                                                    temp.style(text.style()).color(text.color());
                                                }
                                            });
                                        }
                                    }
                                    //isolate the translatable component with the entity inside
                                    else if (component instanceof TranslatableComponent translatable) {
                                        if (LanguageKeyHandler.isEntityKey(translatable.key())) {
                                            temp.append(Component.space())
                                                    .append(Component.text("(")
                                                            .append(Component.text(
                                                                    languageKeyHandler.convertLanguageKeyToDisplayName(translatable.key())))
                                                            .append(Component.text(")")));
                                            totalPrettyName.append(
                                                    serializer.serialize(temp.build()));
                                        }
                                        else if (!LanguageKeyHandler.isCustomKeyForEntityKilledByArg(translatable.key())) {
                                            totalPrettyName.append(
                                                    languageKeyHandler.convertLanguageKeyToDisplayName(
                                                            translatable.key()));
                                        }
                                    }
                                });
                    }
                    else {
                        return languageKeyHandler.convertLanguageKeyToDisplayName(trans.key());
                    }
                    return totalPrettyName.toString();
                })
                .build();

        return serializer.toBuilder().flattener(flattener).build();
    }

    @Contract(" -> new")
    private static @NotNull LegacyComponentSerializer getTextComponentSerializer() {
        return LegacyComponentSerializer
                .builder()
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build();
    }
}