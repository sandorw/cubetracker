/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.github.sandorw.cubetracker.server.api;

import com.github.sandorw.cubetracker.server.cards.MagicCard;
import com.github.sandorw.cubetracker.server.store.CubeTrackerStore;

/**
 * Implementation of the cubetracker-server api.
 */
public final class CubeTrackerResource implements CubeTrackerService {
    private final CubeTrackerStore store;

    private CubeTrackerResource(CubeTrackerStore store) {
        this.store = store;
    }

    public static CubeTrackerResource of(CubeTrackerStore store) {
        return new CubeTrackerResource(store);
    }

    @Override
    public MagicCard getMagicCard(String cardName) {
        return store.getMagicCard(cardName).orNull();
    }

}
