package com.github.sandorw.cubetracker.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.github.sandorw.cubetracker.cards.MagicCard;
import com.github.sandorw.cubetracker.config.Configuration;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class CubeTrackerServer {

    private CubeTrackerServer() {}

    public static void main(String[] args) {

    }

    public void run(Configuration config) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new GuavaModule());
        Map<String, MagicCard> allCardsMap = null;
        try {
            allCardsMap = mapper.readValue(new File(config.getCardJsonPath()),
                    new TypeReference<Map<String, MagicCard>>() { });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(allCardsMap.get("Bitterblossom"));
    }
}
