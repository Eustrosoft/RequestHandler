package com.eustrosoft.dbdatasource.ranges;

import lombok.Getter;

public enum FileType {
    FILE("B"),
    DIRECTORY("D"),
    ROOT("R");

    @Getter
    public final String value;

    FileType(String value) {
        this.value = value;
    }
}