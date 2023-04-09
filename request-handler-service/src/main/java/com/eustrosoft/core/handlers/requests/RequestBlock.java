package com.eustrosoft.core.handlers.requests;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestBlock {
    String getS();

    String getR();

    HttpServletRequest getHttpRequest();

    HttpServletResponse getHttpResponse();
}
