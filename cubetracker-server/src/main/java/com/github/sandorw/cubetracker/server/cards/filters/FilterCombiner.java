package com.github.sandorw.cubetracker.server.cards.filters;

import java.util.List;

/**
 * Strategy for combining filters in search queries.
 */
public enum FilterCombiner {
    AND, OR, NOT;

    public <T> boolean accept(List<SearchFilter<T>> filters, T toFilter) {
        switch (this) {
            case AND:
                for (SearchFilter<T> filter : filters) {
                    if (!filter.accept(toFilter)) {
                        return false;
                    }
                }
                return true;
            case OR:
                for (SearchFilter<T> filter : filters) {
                    if (filter.accept(toFilter)) {
                        return true;
                    }
                }
                return false;
            case NOT:
                for (SearchFilter<T> filter : filters) {
                    if (filter.accept(toFilter)) {
                        return false;
                    }
                }
                return true;
            default:
                return false;
        }
    }
}
