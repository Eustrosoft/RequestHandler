/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.sam;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eustrosoft.core.constants.Constants;
import org.eustrosoft.core.handlers.responses.BasicResponse;
import org.eustrosoft.core.handlers.responses.ResponseLang;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class SAMResponseBlock extends BasicResponse {
    private String errMsg = "";
    private Short errCode = 0;
    private String responseType;
    private String data;
    private List<Long> zsid;

    @Override
    public String getS() {
        return Constants.SUBSYSTEM_SAM;
    }

    @Override
    public String getR() {
        return responseType;
    }

    @Override
    public String getM() {
        return this.errMsg;
    }

    @Override
    public String getL() {
        return ResponseLang.en_US;
    }

    @Override
    public Short getE() {
        return errCode;
    }

    public void setE(int code) {
        errCode = (short) code;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public JsonObject toJsonObject() throws Exception {
        JsonObject object = super.toJsonObject();
        if (getData() != null && !getData().isEmpty()) {
            object.addProperty("data", getData());
        }
        if (zsid != null) {
            object.add("zsid", new Gson().toJsonTree(zsid));
        }
        return object;
    }
}
