package com.github.sandorw.cubetracker.server.decks;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Representation of a decklist from a cube draft, missing a deck ID.
 */
@Value.Immutable
@JsonSerialize(as = ImmutablePartialDeckList.class)
@JsonDeserialize(as = ImmutablePartialDeckList.class)
public abstract class PartialDeckList extends DeckList {

}
