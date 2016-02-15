package com.github.sandorw.cubetracker.server.cards.filters;

/**
 * Interface for search filters.
 */
public interface SearchFilter<T> {

    boolean accept(T type);

}
