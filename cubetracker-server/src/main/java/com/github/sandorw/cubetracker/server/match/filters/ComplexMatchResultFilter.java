package com.github.sandorw.cubetracker.server.match.filters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.filters.FilterCombiner;
import com.github.sandorw.cubetracker.server.cards.filters.SearchFilter;
import com.github.sandorw.cubetracker.server.match.MatchResult;
import java.util.List;
import org.immutables.value.Value;

/**
 * Search filter consisting of multiple individual filters combined together.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableComplexMatchResultFilter.class)
@JsonDeserialize(as = ImmutableComplexMatchResultFilter.class)
public abstract class ComplexMatchResultFilter implements SearchFilter<List<MatchResult>> {

    public abstract List<SearchFilter<List<MatchResult>>> getFilterList();

    public abstract FilterCombiner getCombiner();

    @Override
    public final boolean accept(List<MatchResult> matchList) {
        return getCombiner().accept(getFilterList(), matchList);
    }

}
