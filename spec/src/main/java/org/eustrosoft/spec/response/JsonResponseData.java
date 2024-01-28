package org.eustrosoft.spec.response;

import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonData;

public class JsonResponseData implements JsonData {
    private final String json;

    public JsonResponseData(String json) {
        this.json = json;
    }

    @Override
    public String toJsonString() throws JsonException {
        return json;
    }
}
