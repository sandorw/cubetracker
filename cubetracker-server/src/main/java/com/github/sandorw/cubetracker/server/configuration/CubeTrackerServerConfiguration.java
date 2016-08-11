package com.github.sandorw.cubetracker.server.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.palantir.atlasdb.config.AtlasDbConfig;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Dropwizard configuration class for cubetracker-server.
 */
public final class CubeTrackerServerConfiguration extends Configuration {
    @NotEmpty
    private String mtgJsonFile;
    private final AtlasDbConfig atlas;

    public CubeTrackerServerConfiguration(@JsonProperty("atlas") AtlasDbConfig atlas) {
        this.atlas = atlas;
    }

    public AtlasDbConfig getAtlasConfig() {
        return atlas;
    }

    @JsonProperty
    public String getMtgJsonFile() {
        return mtgJsonFile;
    }

    @JsonProperty
    public void setMtgJsonFile(String file) {
        mtgJsonFile = file;
    }
}
