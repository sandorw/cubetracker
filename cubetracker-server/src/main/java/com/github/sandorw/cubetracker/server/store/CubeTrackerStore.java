/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.github.sandorw.cubetracker.server.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.github.sandorw.cubetracker.server.cards.MagicCard;
import com.github.sandorw.cubetracker.server.configuration.CubeTrackerServerConfiguration;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Backing store for cubetracker-server.
 */
public final class CubeTrackerStore {
    private volatile Map<String, MagicCard> magicCardMap;

    public CubeTrackerStore() {
        magicCardMap = Maps.newTreeMap();
    }

    public void loadMagicCardJson(CubeTrackerServerConfiguration config) {
        String mtgJsonFile = config.getMtgJsonFile();
        ObjectMapper mapper = new ObjectMapper().registerModule(new GuavaModule());
        try {
            magicCardMap = mapper.readValue(new File(mtgJsonFile),
                    new TypeReference<Map<String, MagicCard>>() { });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<MagicCard> getMagicCard(String cardName) {
        return Optional.of(magicCardMap.get(cardName));
    }

}
