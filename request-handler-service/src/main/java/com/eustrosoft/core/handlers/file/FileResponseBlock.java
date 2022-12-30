package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.tools.QJson;

public class FileResponseBlock implements ResponseBlock {
    private QJson response;

    private String errMsg;

    public FileResponseBlock(String answer) {
        this.errMsg = answer;
    }

    @Override
    public String getSubsystem() {
        return "file";
    }

    @Override
    public String getRequest() {
        return "upload"; // TODO make non static value
    }

    @Override
    public Long getStatus() {
        return 200L;
    }

    @Override
    public Long getQId() {
        return 723153493L;
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
