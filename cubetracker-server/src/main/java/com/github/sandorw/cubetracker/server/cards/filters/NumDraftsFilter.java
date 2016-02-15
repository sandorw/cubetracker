package com.github.sandorw.cubetracker.server.cards.filters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.CardUsageData;
import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

/**
 * Applies a filter based on the number of drafts of the card.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableNumDraftsFilter.class)
@JsonDeserialize(as = ImmutableNumDraftsFilter.class)
public abstract class NumDraftsFilter implements SearchFilter<CardUsageData> {

    @Parameter
    public abstract int getFilterValue();

    @Parameter
    public abstract NumericFilterType getFilterType();

    @Override
    public final boolean accept(CardUsageData cardData) {
        return getFilterType().accept(cardData.getNumDrafts(), getFilterValue());
    }

}
