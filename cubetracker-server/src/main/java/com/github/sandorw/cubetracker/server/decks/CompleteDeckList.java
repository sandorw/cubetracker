package com.github.sandorw.cubetracker.server.decks;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Representation of a decklist from a cube draft, including deck ID.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableCompleteDeckList.class)
@JsonDeserialize(as = ImmutableCompleteDeckList.class)
public abstract class CompleteDeckList extends DeckList {

    public abstract String getDeckId();

}
