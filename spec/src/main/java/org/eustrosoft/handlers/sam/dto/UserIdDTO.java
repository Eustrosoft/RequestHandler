package org.eustrosoft.handlers.sam.dto;

import org.eustrosoft.json.JsonUtil;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonData;

public class UserIdDTO implements JsonData {
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

    @Override
    public String toJsonString() throws JsonException {
        return JsonUtil.toJson(
                JsonUtil.getFormatString(1),
                JsonUtil.AsEntry.getStringParams("id", id)
        );
    }
}
