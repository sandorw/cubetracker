package com.github.sandorw.cubetracker.server.match.filters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.filters.NumericFilterType;
import com.github.sandorw.cubetracker.server.cards.filters.SearchFilter;
import com.github.sandorw.cubetracker.server.match.MatchResult;
import com.google.common.base.Optional;
import java.util.List;
import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

/**
 * Applies a filter based on the win percentage of the deck in games.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableGameWinPercentageFilter.class)
@JsonDeserialize(as = ImmutableGameWinPercentageFilter.class)
public abstract class GameWinPercentageFilter implements SearchFilter<List<MatchResult>> {

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
        int totalGames = 0;
        for (MatchResult match : matchList) {
            numWins += match.getFirstDeckWins();
            totalGames += match.getFirstDeckWins() + match.getSecondDeckWins();
            Optional<Integer> draws = match.getDraws();
            if (draws.isPresent()) {
                totalGames += draws.get();
            }
        }
        double winPercentage = totalGames > 0 ? (double) numWins / totalGames : 0.0;
        return getFilterType().accept(winPercentage, getFilterValue());
    }

}
