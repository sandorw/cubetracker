/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.github.sandorw.cubetracker.config;

import org.apache.commons.lang3.Validate;

/**
 *
 */
public final class Configuration {
    private final String cardJsonPath;

    public Configuration(String cardJsonPath) {
        this.cardJsonPath = Validate.notNull(cardJsonPath);
    }

    public String getCardJsonPath() {
        return cardJsonPath;
    }
}
