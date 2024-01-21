package org.eustrosoft.handlers.sam.dto.data;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    private Long id;
    private String username;
    private String fullName;
    private String role;
    private String icon;
    private UserIdDTO2 userIdDTO;
    private List<String> someSh;

    public UserDTO() {
        userIdDTO = new UserIdDTO2("131");
        someSh = new ArrayList<>();
        someSh.add("123");
        someSh.add("222");
        someSh.add("333");
        someSh.add("444");
    }

    public UserDTO(Long id, String username, String fullName, String role, String icon) {
        this();
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.icon = icon;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
