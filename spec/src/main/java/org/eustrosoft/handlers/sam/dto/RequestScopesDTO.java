package org.eustrosoft.handlers.sam.dto;

public class RequestScopesDTO {
    private String type;

    public RequestScopesDTO(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
