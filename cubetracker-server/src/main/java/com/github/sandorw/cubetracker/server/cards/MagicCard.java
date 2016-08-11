package com.github.sandorw.cubetracker.server.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Optional;
import java.util.List;
import org.immutables.value.Value;

/**
 * Relevant magic card properties as read from the mtgjson project.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableMagicCard.class)
@JsonDeserialize(as = ImmutableMagicCard.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class MagicCard {

    public abstract String getName();

    public abstract Optional<String> getManaCost();

    public abstract Optional<Double> getCmc();

    public abstract List<Color> getColors();

    @JsonProperty("type")
    public abstract String getTypeInfo();

    public abstract List<CardType> getTypes();

    public abstract Optional<String> getText();

    public abstract Optional<String> getPower();

    public abstract Optional<String> getToughness();

    public abstract Optional<Integer> getLoyalty();

}
