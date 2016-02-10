package com.github.sandorw.cubetracker.server.api;

import com.github.sandorw.cubetracker.server.cards.CardUsageData;
import com.github.sandorw.cubetracker.server.cards.MagicCard;
import com.github.sandorw.cubetracker.server.decks.DeckList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * HTTP specification for cubetracker-server.
 */
@Path("/")
public interface CubeTrackerService {

    /**
     * Returns general card rules information for the specified card.
     */
    @GET
    @Path("/allcards/{cardName}")
    @Produces(MediaType.APPLICATION_JSON)
    MagicCard getMagicCard(@PathParam("cardName") String cardName);

    /**
     * Returns possible matches given a partial card name.
     */
    @GET
    @Path("/allcards/search/{partialCardName}")
    @Produces(MediaType.APPLICATION_JSON)
    List<String> searchAllCardNames(@PathParam("partialCardName") String partialCardName);

    /**
     * Returns possible matches given a partial card name from active cards in the cube.
     */
    @GET
    @Path("/cubecards/active/search/{partialCardName}")
    @Produces(MediaType.APPLICATION_JSON)
    List<String> searchActiveCardNames(@PathParam("partialCardName") String partialCardName);

    /**
     * Returns possible matches given a partial card name from active cards in the cube.
     */
    @GET
    @Path("/cubecards/inactive/search/{partialCardName}")
    @Produces(MediaType.APPLICATION_JSON)
    List<String> searchInactiveCardNames(@PathParam("partialCardName") String partialCardName);

    /**
     * Retrieves card usage data for the given cube card.
     */
    @GET
    @Path("/cubecards/{cardName}")
    @Produces(MediaType.APPLICATION_JSON)
    CardUsageData getCardData(@PathParam("cardName") String cardName);

    /**
     * Adds a new card to the cube as an active card or makes an existing inactive card active.
     */
    @PUT
    @Path("/cubecards/active/{cardName}")
    @Produces(MediaType.APPLICATION_JSON)
    CardUsageData addActiveCard(@PathParam("cardName") String cardName);

    /**
     * Adds a new card to the cube as an inactive card of makes an existing active card inactive.
     */
    @PUT
    @Path("/cubecards/inactive/{cardName}")
    @Produces(MediaType.APPLICATION_JSON)
    CardUsageData addInactiveCard(@PathParam("cardName") String cardName);

    /**
     * Submits a deck list to be stored. Updates card usage for cards in the deck. Returns the deck ID.
     */
    @POST
    @Path("/decks/submit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    String addDeck(DeckList deck);

}
