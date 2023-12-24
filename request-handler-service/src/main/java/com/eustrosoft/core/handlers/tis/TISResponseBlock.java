/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.tis;

import com.eustrosoft.core.handlers.responses.BasicResponse;
import com.eustrosoft.core.handlers.responses.ResponseLang;
import com.google.gson.JsonObject;

import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_TIS;

public class TISResponseBlock extends BasicResponse {
    private final String responseType;
    private String errMsg = "";
    private Short errCode = 0;

    public TISResponseBlock(String responseType) {
        this.responseType = responseType;
    }

    @Override
    public String getS() {
        return SUBSYSTEM_TIS;
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
        return super.toJsonObject();
    }
}
