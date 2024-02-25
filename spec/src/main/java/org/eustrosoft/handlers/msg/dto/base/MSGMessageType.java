package org.eustrosoft.handlers.msg.dto.base;

public enum MSGMessageType {
    M("M"),
    A("A"),
    L("L");

    final String value;

    MSGMessageType(String value) {
        this.value = value;
    }

    public static MSGMessageType of(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        for (MSGMessageType val : values()) {
            if (val.name().equalsIgnoreCase(str)) {
                return val;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }
}
