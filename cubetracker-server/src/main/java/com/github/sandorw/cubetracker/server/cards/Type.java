package com.github.sandorw.cubetracker.server.cards;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Used to note the card types of each card. Other serves as a catch all for a variety of types
 * since the mtgjson project includes Archenemy, Unglued, and other abnormal cards.
 */
public enum Type {
    ARTIFACT("Artifact"),
    CONSPIRACY("Conspiracy"),
    CREATURE("Creature"),
    ENCHANTMENT("Enchantment"),
    INSTANT("Instant"),
    LAND("Land"),
    OTHER("Other"),
    PLANESWALKER("Planeswalker"),
    SORCERY("Sorcery"),
    TRIBAL("Tribal");

    private String stringRepresentation;

    Type(String name) {
        stringRepresentation = name;
    }

    @JsonCreator
    public static Type fromString(String type) {
        for (Type enumType : Type.values()) {
            if (type.equalsIgnoreCase(enumType.stringRepresentation)) {
                return enumType;
            }
        }
        return OTHER;
    }
}
