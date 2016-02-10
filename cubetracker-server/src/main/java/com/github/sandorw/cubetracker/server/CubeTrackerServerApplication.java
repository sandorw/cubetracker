package com.github.sandorw.cubetracker.server;

import com.github.sandorw.cubetracker.server.api.CubeTrackerResource;
import com.github.sandorw.cubetracker.server.configuration.CubeTrackerServerConfiguration;
import com.github.sandorw.cubetracker.server.store.CubeTrackerStore;
import com.github.sandorw.cubetracker.server.store.atlas.AtlasCubeTrackerSchema;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.palantir.atlasdb.factory.TransactionManagers;
import com.palantir.atlasdb.transaction.api.TransactionManager;
import com.palantir.remoting.http.server.ExceptionMappers;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import javax.net.ssl.SSLSocketFactory;

/**
 * Dropwizard application for cubetracker-server.
 */
public final class CubeTrackerServerApplication extends Application<CubeTrackerServerConfiguration> {

    public static void main(String[] args) throws Exception {
        new CubeTrackerServerApplication().run(args);
    }

    @Override
    public void run(CubeTrackerServerConfiguration configuration, Environment environment) throws Exception {
        TransactionManager transactionManager = TransactionManagers.create(
                configuration.getAtlasConfig(),
                Optional.<SSLSocketFactory>absent(),
                ImmutableSet.of(AtlasCubeTrackerSchema.SCHEMA),
                environment.jersey()::register);
        CubeTrackerStore store = new CubeTrackerStore(transactionManager);
        store.loadMagicCardJson(configuration);
        final CubeTrackerResource resource = CubeTrackerResource.of(store);
        environment.jersey().register(resource);
        ExceptionMappers.visitExceptionMappers(true, environment.jersey()::register);
    }

}
