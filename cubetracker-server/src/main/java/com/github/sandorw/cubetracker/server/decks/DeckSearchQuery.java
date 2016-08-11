package com.github.sandorw.cubetracker.server.decks;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.CardSearchQuery;
import com.github.sandorw.cubetracker.server.decks.filters.ComplexDeckListFilter;
import com.github.sandorw.cubetracker.server.match.filters.ComplexMatchResultFilter;
import com.google.common.base.Optional;
import java.util.List;
import org.immutables.value.Value;

/**
 * Search filter information based on deck properties.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableDeckSearchQuery.class)
@JsonDeserialize(as = ImmutableDeckSearchQuery.class)
public abstract class DeckSearchQuery {

    public abstract Optional<CardSearchQuery> getCardSearchQuery();

    public abstract List<ComplexDeckListFilter> getDeckFilters();

    public abstract List<ComplexMatchResultFilter> getMatchResultFilters();

}
