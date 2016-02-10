package com.github.sandorw.cubetracker.server.api;

import com.github.sandorw.cubetracker.server.cards.CardUsageData;
import com.github.sandorw.cubetracker.server.cards.MagicCard;
import com.github.sandorw.cubetracker.server.decks.DeckList;
import com.github.sandorw.cubetracker.server.store.CubeTrackerStore;
import java.util.List;

/**
 * Implementation of the cubetracker-server api.
 */
public final class CubeTrackerResource implements CubeTrackerService {
    private final CubeTrackerStore store;

    private CubeTrackerResource(CubeTrackerStore store) {
        this.store = store;
    }

    public static CubeTrackerResource of(CubeTrackerStore store) {
        return new CubeTrackerResource(store);
    }

    @Override
    public MagicCard getMagicCard(String cardName) {
        return store.getMagicCard(cardName).orNull();
    }

    @Override
    public List<String> searchAllCardNames(String partialCardName) {
        return store.searchAllCardNames(partialCardName);
    }

    @Override
    public List<String> searchActiveCardNames(String partialCardName) {
        return store.searchActiveCardNames(partialCardName);
    }

    @Override
    public List<String> searchInactiveCardNames(String partialCardName) {
        return store.searchInactiveCardNames(partialCardName);
    }

    @Override
    public CardUsageData getCardData(String cardName) {
        return store.getCubeCardData(cardName).orNull();
    }

    @Override
    public CardUsageData addActiveCard(String cardName) {
        if (store.isValidCard(cardName)) {
            return store.addActiveCard(cardName);
        }
        throw new IllegalArgumentException("Not a valid card");
    }

    @Override
    public CardUsageData addInactiveCard(String cardName) {
        if (store.isValidCard(cardName)) {
            return store.addInactiveCard(cardName);
        }
        throw new IllegalArgumentException("Not a valid card");
    }

    @Override
    public String addDeck(DeckList deck) {
        return store.addDeck(deck);
    }

}
