package com.eustrosoft.core.handlers.requests;

import javax.servlet.http.HttpServletRequest;

public interface RequestBlock {
    String getSubsystem();

    String getRequest();

    HttpServletRequest getHttpRequest();
}
