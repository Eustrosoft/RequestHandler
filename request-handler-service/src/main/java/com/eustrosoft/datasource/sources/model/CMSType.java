package com.eustrosoft.datasource.sources.model;

public enum CMSType {
    DIRECTORY("DIRECTORY"),
    FILE("FILE"),
    LINK("LINK");

    final String value;

    CMSType(String value) {
        this.value = value;
    }

}
