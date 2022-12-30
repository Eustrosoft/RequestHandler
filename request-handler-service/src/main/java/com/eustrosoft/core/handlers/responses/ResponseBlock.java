package com.eustrosoft.core.handlers.responses;

import com.eustrosoft.core.tools.QJson;

public interface ResponseBlock {
    String getSubsystem();

    String getRequest();

    Long getStatus();

    Long getQId();

    Short getErrCode();

    String getErrMsg();

    default QJson toJson() throws Exception {
        QJson json = new QJson();
        json.addItem("subsystem", String.valueOf(getSubsystem()));
        json.addItem("status", String.valueOf(getStatus()));
        json.addItem("qid", String.valueOf(getQId()));
        json.addItem("err_code", String.valueOf(getErrCode()));
        json.addItem("err_msg", String.valueOf(getErrMsg()));
        return json;
    }
}
