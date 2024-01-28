package org.eustrosoft.spec.experimental;

import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonData;

public class Stub implements JsonData {
    @Override
    public String toJsonString() throws JsonException {
        return "";
    }
}
