/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.ping;

import com.eustrosoft.core.handlers.responses.BasicResponse;
import com.eustrosoft.core.handlers.responses.ResponseLang;
import com.google.gson.JsonObject;

public class PingResponseBlock extends BasicResponse {
    private Short errCode;
    private String message;
    private String userId;
    private String username;
    private String fullName;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setErrCode(Short errCode) {
        this.errCode = errCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getS() {
        return "ping";
    }

    @Override
    public String getR() {
        return "ping";
    }

    @Override
    public Short getE() {
        return this.errCode;
    }

    @Override
    public String getM() {
        return this.message;
    }

    @Override
    public String getL() {
        return ResponseLang.en_US; // TODO
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
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
        return object;
    }
}
