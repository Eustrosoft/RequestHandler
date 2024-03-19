package com.eustrosoft.core.dto;

public class ScopeCreationDTO {
    private Long ZSID;
    private String name;
    private String description;

    public ScopeCreationDTO(Long ZSID, String name, String description) {
        this.ZSID = ZSID;
        this.name = name;
        this.description = description;
    }

    public void setZSID(Long ZSID) {
        this.ZSID = ZSID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getZSID() {
        return ZSID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
