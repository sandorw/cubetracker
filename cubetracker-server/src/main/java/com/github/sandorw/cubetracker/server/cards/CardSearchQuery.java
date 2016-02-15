package com.github.sandorw.cubetracker.server.cards;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.filters.ComplexCardUsageFilter;
import com.github.sandorw.cubetracker.server.cards.filters.ComplexMagicCardFilter;
import java.util.List;
import org.immutables.value.Value;

/**
 * Search filter information based on card properties. Fetches card and usage data.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableCardSearchQuery.class)
@JsonDeserialize(as = ImmutableCardSearchQuery.class)
public abstract class CardSearchQuery {

    public enum StartingSet {
        ALL, ACTIVE, INACTIVE
    }

    public abstract StartingSet getStartingSet();

    public abstract List<ComplexMagicCardFilter> getMagicCardFilters();

    public abstract List<ComplexCardUsageFilter> getCardUsageFilters();

}
