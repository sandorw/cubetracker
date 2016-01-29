package com.github.sandorw.cubetracker.server.cards;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Used to note the color or colors of a card. Colorless cards have no colors.
 */
public enum Color {
    WHITE("White"),
    BLUE("Blue"),
    BLACK("Black"),
    RED("Red"),
    GREEN("Green");

    private String stringRepresentation;

    Color(String name) {
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
