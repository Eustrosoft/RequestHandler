package org.eustrosoft.experimental;

import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonConvertible;

public class Stub implements JsonConvertible {
    @Override
    public String convertToString() throws JsonException {
        return "";
    }
}
