package com.eustrosoft.core.handlers.responses;

public interface ResponseBlock {
    String getSubsystem();

    String getRequest();

    Long getStatus();

    Long getQId();

    Short getErrCode();

    String getErrMsg();
}
