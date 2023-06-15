/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.datasource.sources;

import lombok.Getter;

public enum Source {
    DATABASE("DATABASE"),
    FILE_SYSTEM("FILE_SYSTEM");

    @Getter
    private String value;

    Source(String value) {
        this.value = value;
    }

    public boolean isInRange(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        Source[] values = Source.values();
        for (Source val : values) {
            if (val.toString().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
