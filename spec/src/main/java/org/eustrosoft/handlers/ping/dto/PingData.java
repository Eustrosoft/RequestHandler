package org.eustrosoft.handlers.ping.dto;

import org.eustrosoft.json.JsonUtil;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonData;

public class PingData implements JsonData {
    private String username;
    private String fullName;
    private String dbUser;
    private String userId;

    public PingData(String username, String fullName, String dbUser, String userId) {
        this.username = username;
        this.fullName = fullName;
        this.dbUser = dbUser;
        this.userId = userId;
    }

    @Override
    public String toJsonString() throws JsonException {
        return JsonUtil.toJson(
                JsonUtil.getFormatString(4),
                JsonUtil.AsEntry.getStringParams("username", username),
                JsonUtil.AsEntry.getStringParams("fullName", fullName),
                JsonUtil.AsEntry.getStringParams("dbUser", dbUser),
                JsonUtil.AsEntry.getStringParams("userId", userId)
        );
    }
}
