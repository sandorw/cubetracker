package com.github.sandorw.cubetracker.server.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    public abstract String name();

    public abstract Optional<String> manaCost();

    public abstract Optional<Double> cmc();

    public abstract List<Color> colors();

    public abstract String type();

    public abstract List<Type> types();

    public abstract Optional<String> text();

}