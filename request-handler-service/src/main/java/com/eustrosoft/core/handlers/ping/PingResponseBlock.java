package com.eustrosoft.core.handlers.ping;

import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.handlers.responses.ResponseLang;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class PingResponseBlock implements ResponseBlock {
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
    public String toJson() throws Exception {
        JsonObject object = new JsonObject();
        object.addProperty("s", getS());
        object.addProperty("e", getE());
        object.addProperty("m", getM());
        if (getUserId() != null) {
            object.addProperty("userId", getUserId());
        }
        if (getFullName() != null) {
            object.addProperty("fullName", getFullName());
        }
        if (getUsername() != null) {
            object.addProperty("username", getUsername());
        }
        return new Gson().toJson(object);
    }
}
