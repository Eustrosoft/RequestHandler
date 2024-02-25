package org.eustrosoft.exceptions;

public class SessionNotExistsException extends Exception {

    public SessionNotExistsException() {
    }

    public SessionNotExistsException(String message) {
        super(message);
    }
}
