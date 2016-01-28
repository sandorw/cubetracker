/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.github.sandorw.cubetracker.cards;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 *
 */
public enum Color {
    WHITE("White"),
    BLUE("Blue"),
    BLACK("Black"),
    RED("Red"),
    GREEN("Green");

    private String stringRepresentation;

    private Color(String name) {
        stringRepresentation = name;
    }

    @JsonCreator
    public static Color fromString(String color) {
        for (Color enumColor : Color.values()) {
            if (color.equalsIgnoreCase(enumColor.stringRepresentation)) {
                return enumColor;
            }
        }
        return null;
    }
}
