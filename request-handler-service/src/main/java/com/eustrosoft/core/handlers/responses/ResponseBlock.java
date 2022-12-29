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
        json.addItem("subsystem", getSubsystem());
        json.addItem("status", getStatus());
        json.addItem("qid", getQId());
        json.addItem("err_code", getErrCode());
        json.addItem("err_msg", getErrMsg());
        return json;
    }
}
