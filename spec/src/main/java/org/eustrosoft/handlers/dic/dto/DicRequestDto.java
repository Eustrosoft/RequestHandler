package org.eustrosoft.handlers.dic.dto;

import org.eustrosoft.core.json.QJson;
import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonParsable;

public class DicRequestDto implements JsonParsable<DicRequestDto> {
    public static final String PARAM_NAME = "name";
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public DicRequestDto convertToObject(QJson value) throws JsonException {
        setName(value.getItemString(PARAM_NAME));
        return this;
    }
}
