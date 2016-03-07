package com.github.sandorw.cubetracker.server.decks;

import com.google.common.base.Optional;
import java.util.List;
import org.joda.time.LocalDate;

/**
 * Representation of a decklist from a cube draft.
 */
public abstract class DeckList {

    public abstract Optional<String> getDeckDescription();

    public abstract String getPlayerName();

    public abstract LocalDate getDraftDate();

    public abstract List<String> getMaindeck();

    public abstract List<String> getSideboard();

    public abstract int[] getNumBasics();

}
