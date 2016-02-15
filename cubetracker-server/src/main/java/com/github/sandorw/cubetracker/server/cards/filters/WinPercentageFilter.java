package com.github.sandorw.cubetracker.server.cards.filters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.CardUsageData;
import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

/**
 * Applies a filter based on the win percentage of the card.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableWinPercentageFilter.class)
@JsonDeserialize(as = ImmutableWinPercentageFilter.class)
public abstract class WinPercentageFilter implements SearchFilter<CardUsageData> {

    @Parameter
    public abstract double getFilterValue();

    @Parameter
    public abstract NumericFilterType getFilterType();

    @Override
    public final boolean accept(CardUsageData cardData) {
        int totalGames = cardData.getNumWins() + cardData.getNumLosses();
        double winPercentage = totalGames > 0 ? (double) cardData.getNumWins() / totalGames : 0.0;
        return getFilterType().accept(winPercentage, getFilterValue());
    }

}
