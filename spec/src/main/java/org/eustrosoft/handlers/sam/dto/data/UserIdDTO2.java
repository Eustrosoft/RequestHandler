package org.eustrosoft.handlers.sam.dto.data;

public class UserIdDTO2 extends UserIdDTO {
    private String newString;

    public UserIdDTO2(String id) {
        super(id);
    }

    public String getNewString() {
        return newString;
    }

    public void setNewString(String newString) {
        this.newString = newString;
    }

}
