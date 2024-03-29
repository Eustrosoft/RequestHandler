package com.eustrosoft.core.model.ranges;

import com.eustrosoft.cms.CMSType;

public enum MSGMessageType {
    M("M"),
    A("A"),
    L("L");

    final String value;

    public String getValue() {
        return value;
    }

    MSGMessageType(String value) {
        this.value = value;
    }

    public static MSGMessageType of(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        MSGMessageType[] values = MSGMessageType.values();
        for (MSGMessageType val : values) {
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
        CMSType[] values = CMSType.values();
        for (CMSType val : values) {
            if (val.toString().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
