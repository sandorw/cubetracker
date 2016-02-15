package com.github.sandorw.cubetracker.server.cards.filters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.CardType;
import com.github.sandorw.cubetracker.server.cards.MagicCard;
import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

/**
 * Applies a filter based on the types of the card.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableTypeFilter.class)
@JsonDeserialize(as = ImmutableTypeFilter.class)
public abstract class TypeFilter implements SearchFilter<MagicCard> {

    @Parameter
    public abstract CardType getTypeFilter();

    @Override
    public final boolean accept(MagicCard card) {
        return card.getTypes().contains(getTypeFilter());
    }

}
