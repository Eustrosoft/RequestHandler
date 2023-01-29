package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.handlers.responses.ResponseBlock;

import static com.eustrosoft.core.Constants.REQUEST_FILE_UPLOAD;
import static com.eustrosoft.core.Constants.SUBSYSTEM_FILE;

public class FileResponseBlock implements ResponseBlock {
    private String errMsg;

    public FileResponseBlock(String answer) {
        this.errMsg = answer;
    }

    @Override
    public String getSubsystem() {
        return SUBSYSTEM_FILE;
    }

    @Override
    public String getRequest() {
        return REQUEST_FILE_UPLOAD;
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
