package org.eustrosoft.handlers.cms.dto;

public class ResponseParams {
    private String subsystem;
    private String request;
    private String message;
    private Short error;
    private String lang;

    public ResponseParams(String subsystem, String request, String message, Short error, String lang) {
        this.subsystem = subsystem;
        this.request = request;
        this.message = message;
        this.error = error;
        this.lang = lang;
    }

    public String getSubsystem() {
        return subsystem;
    }

    public void setSubsystem(String subsystem) {
        this.subsystem = subsystem;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Short getError() {
        return error;
    }

    public void setError(Short error) {
        this.error = error;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
