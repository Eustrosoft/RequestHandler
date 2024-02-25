package org.eustrosoft.handlers.sam.dto;

import org.eustrosoft.core.json.QJson;
import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonData;
import org.eustrosoft.util.JsonUtil;

public class UserDTO implements JsonData<UserDTO> {
    private Long id;
    private String username;
    private String fullName;
    private String role;
    private String icon;

    public UserDTO() {

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

    @Override
    public String convertToString() throws JsonException {
        return JsonUtil.toJson(
                JsonUtil.getFormatString(5),
                JsonUtil.AsEntry.getNumberParams("id", id),
                JsonUtil.AsEntry.getStringParams("username", username),
                JsonUtil.AsEntry.getStringParams("fullName", fullName),
                JsonUtil.AsEntry.getStringParams("role", role),
                JsonUtil.AsEntry.getStringParams("icon", icon)
        );
    }

    @Override
    public UserDTO convertToObject(QJson value) throws JsonException {
        setFullName(value.getItemString("fullName"));
        setIcon(value.getItemString("icon"));
        setId(value.getItemLong("id"));
        setRole(value.getItemString("role"));
        setUsername(value.getItemString("username"));
        return this;
    }
}
