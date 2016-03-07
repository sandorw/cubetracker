package com.github.sandorw.cubetracker.server.api;

import com.github.sandorw.cubetracker.server.cards.CardSearchQuery;
import com.github.sandorw.cubetracker.server.cards.CardUsageData;
import com.github.sandorw.cubetracker.server.cards.MagicCard;
import com.github.sandorw.cubetracker.server.decks.CompleteDeckList;
import com.github.sandorw.cubetracker.server.decks.DeckSearchQuery;
import com.github.sandorw.cubetracker.server.decks.PartialDeckList;
import com.github.sandorw.cubetracker.server.match.MatchResult;
import com.github.sandorw.cubetracker.server.store.CubeTrackerStore;
import com.google.common.base.Optional;
import java.util.List;
import java.util.Map;

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
    public String addDeck(PartialDeckList deck) {
        return store.addDeck(deck);
    }

    @Override
    public void addMatchResult(MatchResult match) {
        store.addMatchResult(match);
    }

    @Override
    public List<MatchResult> getMatchResults(String deckId) {
        return store.getMatchResults(deckId);
    }

    @Override
    public CompleteDeckList getDeck(String deckId) {
        Optional<CompleteDeckList> deck = store.getDeck(deckId);
        if (deck.isPresent()) {
            return deck.get();
        }
        throw new IllegalArgumentException("Not a valid deck Id");
    }

    @Override
    public Map<MagicCard, CardUsageData> getCardSearchResults(CardSearchQuery query) {
        return store.getCardSearchResults(query);
    }

    @Override
    public Map<CompleteDeckList, List<MatchResult>> getDeckSearchResults(DeckSearchQuery query) {
        return store.getDeckSearchResults(query);
    }

}
