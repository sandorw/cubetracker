package com.github.sandorw.cubetracker.server.cards.filters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.Color;
import com.github.sandorw.cubetracker.server.cards.MagicCard;
import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

/**
 * Applies a filter based on the color of the card.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableColorFilter.class)
@JsonDeserialize(as = ImmutableColorFilter.class)
public abstract class ColorFilter implements SearchFilter<MagicCard> {

    @Parameter
    public abstract Color getColorFilter();

    @Override
    public final boolean accept(MagicCard card) {
        return card.getColors().contains(getColorFilter());
    }

}
