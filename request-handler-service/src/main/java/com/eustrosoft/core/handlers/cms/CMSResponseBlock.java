package com.eustrosoft.core.handlers.cms;

import com.eustrosoft.core.handlers.responses.BasicResponse;
import com.eustrosoft.core.handlers.responses.ResponseLang;
import com.eustrosoft.datasource.sources.model.CMSObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import static com.eustrosoft.core.Constants.SUBSYSTEM_CMS;

public final class CMSResponseBlock extends BasicResponse {
    private String errMsg = "";
    private Short errCode = 0;
    private String responseType;
    private List<CMSObject> content;

    public CMSResponseBlock() {
    }

    public List<CMSObject> getContent() {
        return content;
    }

    public void setContent(List<CMSObject> content) {
        this.content = content;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    @Override
    public String getS() {
        return SUBSYSTEM_CMS;
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
        object.add("content", new Gson().toJsonTree(content));
        return object;
    }
}
