package com.github.sandorw.cubetracker.server.decks;

import java.util.List;
import org.immutables.value.Value;

/**
 * Representation of a decklist from a cube draft.
 */
@Value.Immutable
public abstract class DeckList {

    public abstract String getPlayerName();

    public abstract List<String> getMaindeck();

    public abstract List<String> getSideboard();

    public abstract int[] getNumBasics();

}
