package org.eustrosoft.core.response;

public class ResponseParams {
    private String subsystem;
    private String request;
    private String message;
    private String lang;
    private Long error;

    public ResponseParams(String subsystem, String request, String message, Long error, String lang) {
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

    public Long getError() {
        return error;
    }

    public void setError(Long error) {
        this.error = error;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
