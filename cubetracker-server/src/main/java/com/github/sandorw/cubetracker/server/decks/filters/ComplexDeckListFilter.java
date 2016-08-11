package com.github.sandorw.cubetracker.server.decks.filters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.filters.FilterCombiner;
import com.github.sandorw.cubetracker.server.cards.filters.SearchFilter;
import com.github.sandorw.cubetracker.server.decks.DeckList;
import java.util.List;
import org.immutables.value.Value;

/**
 * Search filter consisting of multiple individual filters combined together.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableComplexDeckListFilter.class)
@JsonDeserialize(as = ImmutableComplexDeckListFilter.class)
public abstract class ComplexDeckListFilter implements SearchFilter<DeckList> {

    public abstract List<SearchFilter<DeckList>> getFilterList();

    public abstract FilterCombiner getCombiner();

    @Override
    public final boolean accept(DeckList deck) {
        return getCombiner().accept(getFilterList(), deck);
    }

}
