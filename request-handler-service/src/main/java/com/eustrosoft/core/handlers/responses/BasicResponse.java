/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.responses;

import com.google.gson.JsonObject;

public abstract class BasicResponse implements ResponseBlock {

    public JsonObject toJsonObject() throws Exception {
        JsonObject object = new JsonObject();
        object.addProperty("s", getS());
        object.addProperty("e", getE());
        object.addProperty("m", getM());
        object.addProperty("l", getL());
        object.addProperty("r", getR());
        return object;
    }
}
