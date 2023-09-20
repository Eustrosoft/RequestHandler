/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.ping;

import com.eustrosoft.core.handlers.responses.BasicResponse;
import com.eustrosoft.core.handlers.responses.ResponseLang;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import static com.eustrosoft.core.constants.Constants.REQUEST_PING;
import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_PING;

@Getter
@Setter
public class PingResponseBlock extends BasicResponse {
    private Short errCode = 0;
    private String errMsg = "";
    private String userId;
    private String username;
    private String fullName;
    private String dbUser;

    @Override
    public String getS() {
        return SUBSYSTEM_PING;
    }

    @Override
    public String getR() {
        return REQUEST_PING;
    }

    @Override
    public Short getE() {
        return this.errCode;
    }

    @Override
    public String getM() {
        return this.errMsg;
    }

    @Override
    public String getL() {
        return ResponseLang.en_US; // TODO
    }

    @Override
    public JsonObject toJsonObject() throws Exception {
        JsonObject object = super.toJsonObject();
        if (getUserId() != null) {
            object.addProperty("userId", getUserId());
        }
        if (getFullName() != null) {
            object.addProperty("fullName", getFullName());
        }
        if (getUsername() != null) {
            object.addProperty("username", getUsername());
        }
        if (getDbUser() != null) {
            object.addProperty("dbUser", getDbUser());
        }
        return object;
    }
}
