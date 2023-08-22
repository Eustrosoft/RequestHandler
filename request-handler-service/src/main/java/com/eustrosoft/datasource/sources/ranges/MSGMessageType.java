package com.eustrosoft.datasource.sources.ranges;

public enum MSGMessageType {
    SIMPLE("M"),
    ANSWER("A"),
    LIKE("L");
    final String value;

    MSGMessageType(String value) {
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
