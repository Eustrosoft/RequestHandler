package com.eustrosoft.core.handlers.requests;

import javax.servlet.http.HttpServletRequest;

public interface RequestBlock {
    String getS();

    String getR();

    HttpServletRequest getHttpRequest();
}
