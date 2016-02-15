package com.github.sandorw.cubetracker.server.cards.filters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.MagicCard;
import java.util.List;
import org.immutables.value.Value;

/**
 * Search filter consisting of multiple individual filters combined together.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableComplexMagicCardFilter.class)
@JsonDeserialize(as = ImmutableComplexMagicCardFilter.class)
public abstract class ComplexMagicCardFilter implements SearchFilter<MagicCard> {

    public abstract List<SearchFilter<MagicCard>> getFilterList();

    public abstract FilterCombiner getCombiner();

    @Override
    public final boolean accept(MagicCard card) {
        return getCombiner().accept(getFilterList(), card);
    }

}
