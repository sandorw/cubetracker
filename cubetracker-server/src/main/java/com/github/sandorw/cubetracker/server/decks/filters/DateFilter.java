package com.github.sandorw.cubetracker.server.decks.filters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.filters.NumericFilterType;
import com.github.sandorw.cubetracker.server.cards.filters.SearchFilter;
import com.github.sandorw.cubetracker.server.decks.DeckList;
import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;
import org.joda.time.LocalDate;

/**
 * Search filter for draft dates.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableDateFilter.class)
@JsonDeserialize(as = ImmutableDateFilter.class)
public abstract class DateFilter implements SearchFilter<DeckList> {

    @Parameter
    public abstract LocalDate getFilterValue();

    @Parameter
    public abstract NumericFilterType getFilterType();

    @Override
    public final boolean accept(DeckList deck) {
        switch (getFilterType()) {
            case OVER:
                return deck.getDraftDate().isAfter(getFilterValue());
            case UNDER:
                return deck.getDraftDate().isBefore(getFilterValue());
            case EXACT:
                return deck.getDraftDate().equals(getFilterValue());
            default:
                return false;
        }
    }

}
