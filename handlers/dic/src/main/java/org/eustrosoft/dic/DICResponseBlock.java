/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.dic;

import org.eustrosoft.dic.model.DIC;
import org.eustrosoft.spec.interfaces.JsonData;
import org.eustrosoft.spec.response.BasicResponseBlock;

import java.util.List;

public final class DICResponseBlock<T extends JsonData> extends BasicResponseBlock<T> {
    private String errMsg = "";
    private Short errCode = 0;
    private String responseType;
    private List<DIC> values;
    private List<String> dics;

    public void setE(int code) {
        errCode = (short) code;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public Short getErrCode() {
        return errCode;
    }

    public void setErrCode(Short errCode) {
        this.errCode = errCode;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public List<DIC> getValues() {
        return values;
    }

    public void setValues(List<DIC> values) {
        this.values = values;
    }

    public List<String> getDics() {
        return dics;
    }

    public void setDics(List<String> dics) {
        this.dics = dics;
    }
}
