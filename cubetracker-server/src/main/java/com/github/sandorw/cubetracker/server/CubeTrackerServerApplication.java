/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.github.sandorw.cubetracker.server;

import com.github.sandorw.cubetracker.server.api.CubeTrackerResource;
import com.github.sandorw.cubetracker.server.configuration.CubeTrackerServerConfiguration;
import com.github.sandorw.cubetracker.server.store.CubeTrackerStore;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

/**
 * Dropwizard application for cubetracker-server.
 */
public final class CubeTrackerServerApplication extends Application<CubeTrackerServerConfiguration> {

    public static void main(String[] args) throws Exception {
        new CubeTrackerServerApplication().run(args);
    }

    @Override
    public void run(CubeTrackerServerConfiguration configuration, Environment environment) throws Exception {
        CubeTrackerStore store = new CubeTrackerStore();
        store.loadMagicCardJson(configuration);
        final CubeTrackerResource resource = CubeTrackerResource.of(store);
        environment.jersey().register(resource);
    }

}
