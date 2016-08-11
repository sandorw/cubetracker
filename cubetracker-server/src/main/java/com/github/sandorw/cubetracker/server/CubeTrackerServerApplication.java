package com.github.sandorw.cubetracker.server;

import com.github.sandorw.cubetracker.server.api.CubeTrackerResource;
import com.github.sandorw.cubetracker.server.configuration.CubeTrackerServerConfiguration;
import com.github.sandorw.cubetracker.server.store.CubeTrackerStore;
import com.github.sandorw.cubetracker.server.store.atlas.AtlasCubeTrackerSchema;
import com.github.sandorw.cubetracker.server.store.atlas.AtlasCubeTrackerStore;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.palantir.atlasdb.factory.TransactionManagers;
import com.palantir.atlasdb.transaction.api.TransactionManager;
import com.palantir.remoting.http.server.ExceptionMappers;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import java.util.EnumSet;
import javax.net.ssl.SSLSocketFactory;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import org.eclipse.jetty.servlets.CrossOriginFilter;

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
        CubeTrackerStore store = new AtlasCubeTrackerStore(transactionManager);
        store.loadMagicCardJson(configuration);
        final CubeTrackerResource resource = CubeTrackerResource.of(store);
        environment.jersey().register(resource);
        ExceptionMappers.visitExceptionMappers(true, environment.jersey()::register);

        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }

}
