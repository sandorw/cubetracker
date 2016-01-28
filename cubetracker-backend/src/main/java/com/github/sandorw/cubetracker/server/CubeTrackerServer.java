/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.github.sandorw.cubetracker.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.github.sandorw.cubetracker.cards.MagicCard;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 *
 */
public class CubeTrackerServer {
    public static void main(String[] args) {

        ObjectMapper mapper = new ObjectMapper().registerModule(new GuavaModule());
        Map<String,MagicCard> allCardsMap = null;
        try {
            allCardsMap = mapper.readValue(new File("/Users/sandorw/Downloads/AllCards.json"), new TypeReference<Map<String,MagicCard>>() { });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(allCardsMap.get("Bitterblossom"));

    }
}
