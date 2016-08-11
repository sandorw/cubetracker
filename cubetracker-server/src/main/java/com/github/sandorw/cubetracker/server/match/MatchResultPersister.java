package com.github.sandorw.cubetracker.server.match;

import com.github.sandorw.cubetracker.server.store.atlas.AtlasCubeTrackerSchema;
import com.palantir.atlasdb.persister.JacksonPersister;

/**
 * Atlas (de)serializer for MatchResults.
 */
public class MatchResultPersister extends JacksonPersister<MatchResult> {

    public MatchResultPersister() {
        super(MatchResult.class, AtlasCubeTrackerSchema.OBJECT_MAPPER);
    }

}
