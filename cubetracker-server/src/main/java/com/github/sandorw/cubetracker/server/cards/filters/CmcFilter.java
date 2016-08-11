package com.github.sandorw.cubetracker.server.cards.filters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.MagicCard;
import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

/**
 * Applies a filter based on converted mana cost of the card.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableCmcFilter.class)
@JsonDeserialize(as = ImmutableCmcFilter.class)
public abstract class CmcFilter implements SearchFilter<MagicCard> {

    @Parameter
    public abstract double getFilterValue();

    @Parameter
    public abstract NumericFilterType getFilterType();

    @Override
    public final boolean accept(MagicCard card) {
        double cmc = card.getCmc().isPresent() ? card.getCmc().get() : 0.0;
        return getFilterType().accept(cmc, getFilterValue());
    }

}
