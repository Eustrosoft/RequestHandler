package org.eustrosoft.json.exception;

public class JsonException extends Exception {

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }

    public JsonException(String message) {
        super(message);
    }

    public JsonException() {
    }
}
