package com.github.sandorw.cubetracker.server.decks;

import com.github.sandorw.cubetracker.server.store.atlas.AtlasCubeTrackerSchema;
import com.palantir.atlasdb.persister.JacksonPersister;

/**
 * Atlas (de)serializer for CompleteDeckLists.
 */
public final class CompleteDeckListPersister extends JacksonPersister<CompleteDeckList> {

    public CompleteDeckListPersister() {
        super(CompleteDeckList.class, AtlasCubeTrackerSchema.OBJECT_MAPPER);
    }

}
