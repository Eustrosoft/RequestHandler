package org.eustrosoft.spec.response;

import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonConvertible;

public class ExceptionData implements JsonConvertible {
    private String data;

    public ExceptionData(String data) {
        this.data = data;
    }

    @Override
    public String convertToString() throws JsonException {
        return data;
    }
}
