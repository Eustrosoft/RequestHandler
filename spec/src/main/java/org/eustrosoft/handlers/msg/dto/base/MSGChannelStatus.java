package org.eustrosoft.handlers.msg.dto.base;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum MSGChannelStatus {
    N("N"),
    W("W"),
    I("I"),
    C("C");

    final String value;

    public String getValue() {
        return value;
    }

    MSGChannelStatus(String value) {
        this.value = value;
    }

    public static String toSQLWhere(String value, List<MSGChannelStatus> statuses) {
        if (value == null || value.isEmpty() || statuses == null || statuses.isEmpty()) {
            return "";
        }
        List<String> values = statuses.stream()
                .map(val -> String.format("'%s'", val.getValue()))
                .collect(Collectors.toList());
        return String.format(
                "%s in (%s)",
                value,
                String.join(",", values)
        );
    }

    public static List<MSGChannelStatus> of(List<String> values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }
        List<MSGChannelStatus> statuses = new ArrayList<>();
        for (String s : values) {
            MSGChannelStatus of = of(s);
            if (of != null) {
                statuses.add(of);
            }
        }
        return statuses;
    }

    public static MSGChannelStatus of(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        MSGChannelStatus[] values = MSGChannelStatus.values();
        for (MSGChannelStatus val : values) {
            if (val.getValue().equalsIgnoreCase(value)) {
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
