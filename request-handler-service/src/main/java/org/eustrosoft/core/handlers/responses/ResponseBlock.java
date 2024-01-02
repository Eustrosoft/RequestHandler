/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.handlers.responses;

import com.google.gson.JsonObject;

public interface ResponseBlock {
    String getS();

    String getR();

    Short getE();

    String getM();

    String getL();

    default JsonObject toJsonObject() throws Exception {
        JsonObject object = new JsonObject();
        object.addProperty("s", getS());
        object.addProperty("e", getE());
        object.addProperty("r", getR());
        object.addProperty("m", getM());
        object.addProperty("l", getL());
        return object;
    }
}
