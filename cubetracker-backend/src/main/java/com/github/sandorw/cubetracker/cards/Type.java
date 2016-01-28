/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.github.sandorw.cubetracker.cards;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 *
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

    private Type(String name) {
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
