package com.github.sandorw.cubetracker.server.cards.filters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.MagicCard;
import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

/**
 * Applies a filter based on regex matching of the card's name.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableNameFilter.class)
@JsonDeserialize(as = ImmutableNameFilter.class)
public abstract class NameFilter implements SearchFilter<MagicCard> {

    @Parameter
    public abstract String getRegexFilter();

    @Override
    public final boolean accept(MagicCard card) {
        return card.getName().matches(getRegexFilter());
    }

}
