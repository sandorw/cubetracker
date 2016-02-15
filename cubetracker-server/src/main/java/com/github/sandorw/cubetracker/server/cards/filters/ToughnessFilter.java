package com.github.sandorw.cubetracker.server.cards.filters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.MagicCard;
import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

/**
 * Applies a filter based on the toughness of the card.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableToughnessFilter.class)
@JsonDeserialize(as = ImmutableToughnessFilter.class)
public abstract class ToughnessFilter implements SearchFilter<MagicCard> {

    @Parameter
    public abstract Double getToughnessThreshold();

    @Parameter
    public abstract NumericFilterType getFilterType();

    @Override
    public final boolean accept(MagicCard card) {
        if (card.getToughness().isPresent()) {
            try {
                double toughness = Double.parseDouble(card.getToughness().get());
                return getFilterType().accept(toughness, getToughnessThreshold());
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

}
