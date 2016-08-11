package com.github.sandorw.cubetracker.server.cards;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Used to note the card types of each card. Other serves as a catch all for a variety of types
 * since the mtgjson project includes Archenemy, Unglued, and other abnormal cards.
 */
public enum CardType {
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

    CardType(String name) {
        stringRepresentation = name;
    }

    @JsonCreator
    public static CardType fromString(String type) {
        for (CardType enumType : CardType.values()) {
            if (type.equalsIgnoreCase(enumType.stringRepresentation)) {
                return enumType;
            }
        }
        return OTHER;
    }
}
