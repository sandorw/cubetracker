package com.github.sandorw.cubetracker.server.match;

import org.immutables.value.Value;

/**
 * Match result between two decks.
 */
@Value.Immutable
public abstract class MatchResult {

    public abstract String getFirstDeckId();

    public abstract String getSecondDeckId();

    public abstract int getFirstDeckWins();

    public abstract int getSecondDeckWins();

}
