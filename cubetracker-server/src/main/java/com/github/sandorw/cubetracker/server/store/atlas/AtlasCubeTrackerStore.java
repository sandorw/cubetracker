package com.github.sandorw.cubetracker.server.store.atlas;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.github.sandorw.cubetracker.server.cards.CardSearchQuery;
import com.github.sandorw.cubetracker.server.cards.CardUsageData;
import com.github.sandorw.cubetracker.server.cards.ImmutableCardUsageData;
import com.github.sandorw.cubetracker.server.cards.MagicCard;
import com.github.sandorw.cubetracker.server.cards.filters.ComplexCardUsageFilter;
import com.github.sandorw.cubetracker.server.cards.filters.ComplexMagicCardFilter;
import com.github.sandorw.cubetracker.server.configuration.CubeTrackerServerConfiguration;
import com.github.sandorw.cubetracker.server.decks.CompleteDeckList;
import com.github.sandorw.cubetracker.server.decks.DeckList;
import com.github.sandorw.cubetracker.server.decks.DeckSearchQuery;
import com.github.sandorw.cubetracker.server.decks.ImmutableCompleteDeckList;
import com.github.sandorw.cubetracker.server.decks.PartialDeckList;
import com.github.sandorw.cubetracker.server.decks.filters.ComplexDeckListFilter;
import com.github.sandorw.cubetracker.server.match.ImmutableMatchResult;
import com.github.sandorw.cubetracker.server.match.MatchResult;
import com.github.sandorw.cubetracker.server.match.filters.ComplexMatchResultFilter;
import com.github.sandorw.cubetracker.server.store.CubeTrackerStore;
import com.github.sandorw.cubetracker.server.store.atlas.generated.CubeCardsTable;
import com.github.sandorw.cubetracker.server.store.atlas.generated.CubeCardsTable.CubeCardsRow;
import com.github.sandorw.cubetracker.server.store.atlas.generated.CubeDecksTable;
import com.github.sandorw.cubetracker.server.store.atlas.generated.CubeDecksTable.CubeDecksRowResult;
import com.github.sandorw.cubetracker.server.store.atlas.generated.CubeMatchesTable;
import com.github.sandorw.cubetracker.server.store.atlas.generated.CubeTrackerStoreTableFactory;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.palantir.atlasdb.keyvalue.api.RangeRequest;
import com.palantir.atlasdb.transaction.api.Transaction;
import com.palantir.atlasdb.transaction.api.TransactionManager;
import com.palantir.common.base.BatchingVisitableView;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import jersey.repackaged.com.google.common.collect.Lists;

/**
 * Atlas implementation of the CubeTracker data store.
 */
public final class AtlasCubeTrackerStore implements CubeTrackerStore {
    private volatile Map<String, MagicCard> magicCardMap;
    private volatile Set<String> allCardNames;
    private volatile Set<String> activeCardNames;
    private volatile Set<String> inactiveCardNames;
    private final TransactionManager txnManager;
    private static final CubeTrackerStoreTableFactory TABLES = CubeTrackerStoreTableFactory.of();

    public AtlasCubeTrackerStore(TransactionManager txnManager) {
        magicCardMap = Maps.newTreeMap();
        this.txnManager = txnManager;
        allCardNames = Sets.newHashSet();
        activeCardNames = Sets.newConcurrentHashSet();
        inactiveCardNames = Sets.newConcurrentHashSet();
        Set<String> activeNames =
                txnManager.runTaskReadOnly(atlasTransaction -> {
                    CubeCardsTable cubeCardsTable = TABLES.getCubeCardsTable(atlasTransaction);
                    return cubeCardsTable
                            .getAllRowsUnordered()
                            .filter(row -> row.getCardUsage().getIsActive())
                            .transform(row -> row.getRowName().getCardName())
                            .immutableSetCopy();
                });
        activeCardNames.addAll(activeNames);
        Set<String> inactiveNames =
                txnManager.runTaskReadOnly(atlasTransaction -> {
                    CubeCardsTable cubeCardsTable = TABLES.getCubeCardsTable(atlasTransaction);
                    return cubeCardsTable
                            .getAllRowsUnordered()
                            .filter(row -> !row.getCardUsage().getIsActive())
                            .transform(row -> row.getRowName().getCardName())
                            .immutableSetCopy();
                });
        inactiveCardNames.addAll(inactiveNames);
    }

    @Override
    public void loadMagicCardJson(CubeTrackerServerConfiguration config) {
        String mtgJsonFile = config.getMtgJsonFile();
        ObjectMapper mapper = new ObjectMapper().registerModule(new GuavaModule());
        try {
            magicCardMap = mapper.readValue(new File(mtgJsonFile),
                    new TypeReference<Map<String, MagicCard>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        allCardNames.addAll(magicCardMap.keySet());
    }

    @Override
    public Optional<MagicCard> getMagicCard(String cardName) {
        MagicCard card = magicCardMap.get(cardName);
        if (card == null) {
            return Optional.absent();
        }
        return Optional.of(card);
    }

    @Override
    public boolean isValidCard(String cardName) {
        return magicCardMap.containsKey(cardName);
    }

    @Override
    public List<String> searchAllCardNames(String partialCardName) {
        return searchCardNames(partialCardName, allCardNames);
    }

    @Override
    public List<String> searchActiveCardNames(String partialCardName) {
        return searchCardNames(partialCardName, activeCardNames);
    }

    @Override
    public List<String> searchInactiveCardNames(String partialCardName) {
        return searchCardNames(partialCardName, inactiveCardNames);
    }

    private List<String> searchCardNames(String partialCardName, Set<String> cards) {
        return cards.stream()
                .filter(cardName -> cardName.startsWith(partialCardName))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CardUsageData> getCubeCardData(String cardName) {
        return txnManager.runTaskReadOnly(atlasTransaction -> {
            CubeCardsTable cubeCardsTable = TABLES.getCubeCardsTable(atlasTransaction);
            CubeCardsTable.CubeCardsRow row = CubeCardsTable.CubeCardsRow.of(cardName);
            return cubeCardsTable.getRow(row).transform(cardRow -> cardRow.getCardUsage());
        });
    }

    @Override
    public CardUsageData addActiveCard(String cardName) {
        CardUsageData cardData = setCardStatusOrCreateNew(cardName, true);
        inactiveCardNames.remove(cardName);
        activeCardNames.add(cardName);
        return cardData;
    }

    @Override
    public CardUsageData addInactiveCard(String cardName) {
        CardUsageData cardData = setCardStatusOrCreateNew(cardName, false);
        activeCardNames.remove(cardName);
        inactiveCardNames.add(cardName);
        return cardData;
    }

    private CardUsageData setCardStatusOrCreateNew(String cardName, boolean isActive) {
        CardUsageData returnedCardData = txnManager.runTaskThrowOnConflict(atlasTransaction -> {
            CubeCardsTable cubeCardsTable = TABLES.getCubeCardsTable(atlasTransaction);
            CubeCardsTable.CubeCardsRow row = CubeCardsTable.CubeCardsRow.of(cardName);
            Optional<CardUsageData> existingCardData =
                    cubeCardsTable.getRow(row)
                    .transform(cardRow -> cardRow.getCardUsage());
            if (existingCardData.isPresent()) {
                CardUsageData cardData = existingCardData.get();
                if (cardData.getIsActive() == isActive) {
                    return cardData;
                } else {
                    CardUsageData newCardData = ImmutableCardUsageData.builder()
                            .from(cardData)
                            .isActive(isActive)
                            .build();
                    cubeCardsTable.putCardUsage(row, newCardData);
                    return newCardData;
                }
            }
            CardUsageData newCardData = ImmutableCardUsageData.builder()
                    .isActive(isActive)
                    .build();
            cubeCardsTable.putCardUsage(row, newCardData);
            return newCardData;
        });
        return returnedCardData;
    }

    @Override
    public String addDeck(PartialDeckList deck) {
        //Validate the deck
        Set<String> allCards = Sets.newTreeSet(deck.getMaindeck());
        allCards.removeAll(deck.getSideboard());
        if (allCards.size() != deck.getMaindeck().size()) {
            throw new IllegalArgumentException("Maindeck and sideboard contain duplicate cards");
        }
        int[] basics = deck.getNumBasics();
        if (basics.length != 6) {
            throw new IllegalArgumentException("Must specify all basics types");
        }
        int numBasics = 0;
        for (int i = 0; i < basics.length; ++i) {
            numBasics += basics[i];
        }
        if (numBasics + deck.getMaindeck().size() < 40) {
            throw new IllegalArgumentException("Maindeck must contain at least 40 cards");
        }

        String deckId = txnManager.runTaskWithRetry(atlasTransaction -> {
            String newDeckId = UUID.randomUUID().toString();

            //Update card usage for maindeck cards
            CubeCardsTable cubeCardsTable = TABLES.getCubeCardsTable(atlasTransaction);
            List<CubeCardsTable.CubeCardsRow> cardRowList = Lists.newArrayList();
            cardRowList.addAll(deck.getMaindeck().stream()
                        .map(cardName -> CubeCardsTable.CubeCardsRow.of(cardName))
                        .collect(Collectors.toList()));
            Map<CubeCardsTable.CubeCardsRow, CardUsageData> cardMap = cubeCardsTable.getCardUsages(cardRowList);
            if (cardMap.size() != deck.getMaindeck().size()) {
                atlasTransaction.abort();
                return null;
            }
            for (Entry<CubeCardsTable.CubeCardsRow, CardUsageData> entry : cardMap.entrySet()) {
                CardUsageData cardData = entry.getValue();
                List<String> newDeckIDs = Lists.newArrayList(cardData.getDeckIDs());
                newDeckIDs.add(newDeckId);
                CardUsageData newCardData = ImmutableCardUsageData.builder()
                        .from(cardData)
                        .numDrafts(cardData.getNumDrafts() + 1)
                        .numMaindecks(cardData.getNumMaindecks() + 1)
                        .deckIDs(newDeckIDs)
                        .build();
                cardMap.put(entry.getKey(), newCardData);
            }
            cubeCardsTable.putCardUsage(cardMap);

            //Update card usage for sideboard cards
            cardRowList = Lists.newArrayList();
            cardRowList.addAll(deck.getSideboard().stream()
                    .map(cardName -> CubeCardsTable.CubeCardsRow.of(cardName))
                    .collect(Collectors.toList()));
            cardMap = cubeCardsTable.getCardUsages(cardRowList);
            if (cardMap.size() != deck.getSideboard().size()) {
                atlasTransaction.abort();
                return null;
            }
            for (Entry<CubeCardsTable.CubeCardsRow, CardUsageData> entry : cardMap.entrySet()) {
                CardUsageData cardData = entry.getValue();
                CardUsageData newCardData = ImmutableCardUsageData.builder()
                        .from(cardData)
                        .numDrafts(cardData.getNumDrafts() + 1)
                        .build();
                cardMap.put(entry.getKey(), newCardData);
            }
            cubeCardsTable.putCardUsage(cardMap);

            //Insert the deck list
            CubeDecksTable cubeDecksTable = TABLES.getCubeDecksTable(atlasTransaction);
            CubeDecksTable.CubeDecksRow deckRow = CubeDecksTable.CubeDecksRow.of(newDeckId);
            CompleteDeckList completeDeck = ImmutableCompleteDeckList.builder()
                    .from(deck)
                    .deckId(newDeckId)
                    .build();
            cubeDecksTable.putDeckList(deckRow, completeDeck);
            return newDeckId;
        });
        if (deckId == null) {
            throw new IllegalArgumentException("Deck contains cards not in the cube");
        }
        return deckId;
    }

    @Override
    public void addMatchResult(MatchResult match) {
        txnManager.runTaskWithRetry(atlasTransaction -> {
            CubeMatchesTable cubeMatchesTable = TABLES.getCubeMatchesTable(atlasTransaction);
            CubeMatchesTable.CubeMatchesRow matchRow = CubeMatchesTable.CubeMatchesRow.of(
                    match.getFirstDeckId(), match.getSecondDeckId());
            Optional<MatchResult> matchResult = cubeMatchesTable.getRow(matchRow).transform(r -> r.getMatchResult());
            int firstDeckNumWins = match.getFirstDeckWins();
            int secondDeckNumWins = match.getSecondDeckWins();
            if (matchResult.isPresent()) {
                if (matchResult.equals(match)) {
                    return null;
                }
                firstDeckNumWins -= matchResult.get().getFirstDeckWins();
                secondDeckNumWins -= matchResult.get().getSecondDeckWins();
            }
            Map<CubeMatchesTable.CubeMatchesRow, MatchResult> matchMap = Maps.newHashMap();
            matchMap.put(matchRow, match);
            CubeMatchesTable.CubeMatchesRow reverseMatchRow = CubeMatchesTable.CubeMatchesRow.of(
                    match.getSecondDeckId(), match.getFirstDeckId());
            MatchResult reverseMatch = ImmutableMatchResult.builder()
                    .firstDeckId(match.getSecondDeckId())
                    .secondDeckId(match.getFirstDeckId())
                    .firstDeckWins(match.getSecondDeckWins())
                    .secondDeckWins(match.getFirstDeckWins())
                    .build();
            matchMap.put(reverseMatchRow, reverseMatch);
            cubeMatchesTable.putMatchResult(matchMap);

            //Update corresponding cards from the decks
            updateCardsInDeckList(atlasTransaction, match.getFirstDeckId(), firstDeckNumWins, secondDeckNumWins);
            updateCardsInDeckList(atlasTransaction, match.getSecondDeckId(), secondDeckNumWins, firstDeckNumWins);

            return null;
        });
    }

    private void updateCardsInDeckList(Transaction atlasTransaction, String deckId, int numWins, int numLosses) {
        if ((numWins == 0) && (numLosses == 0)) {
            return;
        }
        CubeDecksTable cubeDecksTable = TABLES.getCubeDecksTable(atlasTransaction);
        CubeDecksTable.CubeDecksRow deckRow = CubeDecksTable.CubeDecksRow.of(deckId);
        Optional<DeckList> deckList = cubeDecksTable.getRow(deckRow).transform(r -> r.getDeckList());
        if (!deckList.isPresent()) {
            atlasTransaction.abort();
            throw new IllegalArgumentException("Provided match deck ID does not exist");
        }
        CubeCardsTable cubeCardsTable = TABLES.getCubeCardsTable(atlasTransaction);
        List<CubeCardsTable.CubeCardsRow> cardRows = deckList.get().getMaindeck().stream()
                .map(d -> CubeCardsTable.CubeCardsRow.of(d))
                .collect(Collectors.toList());
        Map<CubeCardsTable.CubeCardsRow, CardUsageData> cardMap = cubeCardsTable.getCardUsages(cardRows);
        for (Entry<CubeCardsTable.CubeCardsRow, CardUsageData> entry : cardMap.entrySet()) {
            CardUsageData cardData = entry.getValue();
            CardUsageData newCardData = ImmutableCardUsageData.builder()
                    .from(entry.getValue())
                    .numWins(cardData.getNumWins() + numWins)
                    .numLosses(cardData.getNumLosses() + numLosses)
                    .build();
            cardMap.put(entry.getKey(), newCardData);
        }
        cubeCardsTable.putCardUsage(cardMap);
    }

    @Override
    public List<MatchResult> getMatchResults(String deckId) {
        return txnManager.runTaskReadOnly(atlasTransaction -> {
            return getMatchResultsOnTxn(deckId, atlasTransaction);
        });
    }

    private List<MatchResult> getMatchResultsOnTxn(String deckId, Transaction atlasTransaction) {
        CubeMatchesTable cubeMatchesTable = TABLES.getCubeMatchesTable(atlasTransaction);
        RangeRequest rangeRequest = CubeMatchesTable.CubeMatchesRow.createPrefixRangeUnsorted(deckId).build();
        return cubeMatchesTable.getRange(rangeRequest)
                .transform(r -> r.getMatchResult())
                .immutableCopy();
    }

    @Override
    public Optional<CompleteDeckList> getDeck(String deckId) {
        return txnManager.runTaskReadOnly(atlasTransaction -> {
            CubeDecksTable cubeDecksTable = TABLES.getCubeDecksTable(atlasTransaction);
            CubeDecksTable.CubeDecksRow row = CubeDecksTable.CubeDecksRow.of(deckId);
            return cubeDecksTable.getRow(row).transform(r -> r.getDeckList());
        });
    }

    @Override
    public Map<MagicCard, CardUsageData> getCardSearchResults(CardSearchQuery query) {
        return txnManager.runTaskReadOnly(atlasTransaction -> {
            return getCardSearchResultsOnTxn(query, atlasTransaction);
        });
    }

    private Map<MagicCard, CardUsageData> getCardSearchResultsOnTxn(
            CardSearchQuery query,
            Transaction atlasTransaction) {
        Set<String> cardNames;
        switch (query.getStartingSet()) {
            case ALL:
                cardNames = new TreeSet<>(activeCardNames);
                cardNames.addAll(inactiveCardNames);
                break;
            case ACTIVE:
                cardNames = new TreeSet<>(activeCardNames);
                break;
            default:
                cardNames = new TreeSet<>(inactiveCardNames);
                break;
        }
        for (Iterator<String> it = cardNames.iterator(); it.hasNext(); ) {
            String cardName = it.next();
            MagicCard card = magicCardMap.get(cardName);
            for (ComplexMagicCardFilter filter : query.getMagicCardFilters()) {
                if (!filter.accept(card)) {
                    it.remove();
                    break;
                }
            }
        }
        CubeCardsTable cubeCardsTable = TABLES.getCubeCardsTable(atlasTransaction);
        List<CubeCardsRow> cardRows = cardNames.stream()
                .map(name -> CubeCardsRow.of(name))
                .collect(Collectors.toList());
        Map<CubeCardsRow, CardUsageData> usageMap = cubeCardsTable.getCardUsages(cardRows);
        for (Iterator<Map.Entry<CubeCardsRow, CardUsageData>> it = usageMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<CubeCardsRow, CardUsageData> entry = it.next();
            CardUsageData cardData = entry.getValue();
            for (ComplexCardUsageFilter filter : query.getCardUsageFilters()) {
                if (!filter.accept(cardData)) {
                    it.remove();
                    break;
                }
            }
        }
        Map<MagicCard, CardUsageData> returnData = Maps.newHashMap();
        for (Map.Entry<CubeCardsRow, CardUsageData> entry : usageMap.entrySet()) {
            returnData.put(magicCardMap.get(entry.getKey().getCardName()), entry.getValue());
        }
        return returnData;
    }

    @Override
    public Map<CompleteDeckList, List<MatchResult>> getDeckSearchResults(DeckSearchQuery query) {
        return txnManager.runTaskReadOnly(atlasTransaction -> {
            Map<CubeDecksTable.CubeDecksRow, CompleteDeckList> deckMap = applyDeckFilters(query, atlasTransaction);
            return applyMatchResultFilters(query, deckMap, atlasTransaction);
        });
    }

    private Map<CubeDecksTable.CubeDecksRow, CompleteDeckList> applyDeckFilters(
            DeckSearchQuery query,
            Transaction atlasTransaction) {
        CubeDecksTable cubeDecksTable = TABLES.getCubeDecksTable(atlasTransaction);
        Map<CubeDecksTable.CubeDecksRow, CompleteDeckList> deckMap = Maps.newHashMap();
        if (query.getCardSearchQuery().isPresent()) {
            Map<MagicCard, CardUsageData> cardDataMap =
                    getCardSearchResultsOnTxn(query.getCardSearchQuery().get(), atlasTransaction);
            Set<String> deckIds = Sets.newTreeSet();
            for (CardUsageData cardData : cardDataMap.values()) {
                deckIds.addAll(cardData.getDeckIDs());
            }
            List<CubeDecksTable.CubeDecksRow> deckRows = deckIds.stream()
                    .map(name -> CubeDecksTable.CubeDecksRow.of(name))
                    .collect(Collectors.toList());
            deckMap = cubeDecksTable.getDeckLists(deckRows);
            for (Iterator<Map.Entry<CubeDecksTable.CubeDecksRow, CompleteDeckList>> it
                    = deckMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<CubeDecksTable.CubeDecksRow, CompleteDeckList> entry = it.next();
                CompleteDeckList deck = entry.getValue();
                for (ComplexDeckListFilter filter : query.getDeckFilters()) {
                    if (!filter.accept(deck)) {
                        it.remove();
                        break;
                    }
                }
            }
        } else {
            BatchingVisitableView<CubeDecksRowResult> view = cubeDecksTable.getAllRowsUnordered();
            for (ComplexDeckListFilter filter : query.getDeckFilters()) {
                view.filter(row -> filter.accept(row.getDeckList()));
            }
            for (CubeDecksRowResult result : view.immutableCopy()) {
                deckMap.put(result.getRowName(), result.getDeckList());
            }
        }
        return deckMap;
    }

    private Map<CompleteDeckList, List<MatchResult>> applyMatchResultFilters(
            DeckSearchQuery query,
            Map<CubeDecksTable.CubeDecksRow, CompleteDeckList> deckMap,
            Transaction atlasTransaction) {
        Map<CompleteDeckList, List<MatchResult>> searchResults = Maps.newHashMap();
        for (Map.Entry<CubeDecksTable.CubeDecksRow, CompleteDeckList> entry : deckMap.entrySet()) {
            List<MatchResult> matchResults = getMatchResultsOnTxn(entry.getKey().getDeckId(), atlasTransaction);
            searchResults.put(entry.getValue(), matchResults);
        }
        for (Iterator<Map.Entry<CompleteDeckList, List<MatchResult>>> it
                = searchResults.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<CompleteDeckList, List<MatchResult>> entry = it.next();
            for (ComplexMatchResultFilter filter : query.getMatchResultFilters()) {
                if (!filter.accept(entry.getValue())) {
                    it.remove();
                    break;
                }
            }
        }
        return searchResults;
    }

    @Override
    public List<String> getActiveCardNames() {
        return Lists.newArrayList(activeCardNames);
    }

    @Override
    public List<String> getInactiveCardNames() {
        return Lists.newArrayList(inactiveCardNames);
    }
}
