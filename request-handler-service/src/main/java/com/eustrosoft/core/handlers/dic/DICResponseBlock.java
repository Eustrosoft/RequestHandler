/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.dic;

import com.eustrosoft.core.handlers.responses.BasicResponse;
import com.eustrosoft.core.handlers.responses.ResponseLang;
import com.eustrosoft.core.model.DIC;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_DIC;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class DICResponseBlock extends BasicResponse {
    private String errMsg = "";
    private Short errCode = 0;
    private String responseType;
    private List<DIC> values;
    private List<String> dics;

    @Override
    public String getS() {
        return SUBSYSTEM_DIC;
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
        if (dics != null && !dics.isEmpty()) {
            object.add("dics", new Gson().toJsonTree(dics));
        }
        if (values != null && !values.isEmpty()) {
            object.add("values", new Gson().toJsonTree(values));
        }
        return object;
    }
}
