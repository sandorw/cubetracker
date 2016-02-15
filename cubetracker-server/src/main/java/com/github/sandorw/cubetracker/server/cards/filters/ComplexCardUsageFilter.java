package com.github.sandorw.cubetracker.server.cards.filters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.CardUsageData;
import java.util.List;
import org.immutables.value.Value;

/**
 * Search filter consisting of multiple individual filters combined together.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableComplexCardUsageFilter.class)
@JsonDeserialize(as = ImmutableComplexCardUsageFilter.class)
public abstract class ComplexCardUsageFilter implements SearchFilter<CardUsageData> {

    public abstract List<SearchFilter<CardUsageData>> getFilterList();

    public abstract FilterCombiner getCombiner();

    @Override
    public final boolean accept(CardUsageData cardData) {
        return getCombiner().accept(getFilterList(), cardData);
    }

}
