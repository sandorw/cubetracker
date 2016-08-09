package com.github.sandorw.cubetracker.server.store;

import com.github.sandorw.cubetracker.server.cards.CardSearchQuery;
import com.github.sandorw.cubetracker.server.cards.CardUsageData;
import com.github.sandorw.cubetracker.server.cards.MagicCard;
import com.github.sandorw.cubetracker.server.configuration.CubeTrackerServerConfiguration;
import com.github.sandorw.cubetracker.server.decks.CompleteDeckList;
import com.github.sandorw.cubetracker.server.decks.DeckSearchQuery;
import com.github.sandorw.cubetracker.server.decks.PartialDeckList;
import com.github.sandorw.cubetracker.server.match.MatchResult;
import com.google.common.base.Optional;
import java.util.List;
import java.util.Map;

/**
 * Backing store for cubetracker-server.
 */
public interface CubeTrackerStore {

    void loadMagicCardJson(CubeTrackerServerConfiguration config);

    Optional<MagicCard> getMagicCard(String cardName);

    boolean isValidCard(String cardName);

    List<String> searchAllCardNames(String partialCardName);

    List<String> searchActiveCardNames(String partialCardName);

    List<String> getActiveCardNames();

    List<String> searchInactiveCardNames(String partialCardName);

    List<String> getInactiveCardNames();

    Optional<CardUsageData> getCubeCardData(String cardName);

    CardUsageData addActiveCard(String cardName);

    CardUsageData addInactiveCard(String cardName);

    String addDeck(PartialDeckList deck);

    void addMatchResult(MatchResult match);

    List<MatchResult> getMatchResults(String deckId);

    Optional<CompleteDeckList> getDeck(String deckId);

    Map<MagicCard, CardUsageData> getCardSearchResults(CardSearchQuery query);

    Map<CompleteDeckList, List<MatchResult>> getDeckSearchResults(DeckSearchQuery query);

}
