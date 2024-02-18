package org.eustrosoft.spec.request;

import org.eustrosoft.json.QJson;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonParsable;

public class StringDTO implements JsonParsable<StringDTO> {
    private String query;

    @Override
    public StringDTO convertToObject(QJson value) throws JsonException {
        setQuery(value.getItemString("query"));
        return this;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
