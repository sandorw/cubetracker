package com.github.sandorw.cubetracker.config;

import org.apache.commons.lang3.Validate;

/**
 * Necessary configuration values for the backend server.
 */
public final class Configuration {
    private final String cardJsonPath;

    public Configuration(String cardJsonPath) {
        this.cardJsonPath = Validate.notNull(cardJsonPath,
                "A non-null path to the json card dump is required.");
    }

    public String getCardJsonPath() {
        return cardJsonPath;
    }
}
