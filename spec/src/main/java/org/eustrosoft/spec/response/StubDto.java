package org.eustrosoft.spec.response;

import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonConvertible;

public class StubDto implements JsonConvertible {

    public StubDto() {
    }

    @Override
    public String convertToString() throws JsonException {
        return null;
    }
}
