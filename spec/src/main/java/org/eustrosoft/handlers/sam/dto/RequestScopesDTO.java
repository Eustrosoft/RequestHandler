package org.eustrosoft.handlers.sam.dto;

import org.eustrosoft.core.json.QJson;
import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonParsable;

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
