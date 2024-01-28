/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers.sam.dto;

import org.eustrosoft.spec.interfaces.JsonData;
import org.eustrosoft.spec.response.BasicResponseBlock;

public final class SAMResponseBlock<T extends JsonData> extends BasicResponseBlock<T> {
    private T data;

    public SAMResponseBlock(String requestType) {
        this.r = requestType;
    }

    @Override
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

//    @Override
//    public JsonObject toJsonObject() throws Exception {
//        JsonObject object = super.toJsonObject();
//        if (getData() != null && !getData().isEmpty()) {
//            object.addProperty("data", getData());
//        }
//        if (zsid != null) {
//            object.add("zsid", new Gson().toJsonTree(zsid));
//        }
//        return object;
//    }
}
