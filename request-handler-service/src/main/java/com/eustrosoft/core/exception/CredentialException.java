package com.eustrosoft.core.exception;

// TODO: extends Exception
public class CredentialException {
    private String message;

    public CredentialException() {
        this.message = getStandardMessage();
    }

    public CredentialException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    private String getStandardMessage() {
        return "{\"status\": 401, \"answer\":\"Invalid credentials.\"}";
    }
}
