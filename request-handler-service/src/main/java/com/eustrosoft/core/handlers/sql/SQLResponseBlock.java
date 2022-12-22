package com.eustrosoft.core.handlers.sql;

import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.tools.QJson;

public class SQLResponseBlock implements ResponseBlock {
    private QJson response;

    private String errMsg;

    public SQLResponseBlock(String answer) {
        response = new QJson();
        response.setItem("status", getStatus());
        response.setItem("qid", getQId());
        response.setItem("err_code", getErrCode());
        response.setItem("err_msg", answer); // TODO
    }

    @Override
    public String getSubsystem() {
        return "sql";
    }

    @Override
    public String getRequest() {
        return "sql";
    }

    @Override
    public Long getStatus() {
        return 200L;
    }

    @Override
    public Long getQId() {
        return 723153492L;
    }

    @Override
    public Short getErrCode() {
        return 0;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
