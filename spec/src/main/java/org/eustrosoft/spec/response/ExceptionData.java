package org.eustrosoft.spec.response;

import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonData;

public class ExceptionData implements JsonData {
    private String data;

    public ExceptionData(String data) {
        this.data = data;
    }

    @Override
    public String toJsonString() throws JsonException {
        return data;
    }
}
