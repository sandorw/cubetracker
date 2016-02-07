package com.github.sandorw.cubetracker.server.cards;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.UUID;
import org.immutables.value.Value;

/**
 * Metadata class for cards played in the cube, tracking usage statistics and decks they appear in.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableCardUsageData.class)
@JsonDeserialize(as = ImmutableCardUsageData.class)
@SuppressWarnings("checkstyle:designforextension")
public abstract class CardUsageData {

    public abstract boolean getIsActive();

    @Value.Default
    public int getNumWins() {
        return 0;
    }

    @Value.Default
    public int getNumLosses() {
        return 0;
    }

    @Value.Default
    public int getNumMaindecks() {
        return 0;
    }

    @Value.Default
    public int getNumDrafts() {
        return 0;
    }

    @Value.Default
    public int getNumOnColorSideboards() {
        return 0;
    }

    @Value.Default
    public List<UUID> getDeckIDs() {
        return ImmutableList.of();
    }

}
