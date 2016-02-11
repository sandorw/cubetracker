package com.github.sandorw.cubetracker.server.store.atlas;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.github.sandorw.cubetracker.server.cards.CardUsageData;
import com.github.sandorw.cubetracker.server.cards.ImmutableCardUsageData;
import com.github.sandorw.cubetracker.server.cards.MagicCard;
import com.github.sandorw.cubetracker.server.configuration.CubeTrackerServerConfiguration;
import com.github.sandorw.cubetracker.server.decks.DeckList;
import com.github.sandorw.cubetracker.server.store.CubeTrackerStore;
import com.github.sandorw.cubetracker.server.store.atlas.generated.CubeCardsTable;
import com.github.sandorw.cubetracker.server.store.atlas.generated.CubeDecksTable;
import com.github.sandorw.cubetracker.server.store.atlas.generated.CubeTrackerStoreTableFactory;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.palantir.atlasdb.transaction.api.TransactionManager;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import jersey.repackaged.com.google.common.collect.Lists;

/**
 * Atlas implementation of the CubeTracker data store.
 */
public class AtlasCubeTrackerStore implements CubeTrackerStore {
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
        return Optional.of(magicCardMap.get(cardName));
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
    public String addDeck(DeckList deck) {
        //Validate the deck
        Set<String> allCards = Sets.newTreeSet(deck.getMaindeck());
        allCards.removeAll(deck.getSideboard());
        if (allCards.size() != deck.getMaindeck().size()) {
            throw new IllegalArgumentException("Maindeck and sideboard contain duplicate cards");
        }
        int[] basics = deck.getNumBasics();
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
            cubeDecksTable.putDeckList(deckRow, deck);
            return newDeckId;
        });
        if (deckId == null) {
            throw new IllegalArgumentException("Deck contains cards not in the cube");
        }
        return deckId;
    }
}
