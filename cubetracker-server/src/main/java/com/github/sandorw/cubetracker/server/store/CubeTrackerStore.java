package com.github.sandorw.cubetracker.server.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.github.sandorw.cubetracker.server.cards.CardUsageData;
import com.github.sandorw.cubetracker.server.cards.ImmutableCardUsageData;
import com.github.sandorw.cubetracker.server.cards.MagicCard;
import com.github.sandorw.cubetracker.server.configuration.CubeTrackerServerConfiguration;
import com.github.sandorw.cubetracker.server.store.atlas.generated.CubeCardsTable;
import com.github.sandorw.cubetracker.server.store.atlas.generated.CubeTrackerStoreTableFactory;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.palantir.atlasdb.transaction.api.TransactionManager;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Backing store for cubetracker-server.
 */
public final class CubeTrackerStore {
    private volatile Map<String, MagicCard> magicCardMap;
    private volatile Set<String> allCardNames;
    private volatile Set<String> activeCardNames;
    private volatile Set<String> inactiveCardNames;
    private final TransactionManager txnManager;
    private static final CubeTrackerStoreTableFactory TABLES = CubeTrackerStoreTableFactory.of();

    public CubeTrackerStore(TransactionManager txnManager) {
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

    public Optional<MagicCard> getMagicCard(String cardName) {
        return Optional.of(magicCardMap.get(cardName));
    }

    public boolean isValidCard(String cardName) {
        return magicCardMap.containsKey(cardName);
    }

    public List<String> searchAllCardNames(String partialCardName) {
        return searchCardNames(partialCardName, allCardNames);
    }

    public List<String> searchActiveCardNames(String partialCardName) {
        return searchCardNames(partialCardName, activeCardNames);
    }

    public List<String> searchInactiveCardNames(String partialCardName) {
        return searchCardNames(partialCardName, inactiveCardNames);
    }

    private List<String> searchCardNames(String partialCardName, Set<String> cards) {
        return cards.stream()
                .filter(cardName -> cardName.startsWith(partialCardName))
                .collect(Collectors.toList());
    }

    public Optional<CardUsageData> getCubeCardData(String cardName) {
        return txnManager.runTaskReadOnly(atlasTransaction -> {
            CubeCardsTable cubeCardsTable = TABLES.getCubeCardsTable(atlasTransaction);
            CubeCardsTable.CubeCardsRow row = CubeCardsTable.CubeCardsRow.of(cardName);
            return cubeCardsTable.getRow(row).transform(cardRow -> cardRow.getCardUsage());
        });
    }

    public CardUsageData addActiveCard(String cardName) {
        CardUsageData cardData = setCardStatusOrCreateNew(cardName, true);
        inactiveCardNames.remove(cardName);
        activeCardNames.add(cardName);
        return cardData;
    }

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
}
