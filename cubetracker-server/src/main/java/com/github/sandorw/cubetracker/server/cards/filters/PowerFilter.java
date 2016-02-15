package com.github.sandorw.cubetracker.server.cards.filters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.MagicCard;
import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

/**
 * Applies a filter based on the power of the card.
 */
@Value.Immutable
@JsonSerialize(as = ImmutablePowerFilter.class)
@JsonDeserialize(as = ImmutablePowerFilter.class)
public abstract class PowerFilter implements SearchFilter<MagicCard> {

    @Parameter
    public abstract Double getPowerThreshold();

    @Parameter
    public abstract NumericFilterType getFilterType();

    @Override
    public final boolean accept(MagicCard card) {
        if (card.getPower().isPresent()) {
            try {
                double power = Double.parseDouble(card.getPower().get());
                return getFilterType().accept(power, getPowerThreshold());
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

}
