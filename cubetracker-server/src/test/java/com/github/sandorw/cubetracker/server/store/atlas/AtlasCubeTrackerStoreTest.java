package com.github.sandorw.cubetracker.server.store.atlas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.github.sandorw.cubetracker.server.cards.CardSearchQuery;
import com.github.sandorw.cubetracker.server.cards.CardSearchQuery.StartingSet;
import com.github.sandorw.cubetracker.server.cards.CardType;
import com.github.sandorw.cubetracker.server.cards.CardUsageData;
import com.github.sandorw.cubetracker.server.cards.Color;
import com.github.sandorw.cubetracker.server.cards.ImmutableCardSearchQuery;
import com.github.sandorw.cubetracker.server.cards.ImmutableMagicCard;
import com.github.sandorw.cubetracker.server.cards.MagicCard;
import com.github.sandorw.cubetracker.server.cards.filters.CmcFilter;
import com.github.sandorw.cubetracker.server.cards.filters.ColorFilter;
import com.github.sandorw.cubetracker.server.cards.filters.ComplexCardUsageFilter;
import com.github.sandorw.cubetracker.server.cards.filters.ComplexMagicCardFilter;
import com.github.sandorw.cubetracker.server.cards.filters.FilterCombiner;
import com.github.sandorw.cubetracker.server.cards.filters.ImmutableCmcFilter;
import com.github.sandorw.cubetracker.server.cards.filters.ImmutableColorFilter;
import com.github.sandorw.cubetracker.server.cards.filters.ImmutableComplexCardUsageFilter;
import com.github.sandorw.cubetracker.server.cards.filters.ImmutableComplexMagicCardFilter;
import com.github.sandorw.cubetracker.server.cards.filters.ImmutableNumDraftsFilter;
import com.github.sandorw.cubetracker.server.cards.filters.ImmutableTypeFilter;
import com.github.sandorw.cubetracker.server.cards.filters.NumDraftsFilter;
import com.github.sandorw.cubetracker.server.cards.filters.NumericFilterType;
import com.github.sandorw.cubetracker.server.cards.filters.TypeFilter;
import com.github.sandorw.cubetracker.server.configuration.CubeTrackerServerConfiguration;
import com.github.sandorw.cubetracker.server.decks.DeckList;
import com.github.sandorw.cubetracker.server.decks.ImmutableDeckList;
import com.github.sandorw.cubetracker.server.match.ImmutableMatchResult;
import com.github.sandorw.cubetracker.server.match.MatchResult;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.palantir.atlasdb.memory.InMemoryAtlasDbFactory;
import com.palantir.atlasdb.transaction.impl.SerializableTransactionManager;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * AtlasCubeTrackerStore test cases.
 */
public final class AtlasCubeTrackerStoreTest {
    private AtlasCubeTrackerStore store;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void before() {
        SerializableTransactionManager txnManager =
                InMemoryAtlasDbFactory.createInMemoryTransactionManager(AtlasCubeTrackerSchema.INSTANCE);
        store = new AtlasCubeTrackerStore(txnManager);
        CubeTrackerServerConfiguration config = new CubeTrackerServerConfiguration(null);
        config.setMtgJsonFile(this.getClass().getResource("testcards.json").getPath());
        store.loadMagicCardJson(config);
    }

    @Test
    public void getMagicCard_validCard() {
        Optional<MagicCard> card = store.getMagicCard("Gloom");
        assertTrue(card.isPresent());
        MagicCard gloomCard = ImmutableMagicCard.builder()
                .name("Gloom")
                .manaCost("{2}{B}")
                .cmc(3.0)
                .colors(ImmutableList.of(Color.BLACK))
                .typeInfo("Enchantment")
                .types(ImmutableList.of(CardType.ENCHANTMENT))
                .text("White spells cost {3} more to cast.\n"
                        + "Activated abilities of white enchantments cost {3} more to activate.")
                .build();
        assertEquals(card.get(), gloomCard);
    }

    @Test
    public void getMagicCard_invalidCard() {
        Optional<MagicCard> card = store.getMagicCard("Thragtusk");
        assertFalse(card.isPresent());
    }

    @Test
    public void isValidCard_validCard() {
        assertTrue(store.isValidCard("Gloom"));
    }

    @Test
    public void isValidCard_invalidCard() {
        assertFalse(store.isValidCard("Thragtusk"));
    }

    @Test
    public void searchAllCardNames_autocompleteMatches() {
        assertEquals(store.searchAllCardNames("Gl"),
                ImmutableList.of("Glasses of Urza", "Gloom"));
    }

    @Test
    public void addActiveCard_autocompleteMatches() {
        store.addActiveCard("Gloom");
        assertEquals(store.searchActiveCardNames("Gl"),
                ImmutableList.of("Gloom"));
    }

    @Test
    public void addActiveCard_replaceInactiveCard() {
        store.addInactiveCard("Gloom");
        String deckId = addValidOneCardDeck("Gloom");
        assertEquals(store.searchInactiveCardNames("Gl"),
                ImmutableList.of("Gloom"));
        store.addActiveCard("Gloom");
        assertEquals(store.searchInactiveCardNames("Gl"),
                ImmutableList.of());
        assertEquals(store.searchActiveCardNames("Gl"),
                ImmutableList.of("Gloom"));
        CardUsageData cardData = store.getCubeCardData("Gloom").get();
        assertEquals(cardData.getIsActive(), true);
        assertEquals(cardData.getNumWins(), 0);
        assertEquals(cardData.getNumLosses(), 0);
        assertEquals(cardData.getNumMaindecks(), 1);
        assertEquals(cardData.getNumDrafts(), 1);
        assertEquals(cardData.getDeckIDs().size(), 1);
        assertTrue(cardData.getDeckIDs().contains(deckId));
    }

    @Test
    public void addDeck_notEnoughCards() {
        int[] basics = {0, 0, 0, 0, 0};
        DeckList deck = ImmutableDeckList.builder()
                .playerName("test")
                .addMaindeck("Gloom")
                .numBasics(basics)
                .build();
        exception.expect(IllegalArgumentException.class);
        store.addDeck(deck);
    }

    @Test
    public void addDeck_duplicateCards() {
        int[] basics = {39, 0, 0, 0, 0};
        DeckList deck = ImmutableDeckList.builder()
                .playerName("test")
                .addMaindeck("Gloom")
                .addSideboard("Gloom")
                .numBasics(basics)
                .build();
        exception.expect(IllegalArgumentException.class);
        store.addDeck(deck);
    }

    @Test
    public void addDeck_getDeck() {
        store.addActiveCard("Gloom");
        String deckId = addValidOneCardDeck("Gloom");
        assertNotNull(deckId);
        DeckList gloomDeck = store.getDeck(deckId).get();
        assertEquals(gloomDeck.getMaindeck(), ImmutableList.of("Gloom"));
        assertEquals(gloomDeck.getSideboard(), ImmutableList.of());
        assertEquals(gloomDeck.getPlayerName(), "test");
        assertEquals(gloomDeck.getNumBasics()[0], 39);
    }

    private String addValidOneCardDeck(String cardName) {
        int[] basics = {39, 0, 0, 0, 0};
        DeckList deck = ImmutableDeckList.builder()
                .playerName("test")
                .addMaindeck(cardName)
                .numBasics(basics)
                .build();
        return store.addDeck(deck);
    }

    @Test
    public void addMatchResult_updatesCardData() {
        store.addActiveCard("Gloom");
        String gloomId = addValidOneCardDeck("Gloom");
        store.addInactiveCard("Black Lotus");
        String lotusId = addValidOneCardDeck("Black Lotus");
        MatchResult matchResult = ImmutableMatchResult.builder()
                .firstDeckId(gloomId)
                .secondDeckId(lotusId)
                .firstDeckWins(1)
                .secondDeckWins(2)
                .build();
        store.addMatchResult(matchResult);
        CardUsageData gloomData = store.getCubeCardData("Gloom").get();
        assertEquals(gloomData.getNumWins(), 1);
        assertEquals(gloomData.getNumLosses(), 2);
        CardUsageData lotusData = store.getCubeCardData("Black Lotus").get();
        assertEquals(lotusData.getNumWins(), 2);
        assertEquals(lotusData.getNumLosses(), 1);
        MatchResult retrievedMatchResult = store.getMatchResults(gloomId).get(0);
        assertEquals(matchResult, retrievedMatchResult);
    }

    @Test
    public void addMatchResult_replacementMatchResult() {
        store.addActiveCard("Gloom");
        String gloomId = addValidOneCardDeck("Gloom");
        store.addInactiveCard("Black Lotus");
        String lotusId = addValidOneCardDeck("Black Lotus");
        MatchResult matchResult = ImmutableMatchResult.builder()
                .firstDeckId(gloomId)
                .secondDeckId(lotusId)
                .firstDeckWins(1)
                .secondDeckWins(2)
                .build();
        store.addMatchResult(matchResult);
        matchResult = ImmutableMatchResult.builder()
                .firstDeckId(gloomId)
                .secondDeckId(lotusId)
                .firstDeckWins(4)
                .secondDeckWins(0)
                .build();
        store.addMatchResult(matchResult);
        CardUsageData gloomData = store.getCubeCardData("Gloom").get();
        assertEquals(gloomData.getNumWins(), 4);
        assertEquals(gloomData.getNumLosses(), 0);
        CardUsageData lotusData = store.getCubeCardData("Black Lotus").get();
        assertEquals(lotusData.getNumWins(), 0);
        assertEquals(lotusData.getNumLosses(), 4);
        MatchResult retrievedMatchResult = store.getMatchResults(gloomId).get(0);
        assertEquals(matchResult, retrievedMatchResult);
    }

    @Test
    public void getCardSearchResults_validateSearchResults() {
        store.searchAllCardNames("").stream().forEach(store::addActiveCard);
        addValidOneCardDeck("Animate Dead");
        addValidOneCardDeck("Fear");
        TypeFilter enchantmentFilter = ImmutableTypeFilter.of(CardType.ENCHANTMENT);
        ColorFilter blackFilter = ImmutableColorFilter.of(Color.BLACK);
        CmcFilter lowCostFilter = ImmutableCmcFilter.of(3.0, NumericFilterType.UNDER);
        ComplexMagicCardFilter cardFilter = ImmutableComplexMagicCardFilter.builder()
                .addFilterList(enchantmentFilter)
                .addFilterList(blackFilter)
                .addFilterList(lowCostFilter)
                .combiner(FilterCombiner.AND)
                .build();
        NumDraftsFilter hasBeenDrafted = ImmutableNumDraftsFilter.of(0, NumericFilterType.EXACT);
        ComplexCardUsageFilter usageFilter = ImmutableComplexCardUsageFilter.builder()
                .addFilterList(hasBeenDrafted)
                .combiner(FilterCombiner.NOT)
                .build();
        CardSearchQuery cardQuery = ImmutableCardSearchQuery.builder()
                .startingSet(StartingSet.ACTIVE)
                .addMagicCardFilters(cardFilter)
                .addCardUsageFilters(usageFilter)
                .build();
        Map<MagicCard, CardUsageData> searchResults = store.getCardSearchResults(cardQuery);
        Set<String> searchCardNames = searchResults.keySet().stream()
                .map(card -> card.getName())
                .collect(Collectors.toSet());
        Set<String> expectedNames = ImmutableSet.of("Animate Dead", "Fear");
        assertTrue(searchCardNames.containsAll(expectedNames));
    }

}
