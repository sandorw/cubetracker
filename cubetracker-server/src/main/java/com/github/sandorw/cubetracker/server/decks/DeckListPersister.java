package com.github.sandorw.cubetracker.server.decks;

import com.github.sandorw.cubetracker.server.store.atlas.AtlasCubeTrackerSchema;
import com.palantir.atlasdb.persister.JacksonPersister;

/**
 * Atlas (de)serializer for DeckLists.
 */
public final class DeckListPersister extends JacksonPersister<DeckList> {

    public DeckListPersister() {
        super(DeckList.class, AtlasCubeTrackerSchema.OBJECT_MAPPER);
    }

}
