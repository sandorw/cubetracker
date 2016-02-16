package com.github.sandorw.cubetracker.server.match.filters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.filters.NumericFilterType;
import com.github.sandorw.cubetracker.server.cards.filters.SearchFilter;
import com.github.sandorw.cubetracker.server.match.MatchResult;
import java.util.List;
import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

/**
 * Applies a filter based on the win percentage of the deck in matches.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableMatchWinPercentageFilter.class)
@JsonDeserialize(as = ImmutableMatchWinPercentageFilter.class)
public abstract class MatchWinPercentageFilter implements SearchFilter<List<MatchResult>> {

    @Parameter
    public abstract double getFilterValue();

    @Parameter
    public abstract NumericFilterType getFilterType();

    @Override
    public final boolean accept(List<MatchResult> matchList) {
        if (matchList.isEmpty()) {
            return false;
        }
        int numWins = 0;
        for (MatchResult match : matchList) {
            if (match.getFirstDeckWins() > match.getSecondDeckWins()) {
                ++numWins;
            }
        }
        double winPercentage = (double) numWins / matchList.size();
        return getFilterType().accept(winPercentage, getFilterValue());
    }

}
