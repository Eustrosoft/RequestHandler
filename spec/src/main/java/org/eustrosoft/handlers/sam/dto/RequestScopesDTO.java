package org.eustrosoft.handlers.sam.dto;

import org.eustrosoft.json.QJson;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonParsable;

public class RequestScopesDTO implements JsonParsable<RequestScopesDTO> {
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public RequestScopesDTO convertToObject(QJson value) throws JsonException {
        setType(value.getItemString("type"));
        return this;
    }
}
