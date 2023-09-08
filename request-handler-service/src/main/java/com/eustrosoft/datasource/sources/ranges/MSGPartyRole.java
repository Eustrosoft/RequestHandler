package com.eustrosoft.datasource.sources.ranges;

import lombok.Getter;

public enum MSGPartyRole {
    CREATOR("C"),
    USER("U");
    @Getter
    final String value;

    MSGPartyRole(String value) {
        this.value = value;
    }

    public static MSGPartyRole of(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        MSGPartyRole[] values = MSGPartyRole.values();
        for (MSGPartyRole val : values) {
            if (val.getValue().equalsIgnoreCase(str)) {
                return val;
            }
        }
        return null;
    }

    public boolean isInRange(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        MSGPartyRole[] values = MSGPartyRole.values();
        for (MSGPartyRole val : values) {
            if (val.toString().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
