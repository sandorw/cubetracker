/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.github.sandorw.cubetracker.server.api;

import com.github.sandorw.cubetracker.server.cards.MagicCard;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * HTTP specification for cubetracker-server.
 */
@Path("/")
public interface CubeTrackerService {

    @GET
    @Path("/cards/{cardName}")
    @Produces(MediaType.APPLICATION_JSON)
    MagicCard getMagicCard(@PathParam("cardName") String cardName);
}
