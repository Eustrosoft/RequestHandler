/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.cms.dbdatasource.ranges;

public enum FileType {
    FILE("B"),
    DIRECTORY("D"),
    ROOT("R");

    public final String value;

    public String getValue() {
        return value;
    }

    FileType(String value) {
        this.value = value;
    }

    public static FileType fromString(String str) {
        if (str == null || str.isEmpty()) {
            throw new NullPointerException("String was null.");
        }
        switch (str) {
            case "B":
                return FileType.FILE;
            case "D":
                return FileType.DIRECTORY;
            case "R":
                return FileType.ROOT;
            default:
                throw new NullPointerException("This value not found.");
        }
    }
}
