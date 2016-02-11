package com.github.sandorw.cubetracker.server.store;

import com.github.sandorw.cubetracker.server.cards.CardUsageData;
import com.github.sandorw.cubetracker.server.cards.MagicCard;
import com.github.sandorw.cubetracker.server.configuration.CubeTrackerServerConfiguration;
import com.github.sandorw.cubetracker.server.decks.DeckList;
import com.github.sandorw.cubetracker.server.match.MatchResult;
import com.google.common.base.Optional;
import java.util.List;

/**
 * Backing store for cubetracker-server.
 */
public interface CubeTrackerStore {

    void loadMagicCardJson(CubeTrackerServerConfiguration config);

    Optional<MagicCard> getMagicCard(String cardName);

    boolean isValidCard(String cardName);

    List<String> searchAllCardNames(String partialCardName);

    List<String> searchActiveCardNames(String partialCardName);

    List<String> searchInactiveCardNames(String partialCardName);

    Optional<CardUsageData> getCubeCardData(String cardName);

    CardUsageData addActiveCard(String cardName);

    CardUsageData addInactiveCard(String cardName);

    String addDeck(DeckList deck);

    void addMatchResult(MatchResult match);

    List<MatchResult> getMatchResults(String deckId);

}
