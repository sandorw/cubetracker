package com.github.sandorw.cubetracker.server.cards.filters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.CardUsageData;
import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

/**
 * Applies a filter based on the maindeck percentage of the card.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableMaindeckPercentageFilter.class)
@JsonDeserialize(as = ImmutableMaindeckPercentageFilter.class)
public abstract class MaindeckPercentageFilter implements SearchFilter<CardUsageData> {

    @Parameter
    public abstract double getFilterValue();

    @Parameter
    public abstract NumericFilterType getFilterType();

    @Override
    public final boolean accept(CardUsageData cardData) {
        int totalDrafts = cardData.getNumDrafts();
        double maindeckPercentage =
                totalDrafts > 0 ? (double) cardData.getNumMaindecks() / totalDrafts : 0.0;
        return getFilterType().accept(maindeckPercentage, getFilterValue());
    }

}
