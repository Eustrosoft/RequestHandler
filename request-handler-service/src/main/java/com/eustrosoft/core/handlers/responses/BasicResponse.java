package com.eustrosoft.core.handlers.responses;

import com.google.gson.JsonObject;

public abstract class BasicResponse implements ResponseBlock {

    public JsonObject toJsonObject() throws Exception {
        JsonObject object = new JsonObject();
        object.addProperty("s", getS());
        object.addProperty("e", getE());
        object.addProperty("m", getM());
        object.addProperty("l", getL());
        return object;
    }
}
