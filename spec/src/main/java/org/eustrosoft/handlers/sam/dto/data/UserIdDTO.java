package org.eustrosoft.handlers.sam.dto.data;

public class UserIdDTO {
    private String id;

    public UserIdDTO(String id) {
        this.id = id;
    }

    public String getNewString() {
        return id;
    }

    public void setNewString(String newString) {
        this.id = newString;
    }
}
