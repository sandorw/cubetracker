package com.github.sandorw.cubetracker.server.decks;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value;

/**
 * Representation of a decklist from a cube draft.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableDeckList.class)
@JsonDeserialize(as = ImmutableDeckList.class)
public abstract class DeckList {

    public abstract String getPlayerName();

    public abstract List<String> getMaindeck();

    public abstract List<String> getSideboard();

    public abstract int[] getNumBasics();

}
