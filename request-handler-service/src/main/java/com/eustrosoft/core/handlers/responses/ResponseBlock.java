package com.eustrosoft.core.handlers.responses;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public interface ResponseBlock {
    String getS();

    String getR();
    Short getE();

    String getM();
    String getL();

    default String toJson() throws Exception {
        JsonObject object = new JsonObject();
        object.addProperty("s", getS());
        object.addProperty("e", getE());
        object.addProperty("m", getM());
        object.addProperty("l", getL());
        return new Gson().toJson(object);
    }
}
