package com.eustrosoft.core.handlers.responses;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public interface ResponseBlock {
    String getSubsystem();

    String getRequest();

    Long getStatus();

    Long getQId();

    Short getErrCode();

    String getErrMsg();

    default String toJson() throws Exception {
        JsonObject object = new JsonObject();
        object.addProperty("subsystem", getSubsystem());
        object.addProperty("status", getStatus());
        object.addProperty("qid", getQId());
        object.addProperty("err_code", getErrCode());
        object.addProperty("err_msg", getErrMsg());
        return new Gson().toJson(object);
    }
}
