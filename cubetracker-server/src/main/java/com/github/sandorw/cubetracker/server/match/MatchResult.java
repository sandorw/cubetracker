package com.github.sandorw.cubetracker.server.match;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Optional;
import org.immutables.value.Value;

/**
 * Match result between two decks.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableMatchResult.class)
@JsonDeserialize(as = ImmutableMatchResult.class)
public abstract class MatchResult {

    public abstract String getFirstDeckId();

    public abstract String getSecondDeckId();

    public abstract int getFirstDeckWins();

    public abstract int getSecondDeckWins();

    public abstract Optional<Integer> getDraws();

}
