package org.eustrosoft.core.json.exception;

import static org.eustrosoft.core.json.Constants.ERR_JSON_NAME_NOT_DEFINED;

public class JsonNameException extends JsonException {

    public JsonNameException(Throwable throwable) {
        super(ERR_JSON_NAME_NOT_DEFINED, throwable);
    }

    public JsonNameException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public JsonNameException() {
        super(ERR_JSON_NAME_NOT_DEFINED);
    }
}
