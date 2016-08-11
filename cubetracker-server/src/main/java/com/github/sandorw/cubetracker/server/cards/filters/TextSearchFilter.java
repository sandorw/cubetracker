package com.github.sandorw.cubetracker.server.cards.filters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sandorw.cubetracker.server.cards.MagicCard;
import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

/**
 * Applies a filter based on searching the text of the card.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableTextSearchFilter.class)
@JsonDeserialize(as = ImmutableTextSearchFilter.class)
public abstract class TextSearchFilter implements SearchFilter<MagicCard> {

    @Parameter
    public abstract String getTextSearch();

    @Override
    public final boolean accept(MagicCard card) {
        if (card.getName().contains(getTextSearch())) {
            return true;
        }
        if (card.getTypeInfo().contains(getTextSearch())) {
            return true;
        }
        if (card.getText().isPresent()
                && card.getText().get().contains(getTextSearch())) {
            return true;
        }
        return false;
    }

}
