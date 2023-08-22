package com.eustrosoft.datasource.sources.ranges;

public enum MSGChannelStatus {
    OPEN("OPEN"),
    CLOSED("CLOSED");
    final String value;

    MSGChannelStatus(String value) {
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
