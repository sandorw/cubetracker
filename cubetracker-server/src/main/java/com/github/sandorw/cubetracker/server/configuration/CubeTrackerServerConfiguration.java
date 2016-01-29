/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.github.sandorw.cubetracker.server.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Dropwizard configuration class for cubetracker-server.
 */
public final class CubeTrackerServerConfiguration extends Configuration {
    @NotEmpty
    private String mtgJsonFile;

    @JsonProperty
    public String getMtgJsonFile() {
        return mtgJsonFile;
    }

    @JsonProperty
    public void setMtgJsonFile(String file) {
        mtgJsonFile = file;
    }
}
