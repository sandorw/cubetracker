package com.github.sandorw.cubetracker.server.cards;

import com.github.sandorw.cubetracker.server.store.atlas.AtlasCubeTrackerSchema;
import com.palantir.atlasdb.persister.JacksonPersister;

/**
 * Atlas (de)serializer for CardUsageData.
 */
public final class CardUsageDataPersister extends JacksonPersister<CardUsageData> {

    public CardUsageDataPersister() {
        super(CardUsageData.class, AtlasCubeTrackerSchema.OBJECT_MAPPER);
    }

}
