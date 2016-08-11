package com.github.sandorw.cubetracker.server.cards.filters;

/**
 * Enum determining how to compare numeric values in a filter.
 */
public enum NumericFilterType {
    OVER, UNDER, EXACT;

    public boolean accept(Number value, Number target) {
        switch (this) {
            case OVER:
                return value.doubleValue() > target.doubleValue();
            case UNDER:
                return value.doubleValue() < target.doubleValue();
            case EXACT:
                return value.doubleValue() == target.doubleValue();
            default:
                return false;
        }
    }

}
