package com.eustrosoft.datasource.sources.model;

public enum CMSType {
    DIRECTORY("DIRECTORY"),
    FILE("FILE"),
    LINK("LINK");

    final String value;

    CMSType(String value) {
        this.value = value;
    }

    public boolean isInRange(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        CMSType[] values = CMSType.values();
        for (CMSType val : values) {
            if (val.toString().equals(value)) {
                return true;
            }
        }
        return false;
    }

}
