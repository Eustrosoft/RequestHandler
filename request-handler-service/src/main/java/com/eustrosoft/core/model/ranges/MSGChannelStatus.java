package com.eustrosoft.core.model.ranges;

import lombok.Getter;

public enum MSGChannelStatus {
    N("N"),
    W("W"),
    I("I"),
    C("C");
    @Getter
    final String value;

    MSGChannelStatus(String value) {
        this.value = value;
    }

    public static MSGChannelStatus of(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        MSGChannelStatus[] values = MSGChannelStatus.values();
        for (MSGChannelStatus val : values) {
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
        MSGChannelStatus[] values = MSGChannelStatus.values();
        for (MSGChannelStatus val : values) {
            if (val.toString().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
