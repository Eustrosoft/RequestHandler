/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.model.ranges;

public enum CMSType {
    UNKNOWN("UNKNOWN"),
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
