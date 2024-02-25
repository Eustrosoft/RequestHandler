package org.eustrosoft.core.response.basic;

import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonConvertible;

public class StubDto implements JsonConvertible {

    public StubDto() {
    }

    @Override
    public String convertToString() throws JsonException {
        return null;
    }
}
