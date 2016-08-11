package com.github.sandorw.cubetracker.server.decks.filters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.filters.SearchFilter;
import com.github.sandorw.cubetracker.server.decks.DeckList;
import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

/**
 * Search filter for player names.
 */
@Value.Immutable
@JsonSerialize(as = ImmutablePlayerNameFilter.class)
@JsonDeserialize(as = ImmutablePlayerNameFilter.class)
public abstract class PlayerNameFilter implements SearchFilter<DeckList> {

    @Parameter
    public abstract String getFilterValue();

    @Override
    public final boolean accept(DeckList deck) {
        return deck.getPlayerName().equals(getFilterValue());
    }

}
