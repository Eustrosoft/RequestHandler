package org.eustrosoft.spec.experimental;

import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonConvertible;

public class Stub implements JsonConvertible {
    @Override
    public String convertToString() throws JsonException {
        return "";
    }
}
